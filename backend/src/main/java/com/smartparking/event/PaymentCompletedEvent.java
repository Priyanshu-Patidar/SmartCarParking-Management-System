package com.smartparking.event;

import com.smartparking.entity.Payment;

public class PaymentCompletedEvent {
    private final Payment payment;

    public PaymentCompletedEvent(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
