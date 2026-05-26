package com.smartparking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalLocations;
    private long totalSlots;
    private long availableSlots;
    private long occupiedSlots;
    private long reservedSlots;
    private long totalUsers;
    private long totalBookings;
    private long activeBookings;
    private BigDecimal totalRevenue;
    private List<Map<String, Object>> bookingTrends;
    private List<Map<String, Object>> revenueByLocation;
    private List<Map<String, Object>> peakHourAnalytics;
}
