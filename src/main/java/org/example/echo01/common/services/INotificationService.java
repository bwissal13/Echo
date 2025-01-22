package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.CreateNotificationRequest;
import org.example.echo01.common.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService {
    NotificationResponse createNotification(CreateNotificationRequest request);
    NotificationResponse getNotificationById(Long id);
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);
    Page<NotificationResponse> getCurrentUserNotifications(Pageable pageable);
    void deleteNotification(Long id);
} 