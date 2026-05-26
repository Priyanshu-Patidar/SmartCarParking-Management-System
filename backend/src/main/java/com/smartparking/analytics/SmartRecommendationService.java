package com.smartparking.analytics;

import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.dto.response.ParkingRecommendationResponse;
import com.smartparking.dto.response.PlatformStatsResponse;
import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.mapper.ParkingMapper;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingLocationRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartRecommendationService {

    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final ParkingMapper parkingMapper;

    public ParkingRecommendationResponse getRecommendations(Double lat, Double lng, VehicleType vehicleType) {
        List<ParkingLocation> all = locationRepository.findAllActive();
        List<ParkingLocationResponse> mapped = all.stream()
                .map(l -> {
                    Double dist = (lat != null && lng != null)
                            ? GeoUtils.haversineKm(lat, lng, l.getLatitude(), l.getLongitude()) : null;
                    return parkingMapper.toResponse(l, dist, false);
                })
                .toList();

        int hour = LocalTime.now().getHour();
        boolean isPeak = (hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 20);

        List<ParkingLocationResponse> bestMatch = mapped.stream()
                .filter(p -> p.getAvailableSlots() > 5)
                .sorted(Comparator
                        .comparing(ParkingLocationResponse::getAvailableSlots).reversed()
                        .thenComparing(p -> p.getDistanceKm() != null ? p.getDistanceKm() : 999))
                .limit(5)
                .collect(Collectors.toList());

        List<ParkingLocationResponse> budget = mapped.stream()
                .sorted(Comparator.comparing(ParkingLocationResponse::getHourlyRate))
                .limit(5)
                .collect(Collectors.toList());

        List<ParkingLocationResponse> ev = mapped.stream()
                .filter(ParkingLocationResponse::isEvChargingAvailable)
                .filter(p -> p.getAvailableSlots() > 0)
                .limit(5)
                .collect(Collectors.toList());

        long cities = all.stream().map(ParkingLocation::getCity).distinct().count();
        String insight = isPeak
                ? "Peak demand detected. Book early or choose locations with higher availability scores."
                : "Off-peak hours — enjoy lower rates at selected facilities.";

        if (vehicleType == VehicleType.EV) {
            insight = "EV mode: prioritized locations with active charging infrastructure.";
        }

        return ParkingRecommendationResponse.builder()
                .bestMatch(bestMatch)
                .budgetFriendly(budget)
                .evRecommended(ev)
                .insightMessage(insight)
                .totalCitiesCovered((int) cities)
                .totalLocations(all.size())
                .build();
    }

    public PlatformStatsResponse buildPublicStats() {
        long available = slotRepository.findAll().stream()
                .filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
        long total = slotRepository.count();
        double occupancy = total > 0 ? ((total - available) * 100.0 / total) : 0;

        return PlatformStatsResponse.builder()
                .totalLocations(locationRepository.count())
                .totalCities(locationRepository.findAllActive().stream().map(ParkingLocation::getCity).distinct().count())
                .availableSlots(available)
                .bookingsToday(bookingRepository.countBookingsSince(LocalDateTime.now().toLocalDate().atStartOfDay()))
                .averageOccupancyPercent(Math.round(occupancy * 10) / 10.0)
                .build();
    }
}
