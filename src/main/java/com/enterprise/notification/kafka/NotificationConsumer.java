package com.enterprise.notification.kafka;

import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.service.NotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    
    private final NotificationDispatcherService dispatcherService;
    
    @KafkaListener(topics = "notifications.request", groupId = "notification-service")
    public void consumeNotificationRequest(Notification notification) {
        log.info("Received notification request: {}", notification.getId());
        dispatcherService.dispatch(notification);
    }
    
    @KafkaListener(topics = "notifications.retry", groupId = "notification-service")
    public void consumeRetryRequest(Notification notification) {
        log.info("Received retry request for notification: {}", notification.getId());
        dispatcherService.dispatch(notification);
    }
}
