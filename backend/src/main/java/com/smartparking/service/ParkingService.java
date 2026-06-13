package com.smartparking.service;

import com.smartparking.dto.request.ParkingLocationRequest;
import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.dto.response.ParkingSlotResponse;
import com.smartparking.entity.*;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.mapper.ParkingMapper;
import com.smartparking.repository.*;
import com.smartparking.util.GeoUtils;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingService {

    private final ParkingLocationRepository locationRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final FavoriteLocationRepository favoriteRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ParkingMapper parkingMapper;
    private final AuditService auditService;

    @Transactional
    public List<ParkingLocationResponse> search(String location, String sortBy) {
        User user = tryGetUser();
        String q = location.trim();
        List<ParkingLocation> results = locationRepository.searchByQuery(q);
        if (results.isEmpty()) {
            results = locationRepository.findByActiveTrueAndCityContainingIgnoreCase(q);
        }
        if (results.isEmpty() && q.contains(" ")) {
            results = locationRepository.searchByQuery(q.split("\\s+")[0]);
        }
        if (user != null) {
            searchHistoryRepository.save(SearchHistory.builder()
                    .user(user).query(q).searchedAt(LocalDateTime.now()).build());
        }
        return sortResults(parkingMapper.toResponseList(results, null, user), sortBy);
    }

    @Transactional(readOnly = true)
    public List<ParkingLocationResponse> findNearby(double lat, double lng, double radiusKm,
                                                    String sortBy, VehicleType vehicleType,
                                                    Boolean evOnly, Double maxPrice) {
        User user = tryGetUser();
        List<ParkingLocation> nearby = locationRepository.findAllActive().stream()
                .filter(l -> GeoUtils.haversineKm(lat, lng, l.getLatitude(), l.getLongitude()) <= radiusKm)
                .collect(Collectors.toList());
        
        List<ParkingLocationResponse> mapped = nearby.stream()
                .filter(l -> vehicleType == null || l.getSupportedVehicleTypes().contains(vehicleType))
                .filter(l -> evOnly == null || !evOnly || l.isEvChargingAvailable())
                .filter(l -> maxPrice == null || l.getHourlyRate().doubleValue() <= maxPrice)
                .map(l -> {
                    double dist = Math.round(GeoUtils.haversineKm(lat, lng, l.getLatitude(), l.getLongitude()) * 100.0) / 100.0;
                    // We call individual toResponse here because it's a specialized filter/map, 
                    // but search() and getFavorites() use bulk.
                    boolean fav = user != null && favoriteRepository.existsByUserIdAndLocationId(user.getId(), l.getId());
                    return parkingMapper.toResponse(l, dist, fav);
                })
                .collect(Collectors.toList());

        return sortResults(mapped, sortBy);
    }

    @Transactional(readOnly = true)
    public ParkingLocationResponse getById(Long id, Double refLat, Double refLng) {
        ParkingLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking location not found"));
        User user = tryGetUser();
        Double distance = null;
        if (refLat != null && refLng != null) {
            distance = Math.round(GeoUtils.haversineKm(refLat, refLng, location.getLatitude(), location.getLongitude()) * 100.0) / 100.0;
        }
        boolean fav = user != null && favoriteRepository.existsByUserIdAndLocationId(user.getId(), id);
        return parkingMapper.toResponse(location, distance, fav);
    }

    @Transactional
    public ParkingLocationResponse createLocation(ParkingLocationRequest request) {
        ParkingLocation location = mapRequestToEntity(request, new ParkingLocation());
        locationRepository.save(location);
        auditService.log("admin", "PARKING_CREATED", "Created location: " + location.getName());
        return parkingMapper.toResponse(location, null, false);
    }

    @Transactional
    public ParkingLocationResponse updateLocation(Long id, ParkingLocationRequest request) {
        ParkingLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking location not found"));
        mapRequestToEntity(request, location);
        return parkingMapper.toResponse(locationRepository.save(location), null, false);
    }

    @Transactional
    public void addFloor(Long locationId, int floorNumber, String floorName, int slotCount, VehicleType vehicleType) {
        ParkingLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking location not found"));
        ParkingFloor floor = ParkingFloor.builder()
                .location(location).floorNumber(floorNumber).floorName(floorName).build();
        for (int i = 1; i <= slotCount; i++) {
            floor.getSlots().add(ParkingSlot.builder()
                    .floor(floor)
                    .slotNumber(floorNumber + "-" + String.format("%03d", i))
                    .vehicleType(vehicleType)
                    .status(SlotStatus.AVAILABLE)
                    .evCharging(vehicleType == VehicleType.EV)
                    .build());
        }
        floorRepository.save(floor);
    }

    @Transactional
    public void toggleFavorite(Long locationId) {
        User user = SecurityUtils.getCurrentUser();
        if (favoriteRepository.existsByUserIdAndLocationId(user.getId(), locationId)) {
            favoriteRepository.deleteByUserIdAndLocationId(user.getId(), locationId);
        } else {
            ParkingLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            favoriteRepository.save(FavoriteLocation.builder().user(user).location(location).build());
        }
    }

    @Transactional(readOnly = true)
    public List<ParkingLocationResponse> getFavorites() {
        User user = SecurityUtils.getCurrentUser();
        List<ParkingLocation> locations = favoriteRepository.findByUserId(user.getId()).stream()
                .map(FavoriteLocation::getLocation)
                .toList();
        return parkingMapper.toResponseList(locations, null, user);
    }

    @Transactional(readOnly = true)
    public List<ParkingSlotResponse> getAvailableSlots(Long locationId, VehicleType vehicleType,
                                                     LocalDateTime start, LocalDateTime end) {
        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE);

        List<ParkingSlot> slots = slotRepository.findAvailableSlots(
                locationId,
                vehicleType,
                List.of(SlotStatus.AVAILABLE, SlotStatus.RESERVED),
                activeStatuses,
                start,
                end);

        if (slots.isEmpty()) {
            slots = slotRepository.findByLocationIdAndVehicleType(locationId, vehicleType).stream()
                    .filter(s -> s.getStatus() != SlotStatus.OCCUPIED && s.getStatus() != SlotStatus.MAINTENANCE)
                    .filter(s -> !hasOverlappingBooking(s.getId(), start, end, activeStatuses))
                    .toList();
        }

        return slots.stream()
                .map(this::mapToSlotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParkingSlotResponse> getAllSlotsForLocation(Long locationId) {
        return slotRepository.findByLocationId(locationId).stream()
                .map(this::mapToSlotResponse)
                .collect(Collectors.toList());
    }

    private ParkingSlotResponse mapToSlotResponse(ParkingSlot s) {
        return ParkingSlotResponse.builder()
                .id(s.getId())
                .slotNumber(s.getSlotNumber())
                .status(s.getStatus())
                .vehicleType(s.getVehicleType())
                .evCharging(s.isEvCharging())
                .floorNumber(s.getFloor().getFloorNumber())
                .floorName(s.getFloor().getFloorName())
                .build();
    }

    private ParkingLocation mapRequestToEntity(ParkingLocationRequest req, ParkingLocation entity) {
        entity.setName(req.getName());
        entity.setAddress(req.getAddress());
        entity.setCity(req.getCity());
        entity.setState(req.getState());
        entity.setZipCode(req.getZipCode());
        entity.setLatitude(req.getLatitude());
        entity.setLongitude(req.getLongitude());
        entity.setImageUrl(req.getImageUrl());
        entity.setHourlyRate(req.getHourlyRate());
        entity.setPeakHourRate(req.getPeakHourRate());
        entity.setBikeRate(req.getBikeRate());
        entity.setEvRate(req.getEvRate());
        entity.setEvChargingAvailable(req.isEvChargingAvailable());
        entity.setOpenTime(req.getOpenTime());
        entity.setCloseTime(req.getCloseTime());
        entity.setDescription(req.getDescription());
        if (req.getSupportedVehicleTypes() != null) {
            entity.setSupportedVehicleTypes(req.getSupportedVehicleTypes());
        }
        return entity;
    }

    private List<ParkingLocationResponse> sortResults(List<ParkingLocationResponse> list, String sortBy) {
        if (sortBy == null) return list;
        Comparator<ParkingLocationResponse> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparing(ParkingLocationResponse::getHourlyRate);
            case "availability" -> Comparator.comparing(ParkingLocationResponse::getAvailableSlots).reversed();
            case "rating" -> Comparator.comparing((ParkingLocationResponse r) -> r.getAverageRating() != null ? r.getAverageRating() : 0.0).reversed();
            default -> Comparator.comparing((ParkingLocationResponse r) -> r.getDistanceKm() != null ? r.getDistanceKm() : Double.MAX_VALUE);
        };
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    private boolean hasOverlappingBooking(Long slotId, LocalDateTime start, LocalDateTime end,
                                          List<BookingStatus> statuses) {
        return bookingRepository.countOverlappingBookings(slotId, statuses, start, end, null) > 0;
    }

    private User tryGetUser() {
        try {
            return SecurityUtils.getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }
}
