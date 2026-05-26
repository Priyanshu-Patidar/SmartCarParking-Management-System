package com.smartparking.service;

import com.smartparking.analytics.FeeCalculationService;
import com.smartparking.dto.request.PreBookRequest;
import com.smartparking.dto.response.BookingResponse;
import com.smartparking.entity.*;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.PaymentStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.exception.BadRequestException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.mapper.BookingMapper;
import com.smartparking.repository.*;
import com.smartparking.util.QrCodeGenerator;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;
    private final PaymentRepository paymentRepository;
    private final FeeCalculationService feeCalculationService;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ParkingSessionRepository sessionRepository;

    @Transactional
    public BookingResponse preBook(PreBookRequest request) {
        if (request.getSlotId() == null) {
            throw new BadRequestException("Please select a parking slot");
        }
        if (request.getPayment() == null || request.getPayment().getPaymentMethod() == null
                || request.getPayment().getPaymentMethod().isBlank()) {
            throw new BadRequestException("Payment details are required");
        }

        User user = SecurityUtils.getCurrentUser();
        ParkingLocation location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        ParkingSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        LocalDateTime endTime = request.getStartTime().plusHours(request.getDurationHours());

        long overlaps = bookingRepository.countOverlappingBookings(
                slot.getId(),
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE),
                request.getStartTime(), endTime, null);
        if (overlaps > 0) {
            throw new BadRequestException("Slot already booked for selected time");
        }

        BigDecimal fee = feeCalculationService.calculateFee(
                location, request.getVehicleType(), request.getStartTime(), request.getDurationHours());

        String bookingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String qrPayload = "SPP:" + bookingCode + ":" + slot.getId();

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .user(user)
                .location(location)
                .slot(slot)
                .vehicleType(request.getVehicleType())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .durationHours(request.getDurationHours())
                .estimatedFee(fee)
                .status(BookingStatus.CONFIRMED)
                .vehicleNumber(request.getVehicleNumber())
                .qrCodeData(QrCodeGenerator.generateBase64Png(qrPayload))
                .build();

        slot.setStatus(SlotStatus.RESERVED);
        slotRepository.save(slot);
        bookingRepository.save(booking);

        sessionRepository.save(ParkingSession.builder()
                .booking(booking)
                .status("SCHEDULED")
                .build());

        String paymentMethod = request.getPayment().getPaymentMethod();
        String txnId = "TXN-" + bookingCode + "-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(fee)
                .status(PaymentStatus.PAID)
                .transactionId(txnId)
                .paymentMethod(paymentMethod)
                .build();
        paymentRepository.save(payment);
        booking.setPayment(payment);

        notificationService.notify(user, "Booking Confirmed",
                "Your parking at " + location.getName() + " is confirmed.", 
                com.smartparking.entity.enums.NotificationType.BOOKING_CONFIRMED);
        auditService.log(user.getEmail(), "BOOKING_CREATED", "Booking " + bookingCode);

        broadcastSlotUpdate(location.getId());
        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        User user = SecurityUtils.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId()) && 
            user.getRoles().stream().noneMatch(r -> r.getName().name().equals("ROLE_ADMIN"))) {
            throw new BadRequestException("Not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSlot().setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(booking.getSlot());
        bookingRepository.save(booking);

        notificationService.notify(booking.getUser(), "Booking Cancelled",
                "Booking " + booking.getBookingCode() + " has been cancelled.",
                com.smartparking.entity.enums.NotificationType.BOOKING_CANCELLED);

        broadcastSlotUpdate(booking.getLocation().getId());
        return bookingMapper.toResponse(booking);
    }

    public Page<BookingResponse> getUserBookings(Pageable pageable) {
        User user = SecurityUtils.getCurrentUser();
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(bookingMapper::toResponse);
    }

    public Page<BookingResponse> getAllBookings(Pageable pageable) {
        return bookingRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(bookingMapper::toResponse);
    }

    public BookingResponse getByCode(String code) {
        Booking booking = bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return bookingMapper.toResponse(booking);
    }

    public BigDecimal estimateFee(Long locationId, PreBookRequest request) {
        return estimateFee(locationId, request.getVehicleType(), request.getStartTime(), request.getDurationHours());
    }

    public BigDecimal estimateFee(Long locationId, com.smartparking.entity.enums.VehicleType vehicleType,
                                  LocalDateTime startTime, Integer durationHours) {
        ParkingLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return feeCalculationService.calculateFee(location, vehicleType, startTime, durationHours);
    }

    private void broadcastSlotUpdate(Long locationId) {
        messagingTemplate.convertAndSend("/topic/parking/" + locationId + "/slots",
                java.util.Map.of("locationId", locationId, "updatedAt", LocalDateTime.now().toString()));
    }
}
