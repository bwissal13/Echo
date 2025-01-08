package org.example.echo01.auth.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.request.VerifyOTPRequest;
import org.example.echo01.auth.dto.request.ResendOTPRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.AuthenticationService;
import org.example.echo01.auth.services.EmailService;
import org.example.echo01.auth.services.OTPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OTPService otpService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.register(request, response, httpRequest));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOTP(
            @RequestBody VerifyOTPRequest request
    ) {
        boolean verified = otpService.verifyOTP(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .success(verified)
                .message("Email verified successfully")
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
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.login(request, httpRequest, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authenticationService.logout(request, response);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .success(true)
                .message("Logged out successfully")
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