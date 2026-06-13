package com.smartparking.event;

import com.smartparking.entity.Booking;

public class ReservationExpiredEvent {
    private final Booking booking;

    public ReservationExpiredEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}
