package com.smartparking.analytics;

import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class FeeCalculationService {

    private static final LocalTime PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime PEAK_END = LocalTime.of(11, 0);
    private static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_PEAK_END = LocalTime.of(20, 0);

    public BigDecimal calculateFee(ParkingLocation location, VehicleType vehicleType,
                                 LocalDateTime startTime, int durationHours) {
        BigDecimal baseRate = resolveBaseRate(location, vehicleType);
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < durationHours; i++) {
            LocalDateTime hour = startTime.plusHours(i);
            BigDecimal rate = isPeakHour(hour.toLocalTime()) && location.getPeakHourRate() != null
                    ? location.getPeakHourRate()
                    : baseRate;
            total = total.add(rate);
        }

        BigDecimal demandMultiplier = calculateDemandMultiplier(hourOfDay(startTime));
        return total.multiply(demandMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveBaseRate(ParkingLocation location, VehicleType vehicleType) {
        return switch (vehicleType) {
            case BIKE -> location.getBikeRate() != null ? location.getBikeRate() : location.getHourlyRate().multiply(BigDecimal.valueOf(0.5));
            case EV -> location.getEvRate() != null ? location.getEvRate() : location.getHourlyRate().multiply(BigDecimal.valueOf(1.2));
            default -> location.getHourlyRate();
        };
    }

    private boolean isPeakHour(LocalTime time) {
        return (!time.isBefore(PEAK_START) && time.isBefore(PEAK_END))
                || (!time.isBefore(EVENING_PEAK_START) && time.isBefore(EVENING_PEAK_END));
    }

    /** Simple AI-inspired dynamic pricing based on hour-of-day demand curve */
    private BigDecimal calculateDemandMultiplier(int hour) {
        if (hour >= 8 && hour <= 10) return BigDecimal.valueOf(1.15);
        if (hour >= 17 && hour <= 19) return BigDecimal.valueOf(1.20);
        if (hour >= 22 || hour <= 6) return BigDecimal.valueOf(0.85);
        return BigDecimal.ONE;
    }

    private int hourOfDay(LocalDateTime dateTime) {
        return dateTime.getHour();
    }
}
