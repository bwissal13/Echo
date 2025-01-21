package org.example.echo01.auth.controllers;

import org.example.echo01.auth.dto.request.UpdateProfileRequest;
import org.example.echo01.auth.dto.response.UserResponse;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.enums.Role;
import org.example.echo01.auth.services.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private IUserService userService;

    private UserController userController;

    private UserResponse mockUserResponse;
    private UpdateProfileRequest updateProfileRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);

        mockUserResponse = UserResponse.builder()
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
    }

    @Test
    void getCurrentUserProfile_ShouldReturnUserResponse() {
        when(userService.getCurrentUserProfile()).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = userController.getCurrentUserProfile();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUserResponse, response.getBody());
        verify(userService, times(1)).getCurrentUserProfile();
    }

    @Test
    void updateProfile_ShouldUpdateAndReturnUserProfile() {
        when(userService.updateProfile(updateProfileRequest)).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = userController.updateProfile(updateProfileRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUserResponse, response.getBody());
        verify(userService, times(1)).updateProfile(updateProfileRequest);
    }

    @Test
    void getUserById_ShouldReturnUserProfile() {
        when(userService.getUserById(1L)).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUserResponse, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void updateUserRole_ShouldUpdateAndReturnUserProfile() {
        when(userService.updateUserRole(1L, Role.ADMIN)).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = userController.updateUserRole(1L, Role.ADMIN);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUserResponse, response.getBody());
        verify(userService, times(1)).updateUserRole(1L, Role.ADMIN);
    }

    @Test
    void updateUserStatus_ShouldUpdateAndReturnUserProfile() {
        when(userService.updateUserStatus(1L, false)).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = userController.updateUserStatus(1L, false);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockUserResponse, response.getBody());
        verify(userService, times(1)).updateUserStatus(1L, false);
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User deleted successfully", responseBody.get("message"));
        assertEquals("true", responseBody.get("success"));
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Arrange
        Page<UserResponse> mockPage = new PageImpl<>(Collections.singletonList(createMockUserResponse()));
        when(userService.getAllUsers(0, 10, "")).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(0, 10, "");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPage, response.getBody());
    }

    private UserResponse createMockUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();
    }
} 