package org.example.echo01.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.common.audit.Auditable;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;  // Stores the hashed token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private String deviceId;  // To track device/browser

    @Column(nullable = false)
    private boolean used;     // For one-time use enforcement

    @Column(nullable = false)
    private boolean revoked;  // For manual revocation

    @Column
    private String replacedByToken;  // For token rotation tracking

    public boolean isValid() {
        return !used && !revoked && expiryDate.isAfter(Instant.now());
    }
} 