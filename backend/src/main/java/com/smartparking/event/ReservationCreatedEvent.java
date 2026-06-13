package com.smartparking.event;

import com.smartparking.entity.WaitlistEntry;

public class ReservationCreatedEvent {
    private final WaitlistEntry waitlistEntry;

    public ReservationCreatedEvent(WaitlistEntry waitlistEntry) {
        this.waitlistEntry = waitlistEntry;
    }

    public WaitlistEntry getWaitlistEntry() {
        return waitlistEntry;
    }
}
