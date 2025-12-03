package com.enterprise.notification.dto;

import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private String userId;
    private NotificationType type;
    private NotificationStatus status;
    private Long templateId;
    private Map<String, Object> payload;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer retries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
