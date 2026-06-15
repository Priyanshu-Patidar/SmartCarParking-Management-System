package com.smartparking.controller;

import com.smartparking.analytics.DashboardAnalyticsService;
import com.smartparking.analytics.SmartRecommendationService;
import com.smartparking.dto.response.DashboardStatsResponse;
import com.smartparking.dto.response.ParkingRecommendationResponse;
import com.smartparking.dto.response.PlatformStatsResponse;
import com.smartparking.entity.enums.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class InsightsController {

    private final SmartRecommendationService recommendationService;
    private final DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping("/public/stats")
    @org.springframework.cache.annotation.Cacheable(value = "publicStats")
    public ResponseEntity<PlatformStatsResponse> publicStats() {
        return ResponseEntity.ok(recommendationService.buildPublicStats());
    }

    @GetMapping("/parking/recommendations")
    @org.springframework.cache.annotation.Cacheable(value = "recommendations", key = "#lat + '-' + #lng + '-' + #vehicleType")
    public ResponseEntity<ParkingRecommendationResponse> recommendations(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) VehicleType vehicleType) {
        return ResponseEntity.ok(recommendationService.getRecommendations(lat, lng, vehicleType));
    }

    @GetMapping("/insights/peak-hours")
    public ResponseEntity<DashboardStatsResponse> peakHours() {
        return ResponseEntity.ok(dashboardAnalyticsService.getStats());
    }
}
