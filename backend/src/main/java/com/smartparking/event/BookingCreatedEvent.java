package com.smartparking.event;

import com.smartparking.entity.Booking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookingCreatedEvent {
    private final Booking booking;
}
