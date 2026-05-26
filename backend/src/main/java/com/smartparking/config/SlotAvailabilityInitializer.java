package com.smartparking.config;

import com.smartparking.entity.ParkingSlot;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Releases slots stuck in RESERVED with no active booking (common after dev restarts).
 */
@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class SlotAvailabilityInitializer implements CommandLineRunner {

    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<ParkingSlot> reserved = slotRepository.findAll().stream()
                .filter(s -> s.getStatus() == SlotStatus.RESERVED)
                .toList();

        int released = 0;
        for (ParkingSlot slot : reserved) {
            boolean hasActiveBooking = bookingRepository.countOverlappingBookings(
                    slot.getId(),
                    List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE),
                    LocalDateTime.of(2000, 1, 1, 0, 0),
                    LocalDateTime.of(2099, 12, 31, 23, 59),
                    null) > 0;
            if (!hasActiveBooking) {
                slot.setStatus(SlotStatus.AVAILABLE);
                slotRepository.save(slot);
                released++;
            }
        }
        if (released > 0) {
            log.info("Released {} reserved slots back to AVAILABLE", released);
        }
    }
}
