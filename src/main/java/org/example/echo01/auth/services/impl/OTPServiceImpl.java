package org.example.echo01.auth.services.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.response.OTPResponse;
import org.example.echo01.auth.entities.OTP;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.mapper.OTPMapper;
import org.example.echo01.auth.repositories.OTPRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.auth.services.EmailService;
import org.example.echo01.auth.services.IOTPService;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements IOTPService {

    private final OTPRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OTPMapper otpMapper;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 30;
    
    // Store OTP request counts with email as key
    private final ConcurrentHashMap<String, AtomicInteger> otpRequestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lastOTPRequestTime = new ConcurrentHashMap<>();

    @Override
    public boolean hasExceededOTPLimit(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequestTime = lastOTPRequestTime.get(email);
        
        // Reset counter if outside window
        if (lastRequestTime == null || lastRequestTime.plusMinutes(RATE_LIMIT_WINDOW_MINUTES).isBefore(now)) {
            otpRequestCounts.remove(email);
            lastOTPRequestTime.remove(email);
            return false;
        }
        
        AtomicInteger count = otpRequestCounts.get(email);
        return count != null && count.get() >= MAX_OTP_ATTEMPTS;
    }

    @Override
    @Transactional
    public OTPResponse generateAndSendOTP(User user) {
        String email = user.getEmail();
        
        // Update rate limiting data
        otpRequestCounts.computeIfAbsent(email, k -> new AtomicInteger(0)).incrementAndGet();
        lastOTPRequestTime.put(email, LocalDateTime.now());
        
        String code = generateOTPCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OTP otp = OTP.builder()
                .code(code)
                .email(email)
                .expiryDate(expiryDate)
                .used(false)
                .user(user)
                .build();

        otp = otpRepository.save(otp);
        try {
            emailService.sendOtpEmail(email, code);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
        
        return otpMapper.toResponse(otp);
    }

    @Override
    @Transactional
    public boolean verifyOTP(String email, String code) {
        OTP otp = otpRepository.findFirstByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(email, code)
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

    @Override
    @Transactional
    public void deleteAllUserOTPs(User user) {
        otpRepository.deleteAllByUser(user);
    }

    @Override
    public OTPResponse getLatestOTP(String email) {
        return otpRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .map(otpMapper::toResponse)
                .orElse(null);
    }

    private String generateOTPCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 