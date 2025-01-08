package org.example.echo01.auth.services;

import org.example.echo01.auth.dto.request.UpdateProfileRequest;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.exceptions.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UpdateProfileRequest updateProfileRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .bio("Test bio")
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();

        updateProfileRequest = UpdateProfileRequest.builder()
                .firstname("Updated John")
                .lastname("Updated Doe")
                .bio("Updated bio")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john@example.com");
    }

    @Test
    void getCurrentUserProfile_ShouldReturnUserProfile() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getCurrentUserProfile();

        assertNotNull(response);
        assertEquals(mockUser.getId(), response.getId());
        assertEquals(mockUser.getFirstname(), response.getFirstname());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void updateProfile_ShouldUpdateAndReturnUserProfile() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.updateProfile(updateProfileRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(mockUser.getId(), response.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUserRole_ShouldUpdateAndReturnUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.updateUserRole(1L, Role.ADMIN);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserStatus_ShouldUpdateAndReturnUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.updateUserStatus(1L, false);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
} 