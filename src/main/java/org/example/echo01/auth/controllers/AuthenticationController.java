package org.example.echo01.auth.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.request.VerifyOTPRequest;
import org.example.echo01.auth.dto.request.ResendOTPRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.AuthenticationService;
import org.example.echo01.auth.services.EmailService;
import org.example.echo01.auth.services.LogoutService;
import org.example.echo01.auth.services.OTPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;
    private final OTPService otpService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);
        response.setMessage("User registered successfully. Please verify your email.");
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOTP(
            @RequestBody VerifyOTPRequest request
    ) {
        boolean isValid = otpService.verifyOTP(request.getEmail(), request.getCode());
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .success(isValid)
                .message(isValid ? "Email verified successfully" : "Invalid OTP")
                .build());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthenticationResponse> resendOTP(
            @RequestBody ResendOTPRequest request
    ) {
        User user = authenticationService.getUserByEmail(request.getEmail());
        otpService.generateAndSendOTP(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .success(true)
                .message("OTP sent successfully")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response = authenticationService.login(request);
        response.setMessage("Login successful");
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request
    ) {
        AuthenticationResponse response = authenticationService.refreshToken(request);
        response.setMessage("Token refreshed successfully");
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .success(false)
                    .message("No token found")
                    .build());
        }

        // Extract the token and revoke it
        logoutService.logout(request, null, null);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .success(true)
                .message("Logged out successfully")
                .accessToken(null)
                .refreshToken(null)
                .build());
    }

    @PostMapping("/test-email")
    public ResponseEntity<AuthenticationResponse> testEmail(@RequestParam String email) {
        try {
            emailService.sendOtpEmail(email, "123456");
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .success(true)
                    .message("Test email sent successfully")
                    .build());
        } catch (MessagingException e) {
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .success(false)
                    .message("Failed to send test email: " + e.getMessage())
                    .build());
        }
    }
} 