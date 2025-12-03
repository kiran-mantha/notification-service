package com.enterprise.notification.service;

import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.entity.UserPreference;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.dto.NotificationRequest;
import com.enterprise.notification.dto.NotificationResponse;
import com.enterprise.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserPreferenceService userPreferenceService;
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        if (!isNotificationAllowed(request.getUserId(), request.getType())) {
            throw new IllegalStateException("User has disabled " + request.getType() + " notifications");
        }
        
        Notification notification = Notification.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .status(request.getScheduledAt() != null ? NotificationStatus.PENDING : NotificationStatus.PENDING)
            .templateId(request.getTemplateId())
            .payload(request.getPayload())
            .scheduledAt(request.getScheduledAt())
            .retries(0)
            .build();
        
        notification = notificationRepository.save(notification);
        
        if (request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            kafkaTemplate.send("notifications.request", notification);
        }
        
        return mapToResponse(notification);
    }
    
    @Transactional
    public List<NotificationResponse> sendBulkNotifications(List<NotificationRequest> requests) {
        return requests.stream()
            .map(this::sendNotification)
            .toList();
    }
    
    public NotificationResponse getNotification(UUID id) {
        return notificationRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }
    
    public Page<NotificationResponse> getNotificationsByUser(String userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
            .map(this::mapToResponse);
    }
    
    public Page<NotificationResponse> searchNotifications(
        String userId, 
        NotificationStatus status, 
        NotificationType type,
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    ) {
        return notificationRepository.searchNotifications(userId, status, type, startDate, endDate, pageable)
            .map(this::mapToResponse);
    }
    
    @Transactional
    public void retryNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        notification.setStatus(NotificationStatus.RETRYING);
        notificationRepository.save(notification);
        
        kafkaTemplate.send("notifications.retry", notification);
    }
    
    @Transactional
    public void cancelNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        
        notification.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(notification);
    }
    
    private boolean isNotificationAllowed(String userId, NotificationType type) {
        UserPreference preference = userPreferenceService.getUserPreference(userId);
        
        return switch (type) {
            case EMAIL -> preference.getEmailEnabled();
            case SMS -> preference.getSmsEnabled();
            case PUSH -> preference.getPushEnabled();
            case IN_APP -> preference.getInAppEnabled();
            case WEBHOOK -> preference.getWebhookEnabled();
        };
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .type(notification.getType())
            .status(notification.getStatus())
            .templateId(notification.getTemplateId())
            .payload(notification.getPayload())
            .scheduledAt(notification.getScheduledAt())
            .sentAt(notification.getSentAt())
            .retries(notification.getRetries())
            .createdAt(notification.getCreatedAt())
            .updatedAt(notification.getUpdatedAt())
            .build();
    }
}
