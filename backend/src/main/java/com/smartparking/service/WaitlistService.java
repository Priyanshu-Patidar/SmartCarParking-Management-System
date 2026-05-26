package com.smartparking.service;

import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.User;
import com.smartparking.entity.WaitlistEntry;
import com.smartparking.entity.enums.NotificationType;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.exception.BadRequestException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.ParkingLocationRepository;
import com.smartparking.repository.WaitlistRepository;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final ParkingLocationRepository locationRepository;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> joinWaitlist(Long locationId, VehicleType vehicleType,
                                            LocalDateTime preferredStart, Integer durationHours) {
        User user = SecurityUtils.getCurrentUser();
        if (waitlistRepository.existsByUserIdAndLocationId(user.getId(), locationId)) {
            throw new BadRequestException("You are already on the waitlist for this facility");
        }
        ParkingLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        WaitlistEntry entry = waitlistRepository.save(WaitlistEntry.builder()
                .user(user)
                .location(location)
                .vehicleType(vehicleType)
                .preferredStartTime(preferredStart)
                .durationHours(durationHours)
                .build());

        notificationService.notify(user, "Waitlist confirmed",
                "We will notify you when a slot opens at " + location.getName() + ".",
                NotificationType.SYSTEM);

        return Map.of(
                "id", entry.getId(),
                "message", "You have been added to the waitlist. We'll alert you when capacity opens."
        );
    }

    public List<WaitlistEntry> getMyWaitlist() {
        return waitlistRepository.findByUserIdOrderByCreatedAtDesc(SecurityUtils.getCurrentUser().getId());
    }
}
