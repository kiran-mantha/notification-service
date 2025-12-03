package com.enterprise.notification.service.dispatcher;

import com.enterprise.notification.domain.entity.Notification;

public interface NotificationDispatcher {
    void dispatch(Notification notification);
    boolean supports(Notification notification);
}
