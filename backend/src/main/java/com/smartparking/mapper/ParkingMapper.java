package com.smartparking.mapper;

import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkingMapper {

    private final ParkingSlotRepository slotRepository;
    private final ReviewRepository reviewRepository;

    public ParkingLocationResponse toResponse(ParkingLocation location, Double distanceKm, boolean favorite) {
        long available = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.AVAILABLE);
        long occupied = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.OCCUPIED);
        long reserved = slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.RESERVED);
        long total = available + occupied + reserved +
                slotRepository.countByLocationIdAndStatus(location.getId(), SlotStatus.MAINTENANCE);

        Double avgRating = reviewRepository.getAverageRating(location.getId());
        long reviewCount = reviewRepository.countByLocationId(location.getId());

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
                .totalSlots((int) total)
                .availableSlots((int) available)
                .occupiedSlots((int) occupied)
                .reservedSlots((int) reserved)
                .distanceKm(distanceKm)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null)
                .reviewCount(reviewCount)
                .favorite(favorite)
                .build();
    }
}
