package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.response.TokenResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.TokenType;

import java.util.List;

public interface ITokenService {
    TokenResponse createToken(User user, String jwtToken, TokenType tokenType);
    void revokeAllUserTokens(User user);
    void saveUserToken(User user, String jwtToken);
    List<TokenResponse> findAllValidTokensByUser(Long userId);
    void revokeToken(String token);
} 