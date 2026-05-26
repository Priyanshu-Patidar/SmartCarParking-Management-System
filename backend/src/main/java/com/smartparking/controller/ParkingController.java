package com.smartparking.controller;

import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.dto.response.ParkingSlotResponse;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.exception.BadRequestException;
import com.smartparking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    };

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
            @RequestParam(defaultValue = "10") double radiusKm,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) Boolean evOnly,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(parkingService.findNearby(lat, lng, radiusKm, sortBy, vehicleType, evOnly, maxPrice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLocationResponse> getById(
            @PathVariable Long id,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        return ResponseEntity.ok(parkingService.getById(id, lat, lng));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<ParkingSlotResponse>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam VehicleType vehicleType,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);
        if (!end.isAfter(start)) {
            throw new BadRequestException("End time must be after start time");
        }
        return ResponseEntity.ok(parkingService.getAvailableSlots(id, vehicleType, start, end));
    }

    @PostMapping("/favorites/{id}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id) {
        parkingService.toggleFavorite(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ParkingLocationResponse>> favorites() {
        return ResponseEntity.ok(parkingService.getFavorites());
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Date/time is required");
        }
        String normalized = value.trim().replace(" ", "T");
        if (normalized.length() == 16) {
            normalized += ":00";
        }
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new BadRequestException("Invalid date/time format: " + value);
    }
}
