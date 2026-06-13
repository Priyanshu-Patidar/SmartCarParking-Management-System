package com.smartparking.listener;

import com.smartparking.entity.enums.NotificationType;
import com.smartparking.event.BookingCreatedEvent;
import com.smartparking.event.ReservationCreatedEvent;
import com.smartparking.event.ReservationExpiredEvent;
import com.smartparking.event.UserRegisteredEvent;
import com.smartparking.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        notificationService.notify(
                event.getBooking().getUser(),
                "Booking Confirmed",
                "Your parking at " + event.getBooking().getLocation().getName() + " is confirmed.",
                NotificationType.BOOKING_CONFIRMED
        );
    }

    @Async
    @EventListener
    public void handleReservationCreated(ReservationCreatedEvent event) {
        notificationService.notify(
                event.getWaitlistEntry().getUser(),
                "Waitlist confirmed",
                "We will notify you when a slot opens at " + event.getWaitlistEntry().getLocation().getName() + ".",
                NotificationType.SYSTEM
        );
    }

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        notificationService.notify(
                event.getUser(),
                "Welcome to SmartPark",
                "Thank you for registering! You can now book parking slots in real-time.",
                NotificationType.SYSTEM
        );
    }

    @Async
    @EventListener
    public void handleReservationExpired(ReservationExpiredEvent event) {
        notificationService.notify(
                event.getBooking().getUser(),
                "Parking Session Completed",
                "Your booking at " + event.getBooking().getLocation().getName() + " has ended.",
                NotificationType.SYSTEM
        );
    }
}
