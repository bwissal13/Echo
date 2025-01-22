package org.example.echo01.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.*;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.services.AuthenticationService;
import org.example.echo01.auth.services.EmailService;
import org.example.echo01.auth.services.JwtService;
import org.example.echo01.auth.services.IOTPService;
import org.example.echo01.common.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    private final IOTPService otpService;
    private final EmailService emailService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        log.debug("Processing registration request for email: {}", request.getEmail());
        try {
            AuthenticationResponse authResponse = authenticationService.register(request, response, httpRequest);
            log.info("Successfully registered user with email: {}", request.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (CustomException e) {
            log.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new CustomException("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthenticationResponse> verifyOTP(
            @Valid @RequestBody VerifyOTPRequest request
    ) {
        log.debug("Processing OTP verification request for email: {}", request.getEmail());
        try {
            boolean verified = otpService.verifyOTP(request.getEmail(), request.getCode());
            
            if (!verified) {
                log.warn("Invalid OTP attempt for email: {}", request.getEmail());
                throw new CustomException("Invalid or expired OTP");
            }
            
            User user = authenticationService.getUserByEmail(request.getEmail());
            String accessToken = jwtService.generateToken(user);
            
            log.info("Successfully verified OTP for user: {}", request.getEmail());
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
                    .message("Email verified successfully")
                    .build());
        } catch (CustomException e) {
            log.error("OTP verification failed for email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during OTP verification for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new CustomException("OTP verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthenticationResponse> resendOTP(
            @Valid @RequestBody ResendOTPRequest request
    ) {
        log.debug("Processing OTP resend request for email: {}", request.getEmail());
        try {
            User user = authenticationService.getUserByEmail(request.getEmail());
            
            if (otpService.hasExceededOTPLimit(user.getEmail())) {
                log.warn("OTP request limit exceeded for email: {}", request.getEmail());
                return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(AuthenticationResponse.builder()
                        .message("Too many OTP requests. Please try again later.")
                        .build());
            }
            
            otpService.generateAndSendOTP(user);
            
            log.info("Successfully resent OTP for user: {}", request.getEmail());
            return ResponseEntity.ok(AuthenticationResponse.builder()
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
                    .message("OTP sent successfully")
                    .build());
        } catch (CustomException e) {
            log.error("OTP resend failed for email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during OTP resend for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new CustomException("Failed to resend OTP: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        log.debug("Processing login request for email: {}", request.getEmail());
        try {
            AuthenticationResponse authResponse = authenticationService.login(request, httpRequest, response);
            log.info("Successfully logged in user: {}", request.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for email {}: {}", request.getEmail(), e.getMessage());
            throw new CustomException("Invalid credentials");
        } catch (CustomException e) {
            log.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new CustomException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.debug("Processing refresh token request");
        try {
            AuthenticationResponse authResponse = authenticationService.refreshToken(refreshTokenRequest, request, response);
            log.info("Successfully refreshed token");
            return ResponseEntity.ok(authResponse);
        } catch (CustomException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new CustomException("Token refresh failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.debug("Processing logout request");
        try {
            authenticationService.logout(request, response);
            log.info("Successfully logged out user");
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .message("Logged out successfully")
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during logout: {}", e.getMessage(), e);
            throw new CustomException("Logout failed: " + e.getMessage());
        }
    }
} 