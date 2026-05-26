package com.smartparking.controller;

import com.smartparking.entity.WaitlistEntry;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/waitlist")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> join(
            @RequestParam Long locationId,
            @RequestParam VehicleType vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime preferredStart,
            @RequestParam Integer durationHours) {
        return ResponseEntity.ok(waitlistService.joinWaitlist(locationId, vehicleType, preferredStart, durationHours));
    }

    @GetMapping
    public ResponseEntity<List<WaitlistEntry>> myWaitlist() {
        return ResponseEntity.ok(waitlistService.getMyWaitlist());
    }
}
