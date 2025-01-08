package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.request.LoginRequest;
import org.example.echo01.auth.dto.request.RegisterRequest;
import org.example.echo01.auth.dto.response.AuthenticationResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.repositories.TokenRepository;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .bio("Test bio")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("encodedPassword")
                .bio("Test bio")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void register_WithValidData_ShouldSucceed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        when(tokenRepository.save(any())).thenReturn(null);

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(tokenRepository).save(any());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(CustomException.class,
                () -> authenticationService.register(registerRequest));

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, jwtService, tokenRepository);
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(new ArrayList<>());
        when(tokenRepository.save(any())).thenReturn(null);

        AuthenticationResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(tokenRepository).findAllValidTokenByUser(user.getId());
        verify(tokenRepository).save(any());
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> authenticationService.login(loginRequest));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(jwtService, tokenRepository);
    }
} 