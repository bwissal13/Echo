package org.example.echo01.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.enums.NotificationType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "User ID is required")
    private Long userId;
} 