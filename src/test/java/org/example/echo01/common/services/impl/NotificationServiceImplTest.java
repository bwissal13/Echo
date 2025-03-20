package org.example.echo01.common.services.impl;

import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.common.dto.request.CreateNotificationRequest;
import org.example.echo01.common.dto.response.NotificationResponse;
import org.example.echo01.common.entities.Notification;
import org.example.echo01.common.enums.NotificationType;
import org.example.echo01.common.mappers.NotificationMapper;
import org.example.echo01.common.repositories.NotificationRepository;
import org.example.echo01.common.services.INotificationService;
import org.example.echo01.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .firstname("User")
                .lastname("Test")
                .email("user@example.com")
                .build();

        notification = Notification.builder()
                .id(1L)
                .user(user)
                .content("Test Notification")
                .build();
    }

    @Test
    void createNotification_ShouldReturnNotificationResponse() {
        CreateNotificationRequest request = new CreateNotificationRequest(NotificationType.LIKE, "Test Notification", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationMapper.toEntity(any(CreateNotificationRequest.class))).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toResponse(any(Notification.class))).thenReturn(new NotificationResponse(
                1L, NotificationType.LIKE, "Test Notification", 1L, "user@example.com", LocalDateTime.now()
        ));

        NotificationResponse response = notificationService.createNotification(request);

        assertNotNull(response);
        assertEquals("Test Notification", response.getContent());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationById_ShouldReturnNotificationResponse() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationMapper.toResponse(any(Notification.class))).thenReturn(new NotificationResponse(
                1L, NotificationType.LIKE, "Test Notification", 1L, "user@example.com", LocalDateTime.now()
        ));

        NotificationResponse response = notificationService.getNotificationById(1L);

        assertNotNull(response);
        assertEquals("Test Notification", response.getContent());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> notificationService.getNotificationById(1L));
    }
} 