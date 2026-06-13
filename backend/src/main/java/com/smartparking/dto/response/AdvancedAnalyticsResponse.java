package com.smartparking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedAnalyticsResponse {
    private List<Map<String, Object>> revenueTrends;
    private List<Map<String, Object>> occupancyTrends;
    private List<Map<String, Object>> vehicleTypeStats;
    private List<Map<String, Object>> peakHourStats;
    private List<Map<String, Object>> slotUtilization;
    private Map<String, Object> summary;
}
