package org.example.echo01.auth.services;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.OTP;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.OTPRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPRepository otpRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Transactional
    public void generateAndSendOTP(User user) {
        String otpCode = generateOTP();
        
        OTP otp = OTP.builder()
                .code(otpCode)
                .email(user.getEmail())
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();
        
        otpRepository.save(otp);
        
        try {
            emailService.sendOtpEmail(user.getEmail(), otpCode);
        } catch (MessagingException e) {
            throw new CustomException("Failed to send OTP email");
        }
    }

    @Transactional
    public boolean verifyOTP(String email, String code) {
        OTP otp = otpRepository.findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new CustomException("Invalid OTP"));

        if (otp.isExpired()) {
            throw new CustomException("OTP has expired");
        }

        User user = otp.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        
        otp.setUsed(true);
        otpRepository.save(otp);
        
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