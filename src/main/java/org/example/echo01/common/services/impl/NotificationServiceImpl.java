package org.example.echo01.common.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.auth.services.UserService;
import org.example.echo01.common.dto.request.CreateNotificationRequest;
import org.example.echo01.common.dto.response.NotificationResponse;
import org.example.echo01.common.entities.Notification;
import org.example.echo01.common.mapper.NotificationMapper;
import org.example.echo01.common.repositories.NotificationRepository;
import org.example.echo01.common.services.INotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Notification notification = notificationMapper.toEntity(request);
        notification.setUser(user);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public Page<NotificationResponse> getCurrentUserNotifications(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return getUserNotifications(currentUser.getId(), pageable);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));

        User currentUser = userService.getCurrentUser();
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }
} 