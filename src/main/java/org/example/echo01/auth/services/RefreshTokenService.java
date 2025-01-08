package org.example.echo01.auth.services;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.RefreshToken;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.RefreshTokenRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenDurationMs;

    @Value("${application.security.jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;

    @PostConstruct
    public void validateConfiguration() {
        Assert.isTrue(refreshTokenDurationMs > 0, "Refresh token duration must be positive");
        Assert.hasText(refreshTokenCookieName, "Refresh token cookie name must not be empty");
    }

    @Transactional
    public void createAndSetRefreshToken(User user, String deviceId, HttpServletResponse response) {
        // Generate a new refresh token
        String tokenValue = UUID.randomUUID().toString();
        
        // Hash the token before storing
        String hashedToken = passwordEncoder.encode(tokenValue);
        
        // Create refresh token entity
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(hashedToken)
                .deviceId(deviceId)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .used(false)
                .revoked(false)
                .build();
        
        // Save to database
        refreshTokenRepository.save(refreshToken);
        
        // Create HTTP-only cookie
        Cookie refreshTokenCookie = new Cookie(refreshTokenCookieName, tokenValue);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // for HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (refreshTokenDurationMs / 1000));
        
        // Add cookie to response
        response.addCookie(refreshTokenCookie);
    }

    @Transactional
    public String rotateRefreshToken(String oldTokenValue, User user, String deviceId, HttpServletResponse response) {
        // Find and validate the old token
        RefreshToken oldToken = validateRefreshToken(oldTokenValue);
        
        if (!oldToken.getUser().equals(user) || !oldToken.getDeviceId().equals(deviceId)) {
            throw new CustomException("Invalid refresh token");
        }
        
        // Mark old token as used
        oldToken.setUsed(true);
        
        // Generate new token
        String newTokenValue = UUID.randomUUID().toString();
        String hashedNewToken = passwordEncoder.encode(newTokenValue);
        
        // Create new refresh token
        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(hashedNewToken)
                .deviceId(deviceId)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .used(false)
                .revoked(false)
                .build();
        
        // Save both tokens
        oldToken.setReplacedByToken(hashedNewToken);
        refreshTokenRepository.save(oldToken);
        refreshTokenRepository.save(newToken);
        
        // Update cookie
        Cookie refreshTokenCookie = new Cookie(refreshTokenCookieName, newTokenValue);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (refreshTokenDurationMs / 1000));
        response.addCookie(refreshTokenCookie);
        
        return newTokenValue;
    }

    public RefreshToken validateRefreshToken(String tokenValue) {
        // Find token in database
        return refreshTokenRepository.findByToken(tokenValue)
                .filter(RefreshToken::isValid)
                .orElseThrow(() -> new CustomException("Invalid refresh token"));
    }

    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new CustomException("Token not found"));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeAllUserTokensForDevice(User user, String deviceId) {
        refreshTokenRepository.revokeAllUserTokensForDevice(user, deviceId);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Scheduled(cron = "0 0 */1 * * *") // Run every hour
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
} 