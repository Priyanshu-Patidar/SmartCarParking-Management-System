package com.smartparking.analytics;

import com.smartparking.dto.response.PricingBreakdownResponse;
import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeCalculationService {

    private final ParkingSlotRepository slotRepository;

    private static final LocalTime MORNING_PEAK_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_PEAK_END = LocalTime.of(11, 0);
    private static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_PEAK_END = LocalTime.of(21, 0);

    public BigDecimal calculateFee(ParkingLocation location, VehicleType vehicleType,
                                 LocalDateTime startTime, int durationHours) {
        return calculateDetailedBreakdown(location, vehicleType, startTime, durationHours).getTotalAmount();
    }

    public PricingBreakdownResponse calculateDetailedBreakdown(ParkingLocation location, VehicleType vehicleType,
                                                              LocalDateTime startTime, int durationHours) {
        List<String> rules = new ArrayList<>();
        BigDecimal baseRate = resolveBaseRate(location, vehicleType);
        BigDecimal hourlyBase = baseRate.multiply(BigDecimal.valueOf(durationHours));
        
        BigDecimal peakSurcharge = BigDecimal.ZERO;
        BigDecimal totalSurcharge = BigDecimal.ZERO;

        // 1. Peak Hour Surcharge
        for (int i = 0; i < durationHours; i++) {
            LocalDateTime hour = startTime.plusHours(i);
            if (isPeakHour(hour.toLocalTime())) {
                BigDecimal surcharge = baseRate.multiply(BigDecimal.valueOf(0.25)); // 25% peak surcharge
                peakSurcharge = peakSurcharge.add(surcharge);
            }
        }
        if (peakSurcharge.compareTo(BigDecimal.ZERO) > 0) {
            rules.add("Peak Hour Surge (25%)");
        }

        // 2. Weekend Multiplier
        BigDecimal weekendSurcharge = BigDecimal.ZERO;
        if (isWeekend(startTime)) {
            weekendSurcharge = hourlyBase.multiply(BigDecimal.valueOf(0.15)); // 15% weekend surcharge
            rules.add("Weekend Premium (15%)");
        }

        // 3. Real-time Occupancy Surge
        BigDecimal occupancySurcharge = BigDecimal.ZERO;
        double occupancy = calculateOccupancy(location.getId());
        String occupancyStatus = "Normal";
        
        if (occupancy > 0.9) {
            occupancySurcharge = hourlyBase.multiply(BigDecimal.valueOf(0.50));
            rules.add("Critical Demand Surge (50%)");
            occupancyStatus = "Critical";
        } else if (occupancy > 0.7) {
            occupancySurcharge = hourlyBase.multiply(BigDecimal.valueOf(0.20));
            rules.add("High Demand Surge (20%)");
            occupancyStatus = "High";
        }

        BigDecimal total = hourlyBase.add(peakSurcharge).add(weekendSurcharge).add(occupancySurcharge);

        return PricingBreakdownResponse.builder()
                .baseAmount(hourlyBase.setScale(2, RoundingMode.HALF_UP))
                .peakSurcharge(peakSurcharge.setScale(2, RoundingMode.HALF_UP))
                .weekendSurcharge(weekendSurcharge.setScale(2, RoundingMode.HALF_UP))
                .occupancySurcharge(occupancySurcharge.setScale(2, RoundingMode.HALF_UP))
                .totalAmount(total.setScale(2, RoundingMode.HALF_UP))
                .appliedRules(rules)
                .occupancyStatus(occupancyStatus)
                .build();
    }

    private BigDecimal resolveBaseRate(ParkingLocation location, VehicleType vehicleType) {
        return switch (vehicleType) {
            case BIKE -> location.getBikeRate() != null ? location.getBikeRate() : location.getHourlyRate().multiply(BigDecimal.valueOf(0.5));
            case EV -> location.getEvRate() != null ? location.getEvRate() : location.getHourlyRate().multiply(BigDecimal.valueOf(1.2));
            default -> location.getHourlyRate();
        };
    }

    private boolean isPeakHour(LocalTime time) {
        return (!time.isBefore(MORNING_PEAK_START) && time.isBefore(MORNING_PEAK_END))
                || (!time.isBefore(EVENING_PEAK_START) && time.isBefore(EVENING_PEAK_END));
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private double calculateOccupancy(Long locationId) {
        long total = slotRepository.findByLocationId(locationId).size();
        if (total == 0) return 0;
        long occupied = slotRepository.countByLocationIdAndStatus(locationId, SlotStatus.OCCUPIED) +
                        slotRepository.countByLocationIdAndStatus(locationId, SlotStatus.RESERVED);
        return (double) occupied / total;
    }
}
