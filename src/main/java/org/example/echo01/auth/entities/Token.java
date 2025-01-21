package org.example.echo01.auth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.echo01.auth.enums.TokenType;
import org.example.echo01.common.audit.Auditable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Token create(String token, TokenType tokenType, User user) {
        Token t = new Token();
        t.setToken(token);
        t.setTokenType(tokenType);
        t.setExpired(false);
        t.setRevoked(false);
        t.setUser(user);
        return t;
    }
} 