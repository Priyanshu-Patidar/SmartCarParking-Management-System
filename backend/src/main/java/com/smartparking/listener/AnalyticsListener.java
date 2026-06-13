package com.smartparking.listener;

import com.smartparking.event.BookingCreatedEvent;
import com.smartparking.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsListener {

    @Async
    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Analytics: Processing new booking for occupancy trends. Location: {}", 
                event.getBooking().getLocation().getName());
        // Integration point for future persistent analytics caching
    }

    @Async
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Analytics: Updating revenue metrics. Amount: ₹{}", 
                event.getPayment().getAmount());
        // Integration point for future revenue forecasting
    }
}
