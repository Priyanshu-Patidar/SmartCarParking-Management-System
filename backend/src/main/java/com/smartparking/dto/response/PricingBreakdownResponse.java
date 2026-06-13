package com.smartparking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingBreakdownResponse {
    private BigDecimal baseAmount;
    private BigDecimal peakSurcharge;
    private BigDecimal occupancySurcharge;
    private BigDecimal weekendSurcharge;
    private BigDecimal vehicleMultiplier;
    private BigDecimal totalAmount;
    private List<String> appliedRules;
    private String occupancyStatus;

    public BigDecimal getTotalAmount() { return totalAmount; }
}
