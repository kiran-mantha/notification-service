package com.enterprise.notification.service.dispatcher;

import com.enterprise.notification.domain.entity.DeliveryLog;
import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.domain.enums.NotificationType;
import com.enterprise.notification.repository.DeliveryLogRepository;
import com.enterprise.notification.repository.NotificationRepository;
import com.enterprise.notification.service.TemplateService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailDispatcher implements NotificationDispatcher {
    
    private final JavaMailSender mailSender;
    private final TemplateService templateService;
    private final NotificationRepository notificationRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    
    @Value("${notification.providers.email.from}")
    private String fromEmail;
    
    @Override
    public void dispatch(Notification notification) {
        try {
            String to = (String) notification.getPayload().get("to");
            String subject = (String) notification.getPayload().get("subject");
            String body;
            
            if (notification.getTemplateId() != null) {
                body = templateService.renderTemplate(notification.getTemplateId(), notification.getPayload());
            } else {
                body = (String) notification.getPayload().get("body");
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            
            mailSender.send(message);
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            logDelivery(notification, "Email sent successfully", 200);
            
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email", e);
            handleFailure(notification, e);
        }
    }
    
    @Override
    public boolean supports(Notification notification) {
        return notification.getType() == NotificationType.EMAIL;
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
