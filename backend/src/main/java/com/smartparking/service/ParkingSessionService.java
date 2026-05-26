package com.smartparking.service;

import com.smartparking.analytics.FeeCalculationService;
import com.smartparking.entity.Booking;
import com.smartparking.entity.ParkingSession;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.exception.BadRequestException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSessionRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {

    private final ParkingSessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository slotRepository;
    private final FeeCalculationService feeCalculationService;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> checkIn(Long bookingId) {
        Booking booking = getAuthorizedBooking(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed bookings can be checked in");
        }
        ParkingSession session = sessionRepository.findByBookingId(bookingId)
                .orElse(ParkingSession.builder().booking(booking).build());
        session.setCheckInTime(LocalDateTime.now());
        session.setStatus("ACTIVE");
        booking.setStatus(BookingStatus.ACTIVE);
        booking.getSlot().setStatus(SlotStatus.OCCUPIED);
        slotRepository.save(booking.getSlot());
        bookingRepository.save(booking);
        sessionRepository.save(session);

        return Map.of(
                "status", "ACTIVE",
                "checkInTime", session.getCheckInTime().toString(),
                "message", "Parking session started. Your slot is now active."
        );
    }

    @Transactional
    public Map<String, Object> checkOut(Long bookingId) {
        Booking booking = getAuthorizedBooking(bookingId);
        ParkingSession session = sessionRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("No active session found"));

        LocalDateTime checkOut = LocalDateTime.now();
        session.setCheckOutTime(checkOut);
        session.setStatus("COMPLETED");

        long hours = Math.max(1, ChronoUnit.HOURS.between(
                session.getCheckInTime() != null ? session.getCheckInTime() : booking.getStartTime(),
                checkOut));
        BigDecimal finalFee = feeCalculationService.calculateFee(
                booking.getLocation(), booking.getVehicleType(), booking.getStartTime(), (int) hours);
        session.setFinalAmount(finalFee);
        booking.setActualFee(finalFee);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.getSlot().setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(booking.getSlot());
        bookingRepository.save(booking);
        sessionRepository.save(session);

        notificationService.notify(booking.getUser(), "Session completed",
                "Your parking session ended. Total: ₹" + finalFee,
                com.smartparking.entity.enums.NotificationType.PAYMENT_RECEIVED);

        return Map.of(
                "status", "COMPLETED",
                "finalAmount", finalFee,
                "durationHours", hours,
                "message", "Thank you for using SmartPark. Receipt updated in your bookings."
        );
    }

    private Booking getAuthorizedBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(SecurityUtils.getCurrentUser().getId())) {
            throw new BadRequestException("Unauthorized");
        }
        return booking;
    }
}
