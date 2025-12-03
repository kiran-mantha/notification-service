package com.enterprise.notification.service.dispatcher;

import com.enterprise.notification.domain.entity.DeliveryLog;
import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.repository.DeliveryLogRepository;
import com.enterprise.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InAppDispatcher implements NotificationDispatcher {
    
    private final NotificationRepository notificationRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    
    @Override
    public void dispatch(Notification notification) {
        try {
            // In-app notifications are stored in DB and retrieved via API
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            logDelivery(notification, "In-app notification stored successfully", 200);
            
            log.info("In-app notification stored for user {}", notification.getUserId());
        } catch (Exception e) {
            log.error("Failed to store in-app notification", e);
            handleFailure(notification, e);
        }
    }
    
    @Override
    public boolean supports(Notification notification) {
        return notification.getType() == NotificationType.IN_APP;
    }
    
    private void handleFailure(Notification notification, Exception e) {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setRetries(notification.getRetries() + 1);
        notificationRepository.save(notification);
        
        logDelivery(notification, e.getMessage(), 500);
    }
    
    private void logDelivery(Notification notification, String response, int statusCode) {
        DeliveryLog log = DeliveryLog.builder()
            .notificationId(notification.getId())
            .requestPayload(notification.getPayload().toString())
            .responsePayload(response)
            .statusCode(statusCode)
            .attempt(notification.getRetries() + 1)
            .build();
        
        deliveryLogRepository.save(log);
    }
}
