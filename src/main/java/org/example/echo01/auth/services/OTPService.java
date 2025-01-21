package org.example.echo01.auth.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.OTP;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.OTPRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPService.class);

    private final OTPRepository otpRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Transactional
    public void generateAndSendOTP(User user) {
        String otpCode = generateOTP();
        log.debug("Generated OTP code: {} for user: {}", otpCode, user.getEmail());
        
        OTP otp = OTP.builder()
                .code(otpCode)
                .email(user.getEmail())
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();
        
        OTP savedOtp = otpRepository.save(otp);
        log.debug("Saved OTP entity: {}", savedOtp);
        
        try {
            emailService.sendOtpEmail(user.getEmail(), otpCode);
            log.debug("OTP email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send OTP email", e);
            throw new CustomException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Transactional
    public boolean verifyOTP(String email, String code) {
        log.debug("Starting OTP verification - Email: {}, Code: {}", email, code);
        
        // First, let's check if there are any OTPs for this email
        Optional<OTP> latestOtp = otpRepository.findFirstByEmailOrderByCreatedAtDesc(email);
        latestOtp.ifPresent(otp -> log.debug("Latest OTP found for email {}: {}", email, otp));
        
        // Now try to find the specific OTP
        OTP otp = otpRepository.findFirstByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(email, code)
                .orElseThrow(() -> {
                    log.debug("No valid OTP found for email: {} and code: {}", email, code);
                    return new CustomException("Invalid OTP");
                });

        log.debug("Found matching OTP: {}", otp);

        if (otp.isExpired()) {
            log.debug("OTP has expired. Expiry date: {}, Current time: {}", otp.getExpiryDate(), LocalDateTime.now());
            throw new CustomException("OTP has expired");
        }

        log.debug("OTP verified successfully for email: {}", email);
        
        User user = otp.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        User savedUser = userRepository.save(user);
        log.debug("Updated user status - enabled: {}, emailVerified: {}", savedUser.isEnabled(), savedUser.isEmailVerified());
        
        otp.setUsed(true);
        otpRepository.save(otp);
        log.debug("Marked OTP as used");
        
        return true;
    }

    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
} 