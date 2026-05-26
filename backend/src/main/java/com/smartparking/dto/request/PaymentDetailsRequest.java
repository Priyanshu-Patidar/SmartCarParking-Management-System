package com.smartparking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentDetailsRequest {
    @NotBlank
    private String paymentMethod;
    private String upiId;
    private String cardLastFour;
    private String cardHolderName;
}
