package com.smartparking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformStatsResponse {
    private long totalLocations;
    private long totalCities;
    private long availableSlots;
    private long bookingsToday;
    private double averageOccupancyPercent;
}
