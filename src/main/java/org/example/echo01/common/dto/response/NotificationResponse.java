package org.example.echo01.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String content;
    private Long userId;
    private String userEmail;
    private LocalDateTime date;
} 