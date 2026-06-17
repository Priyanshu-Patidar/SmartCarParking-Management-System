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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private static final Logger log = LoggerFactory.getLogger(ParkingService.class);

    private final ParkingLocationRepository locationRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final FavoriteLocationRepository favoriteRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final ParkingMapper parkingMapper;
    private final AuditService auditService;

    @Transactional
    @org.springframework.cache.annotation.Cacheable(value = "parkingSearch", key = "#location + #sortBy")
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
            SearchHistory history = new SearchHistory();
            history.setUser(user);
            history.setQuery(q);
            history.setSearchedAt(LocalDateTime.now());
            searchHistoryRepository.save(history);
        }
        return sortResults(parkingMapper.toResponseList(results, null, user), sortBy);
    }

    @Transactional(readOnly = true)
    public List<ParkingLocationResponse> findNearby(double lat, double lng, double radiusKm,
                                                    String sortBy, VehicleType vehicleType,
                                                    Boolean evOnly, Double maxPrice) {
        User user = tryGetUser();
        
        // Optimize: Use bounding box to filter locations at DB level (approx 1 degree lat ~ 111km)
        double latRange = radiusKm / 111.0;
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
        
        List<ParkingLocation> candidates = locationRepository.findByBoundingBox(
                lat - latRange, lat + latRange, lng - lngRange, lng + lngRange);

        List<Map.Entry<ParkingLocation, Double>> results = candidates.stream()
                .map(l -> {
                    double dist = GeoUtils.haversineKm(lat, lng, l.getLatitude(), l.getLongitude());
                    return new AbstractMap.SimpleEntry<>(l, dist);
                })
                .filter(entry -> entry.getValue() <= radiusKm)
                .collect(Collectors.toList());
        
        List<ParkingLocation> filteredLocations = results.stream()
                .map(Map.Entry::getKey)
                .filter(l -> vehicleType == null || l.getSupportedVehicleTypes().contains(vehicleType))
                .filter(l -> evOnly == null || !evOnly || l.isEvChargingAvailable())
                .filter(l -> maxPrice == null || l.getHourlyRate().doubleValue() <= maxPrice)
                .toList();

        Map<Long, Double> distanceMap = new HashMap<>();
        results.forEach(entry -> distanceMap.put(entry.getKey().getId(), Math.round(entry.getValue() * 100.0) / 100.0));

        return sortResults(parkingMapper.toResponseList(filteredLocations, distanceMap, user), sortBy);
    }

    private List<ParkingLocationResponse> mapBulkWithDistances(List<ParkingLocation> locations, Map<Long, Double> distanceMap, User user) {
        // Redundant method, will use parkingMapper.toResponseList directly in findNearby
        return parkingMapper.toResponseList(locations, distanceMap, user);
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
    @org.springframework.cache.annotation.CacheEvict(value = {"parkingSearch", "publicStats", "recommendations"}, allEntries = true)
    public ParkingLocationResponse createLocation(ParkingLocationRequest request) {
        ParkingLocation location = mapRequestToEntity(request, new ParkingLocation());
        locationRepository.save(location);
        auditService.log("admin", "PARKING_CREATED", "Created location: " + location.getName());
        return parkingMapper.toResponse(location, null, false);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"parkingSearch", "publicStats", "recommendations"}, allEntries = true)
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
        ParkingFloor floor = new ParkingFloor();
        floor.setLocation(location);
        floor.setFloorNumber(floorNumber);
        floor.setFloorName(floorName);
        for (int i = 1; i <= slotCount; i++) {
            ParkingSlot slot = new ParkingSlot();
            slot.setFloor(floor);
            slot.setSlotNumber(floorNumber + "-" + String.format("%03d", i));
            slot.setVehicleType(vehicleType);
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setEvCharging(vehicleType == VehicleType.EV);
            floor.getSlots().add(slot);
        }
        floorRepository.save(floor);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "favorites", key = "#user.id")
    public void toggleFavorite(Long locationId, User user) {
        if (favoriteRepository.existsByUserIdAndLocationId(user.getId(), locationId)) {
            favoriteRepository.deleteByUserIdAndLocationId(user.getId(), locationId);
        } else {
            ParkingLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
            FavoriteLocation fav = new FavoriteLocation();
            fav.setUser(user);
            fav.setLocation(location);
            favoriteRepository.save(fav);
        }
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "favorites", key = "#user.id")
    public List<ParkingLocationResponse> getFavorites(User user) {
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
