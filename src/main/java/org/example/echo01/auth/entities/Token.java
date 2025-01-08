package org.example.echo01.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.enums.TokenType;
import org.example.echo01.common.audit.Auditable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
@EqualsAndHashCode(callSuper = false)
public class Token extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean revoked;

    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
} 