package com.smartparking.controller;

import com.smartparking.dto.request.PreBookRequest;
import com.smartparking.dto.response.BookingResponse;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/parking/prebook")
    public ResponseEntity<BookingResponse> preBook(@Valid @RequestBody PreBookRequest request) {
        return ResponseEntity.ok(bookingService.preBook(request));
    }

    @PutMapping("/parking/cancel/{id}")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/parking/estimate")
    public ResponseEntity<BigDecimal> estimate(
            @RequestParam Long locationId,
            @RequestParam VehicleType vehicleType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam Integer durationHours) {
        return ResponseEntity.ok(bookingService.estimateFee(locationId, vehicleType, startTime, durationHours));
    }

    @PostMapping("/parking/estimate")
    public ResponseEntity<BigDecimal> estimatePost(@RequestParam Long locationId,
                                                   @Valid @RequestBody PreBookRequest request) {
        return ResponseEntity.ok(bookingService.estimateFee(locationId, request));
    }

    @GetMapping("/bookings/user")
    public ResponseEntity<Page<BookingResponse>> userBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getUserBookings(PageRequest.of(page, size)));
    }

    @GetMapping("/bookings/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> adminBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookingService.getAllBookings(PageRequest.of(page, size)));
    }

    @GetMapping("/bookings/{code}")
    public ResponseEntity<BookingResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(bookingService.getByCode(code));
    }
}
