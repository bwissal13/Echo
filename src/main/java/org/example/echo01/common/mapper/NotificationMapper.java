package org.example.echo01.common.mapper;

import org.example.echo01.common.dto.request.CreateNotificationRequest;
import org.example.echo01.common.dto.response.NotificationResponse;
import org.example.echo01.common.entities.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now())")
    Notification toEntity(CreateNotificationRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    NotificationResponse toResponse(Notification notification);
} 