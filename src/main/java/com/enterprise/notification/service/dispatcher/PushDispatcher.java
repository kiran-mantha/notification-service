package com.enterprise.notification.service.dispatcher;

import com.enterprise.notification.domain.entity.DeliveryLog;
import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.repository.DeliveryLogRepository;
import com.enterprise.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushDispatcher implements NotificationDispatcher {
    
    private final NotificationRepository notificationRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    
    @Value("${notification.providers.push.server-key:}")
    private String serverKey;
    
    @SuppressWarnings("unused")
    @Override
    public void dispatch(Notification notification) {
        try {
            String deviceToken = (String) notification.getPayload().get("deviceToken");
            String title = (String) notification.getPayload().get("title");
            String body = (String) notification.getPayload().get("body");
            
            // Simulated FCM push (integrate with Firebase SDK in production)
            log.info("Sending push notification to device {} with title: {}", deviceToken, title);
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            logDelivery(notification, "Push notification sent successfully", 200);
            
        } catch (Exception e) {
            log.error("Failed to send push notification", e);
            handleFailure(notification, e);
        }
    }
    
    @Override
    public boolean supports(Notification notification) {
        return notification.getType() == NotificationType.PUSH;
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
