package com.enterprise.notification.dto;

import com.enterprise.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class NotificationRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    private Long templateId;
    
    @NotNull(message = "Payload is required")
    private Map<String, Object> payload;
    
    private LocalDateTime scheduledAt;
}
