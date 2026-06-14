package com.smartparking.event;

import lombok.Getter;

@Getter
public class BookingCreatedEvent {
    private final Long bookingId;
    private final String bookingCode;
    private final String userEmail;
    private final String locationName;

    public BookingCreatedEvent(Long bookingId, String bookingCode, String userEmail, String locationName) {
        this.bookingId = bookingId;
        this.bookingCode = bookingCode;
        this.userEmail = userEmail;
        this.locationName = locationName;
    }
}
