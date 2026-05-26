package com.smartparking.config;

import com.smartparking.entity.Booking;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulingConfig {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository slotRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookingsAndFreeSlots() {
        List<Booking> expired = bookingRepository.findByStatusAndEndTimeBefore(
                BookingStatus.CONFIRMED, LocalDateTime.now());
        for (Booking b : expired) {
            b.setStatus(BookingStatus.COMPLETED);
            b.getSlot().setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(b.getSlot());
            bookingRepository.save(b);
            messagingTemplate.convertAndSend("/topic/parking/" + b.getLocation().getId() + "/slots",
                    java.util.Map.of("locationId", b.getLocation().getId()));
        }
    }
}
