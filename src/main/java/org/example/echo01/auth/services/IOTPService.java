package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.response.OTPResponse;
import org.example.echo01.auth.entities.User;

public interface IOTPService {
    OTPResponse generateAndSendOTP(User user);
    boolean verifyOTP(String email, String code);
    void deleteAllUserOTPs(User user);
    OTPResponse getLatestOTP(String email);
    boolean hasExceededOTPLimit(String email);
} 