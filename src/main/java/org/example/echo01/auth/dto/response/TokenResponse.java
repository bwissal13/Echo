package org.example.echo01.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.auth.enums.TokenType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private Long id;
    private TokenType tokenType;
    private boolean revoked;
    private boolean expired;
    private Long userId;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
} 