package com.smartparking.event;

import com.smartparking.entity.Booking;

public class BookingCreatedEvent {
    private final Booking booking;

    public BookingCreatedEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}
