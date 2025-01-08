package org.example.echo01.auth.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.entities.Token;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.enums.TokenType;
import org.example.echo01.auth.repositories.TokenRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;

    @Value("${application.security.jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response, HttpServletRequest httpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already exists");
        }

        // Create new user
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .role(Role.USER)
                .build();
        
        var savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(user);
        saveUserToken(savedUser, accessToken);
        
        // Generate device ID for refresh token
        String deviceId = generateDeviceId(httpRequest);
        
        // Create and set refresh token in HTTP-only cookie
        refreshTokenService.createAndSetRefreshToken(savedUser, deviceId, response);
        
        // Generate and send OTP
        otpService.generateAndSendOTP(savedUser);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .message("User registered successfully. Please check your email for OTP verification.")
                .success(true)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));
        
        if (!user.isEnabled()) {
            throw new org.springframework.security.authentication.DisabledException("Account is not verified. Please verify your email with the OTP sent during registration.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        
        // Generate device ID from user agent or other request properties
        String deviceId = generateDeviceId(httpRequest);
        
        // Create and set refresh token in HTTP-only cookie
        refreshTokenService.createAndSetRefreshToken(user, deviceId, response);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .message("Login successful")
                .success(true)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.ACCESS)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Extract refresh token from cookie
        String refreshToken = refreshTokenService.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new CustomException("Refresh token not found in cookie");
        }

        // Get current user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found"));

        // Generate device ID
        String deviceId = generateDeviceId(request);

        try {
            // Rotate refresh token and get new one
            refreshTokenService.rotateRefreshToken(refreshToken, user, deviceId, response);

            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, newAccessToken);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .message("Token refreshed successfully")
                    .success(true)
                    .build();
        } catch (CustomException e) {
            // If refresh token is invalid, force re-login
            throw new CustomException("Invalid refresh token. Please login again");
        }
    }

    private String generateDeviceId(HttpServletRequest request) {
        // Generate a unique device identifier based on request properties
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes()).toString();
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Get refresh token from cookie
        String refreshToken = refreshTokenService.extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            // Revoke refresh token
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        // Get access token and revoke it
        String accessToken = extractAccessToken(request);
        if (accessToken != null) {
            var token = tokenRepository.findByToken(accessToken);
            token.ifPresent(t -> {
                t.setExpired(true);
                t.setRevoked(true);
                tokenRepository.save(t);
            });
        }

        // Clear refresh token cookie
        Cookie cookie = new Cookie(refreshTokenCookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        // Clear security context
        SecurityContextHolder.clearContext();
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
} 