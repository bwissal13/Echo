package org.example.echo01.auth.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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

    @Mock
    private OTPService otpService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

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

        httpRequest = new MockHttpServletRequest();
        httpResponse = new MockHttpServletResponse();
    }

    @Test
    void register_WithValidData_ShouldSucceed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");
        doNothing().when(refreshTokenService).createAndSetRefreshToken(any(), any(), any());
        doNothing().when(otpService).generateAndSendOTP(any());

        AuthenticationResponse response = authenticationService.register(registerRequest, httpResponse, httpRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getFirstname(), response.getUser().getFirstname());
        assertEquals(user.getLastname(), response.getUser().getLastname());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getBio(), response.getUser().getBio());
        assertEquals(user.getRole(), response.getUser().getRole());
        assertEquals(user.isEnabled(), response.getUser().isEnabled());
        assertEquals(user.isEmailVerified(), response.getUser().isEmailVerified());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(refreshTokenService).createAndSetRefreshToken(any(), any(), any());
        verify(otpService).generateAndSendOTP(any());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(CustomException.class,
                () -> authenticationService.register(registerRequest, httpResponse, httpRequest));

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, jwtService, refreshTokenService, otpService);
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");
        when(tokenRepository.findAllValidTokenByUser(anyLong())).thenReturn(new ArrayList<>());
        doNothing().when(refreshTokenService).createAndSetRefreshToken(any(), any(), any());

        AuthenticationResponse response = authenticationService.login(loginRequest, httpRequest, httpResponse);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getFirstname(), response.getUser().getFirstname());
        assertEquals(user.getLastname(), response.getUser().getLastname());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getBio(), response.getUser().getBio());
        assertEquals(user.getRole(), response.getUser().getRole());
        assertEquals(user.isEnabled(), response.getUser().isEnabled());
        assertEquals(user.isEmailVerified(), response.getUser().isEmailVerified());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(jwtService).generateToken(user);
        verify(tokenRepository).findAllValidTokenByUser(user.getId());
        verify(refreshTokenService).createAndSetRefreshToken(any(), any(), any());
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> authenticationService.login(loginRequest, httpRequest, httpResponse));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(jwtService, tokenRepository, refreshTokenService);
    }

    @Test
    void login_WithUnverifiedAccount_ShouldThrowException() {
        User unverifiedUser = User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("encodedPassword")
                .bio("Test bio")
                .role(Role.USER)
                .enabled(false)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(unverifiedUser));

        assertThrows(org.springframework.security.authentication.DisabledException.class,
                () -> authenticationService.login(loginRequest, httpRequest, httpResponse));

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(authenticationManager, jwtService, tokenRepository, refreshTokenService);
    }
} 