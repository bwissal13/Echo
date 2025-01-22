package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.response.RefreshTokenResponse;
import org.example.echo01.auth.entities.User;

import java.util.List;

public interface IRefreshTokenService {
    RefreshTokenResponse createRefreshToken(User user, String deviceId);
    RefreshTokenResponse verifyRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserRefreshTokens(User user);
    List<RefreshTokenResponse> findAllValidTokensByUser(Long userId);
    void revokeByDeviceId(String deviceId);
} 