package com.smartparking.controller;

import com.smartparking.service.ParkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ParkingSessionService sessionService;

    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<Map<String, Object>> checkIn(@PathVariable Long bookingId) {
        return ResponseEntity.ok(sessionService.checkIn(bookingId));
    }

    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<Map<String, Object>> checkOut(@PathVariable Long bookingId) {
        return ResponseEntity.ok(sessionService.checkOut(bookingId));
    }
}
