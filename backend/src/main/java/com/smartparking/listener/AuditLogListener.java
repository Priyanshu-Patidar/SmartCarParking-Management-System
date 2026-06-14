package com.smartparking.listener;

import com.smartparking.event.BookingCreatedEvent;
import com.smartparking.event.PaymentCompletedEvent;
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
                event.getUserEmail(),
                "BOOKING_CREATED",
                "Booking Code: " + event.getBookingCode() + " at " + event.getLocationName()
        );
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        auditService.log(
                event.getUserEmail(),
                "PAYMENT_COMPLETED",
                "Transaction: " + event.getTransactionId() + " Amount: ₹" + event.getAmount()
        );
    }
}
