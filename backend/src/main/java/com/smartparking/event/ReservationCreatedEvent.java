package com.smartparking.event;

import com.smartparking.entity.WaitlistEntry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationCreatedEvent {
    private final WaitlistEntry waitlistEntry;
}
