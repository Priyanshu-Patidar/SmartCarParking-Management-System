package com.smartparking.listener;

import com.smartparking.event.BookingCreatedEvent;
import com.smartparking.event.PaymentCompletedEvent;
import com.smartparking.event.UserRegisteredEvent;
import com.smartparking.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditService auditService;

    @Async
    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        auditService.log(
                event.getBooking().getUser().getEmail(),
                "BOOKING_CREATED",
                "Booking Code: " + event.getBooking().getBookingCode() + " at " + event.getBooking().getLocation().getName()
        );
    }

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        auditService.log(
                event.getUser().getEmail(),
                "USER_REGISTERED",
                "New account created for " + event.getUser().getFullName()
        );
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        auditService.log(
                event.getPayment().getBooking().getUser().getEmail(),
                "PAYMENT_COMPLETED",
                "Transaction: " + event.getPayment().getTransactionId() + " Amount: ₹" + event.getPayment().getAmount()
        );
    }
}
