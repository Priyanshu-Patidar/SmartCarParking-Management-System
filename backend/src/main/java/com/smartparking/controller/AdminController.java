package com.smartparking.controller;

import com.smartparking.dto.request.ParkingLocationRequest;
import com.smartparking.dto.response.ParkingLocationResponse;
import com.smartparking.entity.AuditLog;
import com.smartparking.entity.User;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.service.AdminService;
import com.smartparking.service.AuditService;
import com.smartparking.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ParkingService parkingService;
    private final AdminService adminService;
    private final AuditService auditService;

    @PostMapping("/parking")
    public ResponseEntity<ParkingLocationResponse> createParking(@Valid @RequestBody ParkingLocationRequest request) {
        return ResponseEntity.ok(parkingService.createLocation(request));
    }

    @PutMapping("/parking/{id}")
    public ResponseEntity<ParkingLocationResponse> updateParking(
            @PathVariable Long id, @Valid @RequestBody ParkingLocationRequest request) {
        return ResponseEntity.ok(parkingService.updateLocation(id, request));
    }

    @PostMapping("/parking/{id}/floors")
    public ResponseEntity<Void> addFloor(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        parkingService.addFloor(id,
                (Integer) body.get("floorNumber"),
                (String) body.get("floorName"),
                (Integer) body.get("slotCount"),
                VehicleType.valueOf((String) body.get("vehicleType")));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(PageRequest.of(page, size)));
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id, @RequestParam boolean blocked) {
        adminService.blockUser(id, blocked);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLog>> auditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(auditService.getLogs(PageRequest.of(page, size)));
    }
}
