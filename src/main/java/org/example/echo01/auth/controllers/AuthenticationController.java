package org.example.echo01.auth.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.request.VerifyOTPRequest;
import org.example.echo01.auth.dto.request.ResendOTPRequest;
import org.example.echo01.auth.dto.request.RefreshTokenRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.AuthenticationService;
import org.example.echo01.auth.services.EmailService;
import org.example.echo01.auth.services.JwtService;
import org.example.echo01.auth.services.OTPService;
import org.example.echo01.common.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    private final OTPService otpService;
    private final EmailService emailService;
    private final JwtService jwtService;

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
            @Valid @RequestBody VerifyOTPRequest request
    ) {
        log.debug("Received OTP verification request - Email: {}, Code: {}", request.getEmail(), request.getCode());
        boolean verified = otpService.verifyOTP(request.getEmail(), request.getCode());
        
        // Get user after verification
        User user = authenticationService.getUserByEmail(request.getEmail());
        String accessToken = jwtService.generateToken(user);
        
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .user(UserResponse.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .bio(user.getBio())
                    .role(user.getRole())
                    .enabled(user.isEnabled())
                    .emailVerified(user.isEmailVerified())
                    .build())
                .build());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthenticationResponse> resendOTP(
            @Valid @RequestBody ResendOTPRequest request
    ) {
        User user = authenticationService.getUserByEmail(request.getEmail());
        otpService.generateAndSendOTP(user);
        String accessToken = jwtService.generateToken(user);
        
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .user(UserResponse.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .bio(user.getBio())
                    .role(user.getRole())
                    .enabled(user.isEnabled())
                    .emailVerified(user.isEmailVerified())
                    .build())
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
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest, request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authenticationService.logout(request, response);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(null)
                .user(null)
                .message("Logged out successfully")
                .build());
    }

    @PostMapping("/test-email")
    public ResponseEntity<AuthenticationResponse> testEmail(@RequestParam String email) {
        try {
            emailService.sendOtpEmail(email, "123456");
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(null)
                    .user(null)
                    .build());
        } catch (MessagingException e) {
            throw new CustomException("Failed to send test email: " + e.getMessage());
        }
    }
} 