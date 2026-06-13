package com.smartparking.listener;

import com.smartparking.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCreatedListener {

    @Async
    @EventListener
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.info("System Notification: [{}] for user {}", 
                event.getNotification().getTitle(), 
                event.getNotification().getUser().getEmail());
        // Integration point for Real-time Push Notifications (FCM/OneSignal)
    }
}
