package org.example.echo01.auth.security;

import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=sa",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "application.security.jwt.expiration=60000",
    "application.security.jwt.refresh-token.expiration=604800000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void whenGeneratingToken_thenSuccess() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void whenExtractingUsername_thenSuccess() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        
        assertEquals("test@example.com", username);
    }

    @Test
    void whenTokenIsExpired_thenNotValid() {
        // Create a user that will be used to generate an expired token
        UserDetails expiredUser = User.builder()
                .firstname("Expired")
                .lastname("User")
                .email("expired@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Generate token with immediate expiration
        String token = jwtService.buildToken(new HashMap<>(), expiredUser, -1000); // expired 1 second ago
        
        assertFalse(jwtService.isTokenValid(token, expiredUser));
    }

    @Test
    void whenGeneratingRefreshToken_thenDifferentFromAccessToken() {
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        assertNotEquals(accessToken, refreshToken);
    }

    @Test
    void whenTokenForDifferentUser_thenNotValid() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails otherUser = User.builder()
                .firstname("Other")
                .lastname("User")
                .email("other@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void whenExtractingClaims_thenSuccess() {
        String token = jwtService.generateToken(userDetails);
        
        Date issuedAt = jwtService.extractClaim(token, claims -> claims.getIssuedAt());
        Date expiration = jwtService.extractClaim(token, claims -> claims.getExpiration());
        
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
    }
} 