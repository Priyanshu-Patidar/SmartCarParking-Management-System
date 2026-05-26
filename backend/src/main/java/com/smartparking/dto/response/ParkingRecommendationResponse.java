package com.smartparking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParkingRecommendationResponse {
    private List<ParkingLocationResponse> bestMatch;
    private List<ParkingLocationResponse> budgetFriendly;
    private List<ParkingLocationResponse> evRecommended;
    private String insightMessage;
    private int totalCitiesCovered;
    private long totalLocations;
}
