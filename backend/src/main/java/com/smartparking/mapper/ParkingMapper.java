package com.smartparking.mapper;

import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.User;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.repository.FavoriteLocationRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParkingMapper {

    private final ParkingSlotRepository slotRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteLocationRepository favoriteRepository;

    public List<ParkingLocationResponse> toResponseList(List<ParkingLocation> locations, Map<Long, Double> distances, User user) {
        if (locations.isEmpty()) return List.of();

        List<Long> locationIds = locations.stream().map(ParkingLocation::getId).collect(Collectors.toList());

        Map<Long, Map<SlotStatus, Long>> slotStats = new HashMap<>();
        for (Object[] row : slotRepository.countStatusesGroupedByLocationIds(locationIds)) {
            Long locId = (Long) row[0];
            SlotStatus status = (SlotStatus) row[1];
            Long count = (Long) row[2];
            slotStats.computeIfAbsent(locId, k -> new HashMap<>()).put(status, count);
        }

        Map<Long, Double> ratings = new HashMap<>();
        for (Object[] row : reviewRepository.getAverageRatingsForLocations(locationIds)) {
            ratings.put((Long) row[0], (Double) row[1]);
        }
        Map<Long, Long> reviewCounts = new HashMap<>();
        for (Object[] row : reviewRepository.getReviewCountsForLocations(locationIds)) {
            reviewCounts.put((Long) row[0], (Long) row[1]);
        }

        return locations.stream().map(l -> {
            Map<SlotStatus, Long> stats = slotStats.getOrDefault(l.getId(), Map.of());
            long available = stats.getOrDefault(SlotStatus.AVAILABLE, 0L);
            long occupied = stats.getOrDefault(SlotStatus.OCCUPIED, 0L);
            long reserved = stats.getOrDefault(SlotStatus.RESERVED, 0L);
            long maintenance = stats.getOrDefault(SlotStatus.MAINTENANCE, 0L);
            long total = available + occupied + reserved + maintenance;

            boolean fav = user != null && favoriteRepository.existsByUserIdAndLocationId(user.getId(), l.getId());
            
            Double dist = (distances != null) ? distances.get(l.getId()) : null;

            return toResponse(l, dist, fav, 
                    available, occupied, reserved, total,
                    ratings.get(l.getId()), reviewCounts.getOrDefault(l.getId(), 0L));
        }).collect(Collectors.toList());
    }

    public ParkingLocationResponse toResponse(ParkingLocation location, Double distanceKm, boolean favorite) {
        // Optimization: Use the bulk logic even for single entity to avoid manual N+1 queries
        Map<Long, Double> dists = distanceKm != null ? Map.of(location.getId(), distanceKm) : null;
        List<ParkingLocationResponse> list = toResponseList(List.of(location), dists, null);
        return list.get(0);
    }

    public ParkingLocationResponse toResponse(ParkingLocation location, Double distanceKm, boolean favorite,
                                              Long available, Long occupied, Long reserved, Long total,
                                              Double avgRating, Long reviewCount) {
        
        if (available == null) {
            available = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.AVAILABLE);
            occupied = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.OCCUPIED);
            reserved = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.RESERVED);
            total = available + occupied + reserved +
                    slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.MAINTENANCE);
        }

        if (avgRating == null) {
            avgRating = reviewRepository.getAverageRating(location.getId());
            reviewCount = reviewRepository.getAllReviewCounts().stream()
                    .filter(r -> r[0].equals(location.getId()))
                    .map(r -> (Long) r[1])
                    .findFirst().orElse(0L);
        }

        return ParkingLocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .city(location.getCity())
                .state(location.getState())
                .zipCode(location.getZipCode())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .imageUrl(location.getImageUrl())
                .hourlyRate(location.getHourlyRate())
                .peakHourRate(location.getPeakHourRate())
                .bikeRate(location.getBikeRate())
                .evRate(location.getEvRate())
                .evChargingAvailable(location.isEvChargingAvailable())
                .openTime(location.getOpenTime())
                .closeTime(location.getCloseTime())
                .description(location.getDescription())
                .supportedVehicleTypes(location.getSupportedVehicleTypes())
                .totalSlots(total.intValue())
                .availableSlots(available.intValue())
                .occupiedSlots(occupied.intValue())
                .reservedSlots(reserved.intValue())
                .distanceKm(distanceKm)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null)
                .reviewCount(reviewCount)
                .favorite(favorite)
                .build();
    }
}
