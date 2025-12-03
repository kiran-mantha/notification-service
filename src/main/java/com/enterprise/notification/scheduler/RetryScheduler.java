package com.enterprise.notification.scheduler;

import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryScheduler {
    
    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    
    @Value("${notification.retry.max-attempts}")
    private int maxRetries;
    
    @Scheduled(fixedDelay = 120000) // Run every 2 minutes
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findAll().stream()
            .filter(n -> n.getStatus() == NotificationStatus.FAILED)
            .filter(n -> n.getRetries() < maxRetries)
            .filter(n -> shouldRetry(n))
            .toList();
        
        log.info("Retrying {} failed notifications", failedNotifications.size());
        
        failedNotifications.forEach(notification -> {
            notification.setStatus(NotificationStatus.RETRYING);
            notificationRepository.save(notification);
            kafkaTemplate.send("notifications.retry", notification);
        });
    }
    
    private boolean shouldRetry(Notification notification) {
        long minutesSinceLastUpdate = java.time.Duration.between(
            notification.getUpdatedAt(), 
            LocalDateTime.now()
        ).toMinutes();
        
        // Exponential backoff: 1, 2, 5, 10, 20 minutes
        long[] backoffMinutes = {1, 2, 5, 10, 20};
        int retryIndex = Math.min(notification.getRetries(), backoffMinutes.length - 1);
        
        return minutesSinceLastUpdate >= backoffMinutes[retryIndex];
    }
}
