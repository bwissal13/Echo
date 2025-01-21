package org.example.echo01.auth.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.dto.response.TokenResponse;
import org.example.echo01.auth.entities.Token;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.TokenType;
import org.example.echo01.auth.mapper.TokenMapper;
import org.example.echo01.auth.repositories.TokenRepository;
import org.example.echo01.auth.services.ITokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService {

    private final TokenRepository tokenRepository;
    private final TokenMapper tokenMapper;

    @Override
    @Transactional
    public TokenResponse createToken(User user, String jwtToken, TokenType tokenType) {
        Token token = Token.create(jwtToken, tokenType, user);
        token = tokenRepository.save(token);
        return tokenMapper.toResponse(token);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    @Transactional
    public void saveUserToken(User user, String jwtToken) {
        createToken(user, jwtToken, TokenType.ACCESS);
    }

    @Override
    public List<TokenResponse> findAllValidTokensByUser(Long userId) {
        return tokenRepository.findAllValidTokenByUser(userId)
                .stream()
                .map(tokenMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        tokenRepository.findByToken(token)
                .ifPresent(t -> {
                    t.setExpired(true);
                    t.setRevoked(true);
                    tokenRepository.save(t);
                });
    }
} 