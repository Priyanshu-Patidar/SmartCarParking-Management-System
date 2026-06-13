package com.smartparking.event;

import com.smartparking.entity.Payment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {
    private final Payment payment;
}
