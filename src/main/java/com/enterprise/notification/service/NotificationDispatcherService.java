package com.enterprise.notification.service;

import com.enterprise.notification.domain.entity.Notification;
import com.enterprise.notification.service.dispatcher.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {
    
    private final List<NotificationDispatcher> dispatchers;
    
    public void dispatch(Notification notification) {
        dispatchers.stream()
            .filter(dispatcher -> dispatcher.supports(notification))
            .findFirst()
            .ifPresentOrElse(
                dispatcher -> dispatcher.dispatch(notification),
                () -> log.error("No dispatcher found for notification type: {}", notification.getType())
            );
    }
}
