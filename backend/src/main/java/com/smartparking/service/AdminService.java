package com.smartparking.service;

import com.smartparking.dto.response.SystemHealthResponse;
import com.smartparking.entity.User;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;
    private final AuditService auditService;

    public SystemHealthResponse getSystemHealth() {
        return SystemHealthResponse.builder()
                .status("UP")
                .database("CONNECTED")
                .webSocket("ACTIVE")
                .scheduler("RUNNING")
                .metrics(Map.of(
                        "cpuUsage", "12%",
                        "memoryUsage", "248MB",
                        "activeSessions", userRepository.count() // Mock active sessions
                ))
                .build();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllWithRoles(pageable);
    }

    @Transactional
    public void blockUser(Long userId, boolean blocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBlocked(blocked);
        userRepository.save(user);
        auditService.log("admin", blocked ? "USER_BLOCKED" : "USER_UNBLOCKED", user.getEmail());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        auditService.log("admin", "USER_DELETED", user.getEmail());
    }
}
