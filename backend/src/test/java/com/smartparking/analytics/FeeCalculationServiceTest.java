package com.smartparking.analytics;

import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.VehicleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FeeCalculationServiceTest {

    private final FeeCalculationService service = new FeeCalculationService();

    @Test
    void calculateFee_returnsPositiveAmount() {
        ParkingLocation location = ParkingLocation.builder()
                .hourlyRate(new BigDecimal("50"))
                .peakHourRate(new BigDecimal("75"))
                .bikeRate(new BigDecimal("25"))
                .evRate(new BigDecimal("60"))
                .build();

        BigDecimal fee = service.calculateFee(location, VehicleType.CAR,
                LocalDateTime.of(2026, 5, 21, 9, 0), 3);

        assertTrue(fee.compareTo(BigDecimal.ZERO) > 0);
    }
}
