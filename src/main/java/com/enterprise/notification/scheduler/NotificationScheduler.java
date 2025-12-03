package com.enterprise.notification.scheduler;

import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.domain.enums.NotificationStatus;
import com.enterprise.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    
    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void processScheduledNotifications() {
        List<Notification> scheduledNotifications = notificationRepository
            .findByStatusAndScheduledAtBefore(NotificationStatus.PENDING, LocalDateTime.now());
        
        log.info("Processing {} scheduled notifications", scheduledNotifications.size());
        
        scheduledNotifications.forEach(notification -> {
            kafkaTemplate.send("notifications.request", notification);
        });
    }
}
