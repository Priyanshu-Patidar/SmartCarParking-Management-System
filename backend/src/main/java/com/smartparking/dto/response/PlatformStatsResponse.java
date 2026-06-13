package com.smartparking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsResponse {
    private long totalLocations;
    private long totalCities;
    private long availableSlots;
    private long bookingsToday;
    private double averageOccupancyPercent;
}
