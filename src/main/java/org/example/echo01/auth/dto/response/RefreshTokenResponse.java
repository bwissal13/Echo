package org.example.echo01.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private Instant expiryDate;
    private String deviceId;
    private boolean used;
    private boolean revoked;
    private String replacedByToken;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
} 