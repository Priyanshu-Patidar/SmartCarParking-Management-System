package com.smartparking.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentCompletedEvent {
    private final Long paymentId;
    private final String transactionId;
    private final BigDecimal amount;
    private final String userEmail;

    public PaymentCompletedEvent(Long paymentId, String transactionId, BigDecimal amount, String userEmail) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.userEmail = userEmail;
    }
}
