package com.smartparking.analytics;

import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeCalculationServiceTest {

    @Mock
    private ParkingSlotRepository slotRepository;

    private FeeCalculationService service;

    @BeforeEach
    void setUp() {
        service = new FeeCalculationService(slotRepository);
    }

    @Test
    void calculateFee_returnsPositiveAmount() {
        // Mock empty slots to simulate 0% occupancy (Normal)
        when(slotRepository.findByLocationId(anyLong())).thenReturn(Collections.emptyList());

        ParkingLocation location = ParkingLocation.builder()
                .id(1L)
                .hourlyRate(new BigDecimal("50"))
                .build();

        BigDecimal fee = service.calculateFee(location, VehicleType.CAR,
                LocalDateTime.of(2026, 6, 15, 14, 0), 2); // Monday afternoon

        assertTrue(fee.compareTo(BigDecimal.ZERO) > 0);
    }
}
