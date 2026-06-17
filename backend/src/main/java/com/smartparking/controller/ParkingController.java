package com.smartparking.controller;

import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.dto.response.ParkingSlotResponse;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.exception.BadRequestException;
import com.smartparking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartparking.entity.User;
import com.smartparking.util.SecurityUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/v1/parking")
@RequiredArgsConstructor
public class ParkingController {

    private static final Logger log = LoggerFactory.getLogger(ParkingController.class);

    private final ParkingService parkingService;

    @GetMapping("/search")
    public ResponseEntity<List<ParkingLocationResponse>> search(
            @RequestParam String location,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(parkingService.search(location, sortBy));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ParkingLocationResponse>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radius,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) Boolean evOnly,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(parkingService.findNearby(lat, lng, radius, sortBy, vehicleType, evOnly, maxPrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLocationResponse> getById(
            @PathVariable Long id,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        return ResponseEntity.ok(parkingService.getById(id, lat, lng));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ParkingLocationResponse>> getFavorites() {
        User user = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(parkingService.getFavorites(user));
    }

    @PostMapping("/favorites/{id}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id) {
        User user = SecurityUtils.getCurrentUser();
        parkingService.toggleFavorite(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<ParkingSlotResponse>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        log.info("Fetching slots for location: {}, type: {}, start: {}, end: {}", id, vehicleType, startTime, endTime);

        if (vehicleType == null || startTime == null || endTime == null) {
            return ResponseEntity.ok(parkingService.getAllSlotsForLocation(id));
        }

        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);
        if (!end.isAfter(start)) {
            throw new BadRequestException("End time must be after start time");
        }
        return ResponseEntity.ok(parkingService.getAvailableSlots(id, vehicleType, start, end));
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value;
        if (value.length() == 16) normalized = value + ":00";
        
        String[] patterns = {"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm"};
        for (String pattern : patterns) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new BadRequestException("Invalid date/time format: " + value);
    }
}
