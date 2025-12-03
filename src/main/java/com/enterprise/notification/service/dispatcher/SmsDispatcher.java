package com.enterprise.notification.service.dispatcher;

import com.enterprise.notification.domain.entity.DeliveryLog;
import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.repository.DeliveryLogRepository;
import com.enterprise.notification.repository.NotificationRepository;
import com.enterprise.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsDispatcher implements NotificationDispatcher {
    
    private final TemplateService templateService;
    private final NotificationRepository notificationRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    
    @Value("${notification.providers.sms.account-sid:}")
    private String accountSid;
    
    @Value("${notification.providers.sms.auth-token:}")
    private String authToken;
    
    @Value("${notification.providers.sms.from-number:}")
    private String fromNumber;
    
    @Override
    public void dispatch(Notification notification) {
        try {
            String to = (String) notification.getPayload().get("to");
            String message;
            
            if (notification.getTemplateId() != null) {
                message = templateService.renderTemplate(notification.getTemplateId(), notification.getPayload());
            } else {
                message = (String) notification.getPayload().get("message");
            }
            
            // Simulated SMS sending (integrate with Twilio SDK in production)
            log.info("Sending SMS to {} with message: {}", to, message);
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            logDelivery(notification, "SMS sent successfully", 200);
            
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            handleFailure(notification, e);
        }
    }
    
    @Override
    public boolean supports(Notification notification) {
        return notification.getType() == NotificationType.SMS;
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
