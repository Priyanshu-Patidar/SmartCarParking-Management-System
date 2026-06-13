package com.smartparking.listener;

import com.smartparking.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationCreatedListener.class);

    @Async
    @EventListener
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.info("System Notification: [{}] for user {}", 
                event.getNotification().getTitle(), 
                event.getNotification().getUser().getEmail());
        // Integration point for Real-time Push Notifications (FCM/OneSignal)
    }
}
