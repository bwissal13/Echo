package org.example.echo01.auth.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.request.RefreshTokenRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.entities.RefreshToken;
import org.example.echo01.auth.enums.Role;
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
    private final ITokenService tokenService;
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
        tokenService.saveUserToken(savedUser, accessToken);
        
        // Generate device ID for refresh token
        String deviceId = generateDeviceId(httpRequest);
        
        // Create and set refresh token in HTTP-only cookie
        refreshTokenService.createAndSetRefreshToken(savedUser, deviceId, response);
        
        // Generate and send OTP
        otpService.generateAndSendOTP(savedUser);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .user(UserResponse.builder()
                    .id(savedUser.getId())
                    .firstname(savedUser.getFirstname())
                    .lastname(savedUser.getLastname())
                    .email(savedUser.getEmail())
                    .bio(savedUser.getBio())
                    .role(savedUser.getRole())
                    .enabled(savedUser.isEnabled())
                    .emailVerified(savedUser.isEmailVerified())
                    .build())
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
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, accessToken);
        
        // Generate device ID from user agent or other request properties
        String deviceId = generateDeviceId(httpRequest);
        
        // Create and set refresh token in HTTP-only cookie
        refreshTokenService.createAndSetRefreshToken(user, deviceId, response);
        
        return AuthenticationResponse.builder()
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
                .build();
    }

    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest request, HttpServletResponse response) {
        // Try to get refresh token from request body first, then from cookie
        String refreshToken = null;
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null) {
            refreshToken = refreshTokenRequest.getRefreshToken();
        } else {
            refreshToken = refreshTokenService.extractRefreshTokenFromCookie(request);
        }
        
        if (refreshToken == null) {
            throw new CustomException("Refresh token not found in cookie or request body");
        }

        try {
            // Validate refresh token and get user
            RefreshToken validRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);
            User user = validRefreshToken.getUser();

            // Generate device ID
            String deviceId = generateDeviceId(request);

            // Rotate refresh token and get new one
            refreshTokenService.rotateRefreshToken(refreshToken, user, deviceId, response);

            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);
            tokenService.revokeAllUserTokens(user);
            tokenService.saveUserToken(user, newAccessToken);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
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
            tokenService.revokeToken(accessToken);
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