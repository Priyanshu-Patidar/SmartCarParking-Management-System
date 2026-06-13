package com.smartparking.event;

import com.smartparking.entity.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {
    private final Notification notification;
}
