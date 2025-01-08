package org.example.echo01.auth.repositories;

import org.example.echo01.auth.entities.RefreshToken;
import org.example.echo01.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findAllByUserAndUsedFalseAndRevokedFalse(User user);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = ?1 AND rt.deviceId = ?2 AND rt.used = false AND rt.revoked = false")
    Optional<RefreshToken> findValidTokenForUserAndDevice(User user, String deviceId);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.used = true WHERE rt.token = ?1")
    void markTokenAsUsed(String token);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = ?1 AND rt.deviceId = ?2")
    void revokeAllUserTokensForDevice(User user, String deviceId);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < ?1")
    void deleteAllExpiredTokens(Instant now);
} 