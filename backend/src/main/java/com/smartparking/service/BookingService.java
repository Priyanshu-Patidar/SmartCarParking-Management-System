package com.smartparking.service;

import com.smartparking.analytics.FeeCalculationService;
import com.smartparking.dto.request.PreBookRequest;
import com.smartparking.dto.response.BookingResponse;
import com.smartparking.entity.*;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.PaymentStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.event.BookingCreatedEvent;
import com.smartparking.event.PaymentCompletedEvent;
import com.smartparking.exception.BadRequestException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.mapper.BookingMapper;
import com.smartparking.repository.*;
import com.smartparking.util.QrCodeGenerator;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
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

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;
    private final PaymentRepository paymentRepository;
    private final FeeCalculationService feeCalculationService;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final ParkingSessionRepository sessionRepository;

    @Transactional
    public BookingResponse preBook(PreBookRequest request) {
        try {
            log.info("START: Processing pre-book request for user: {}, location: {}, slot: {}", 
                    SecurityUtils.getCurrentUser().getEmail(), request.getLocationId(), request.getSlotId());
            
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
            
            // Use Pessimistic Lock to prevent concurrent bookings
            ParkingSlot slot = slotRepository.findByIdForUpdate(request.getSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

            LocalDateTime startTime = request.getStartTime();
            LocalDateTime endTime = startTime.plusHours(request.getDurationHours());

            log.info("Checking overlaps for slot {} from {} to {}", slot.getId(), startTime, endTime);

            long overlaps = bookingRepository.countOverlappingBookings(
                    slot.getId(),
                    List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE),
                    startTime, endTime, null);
            
            if (overlaps > 0) {
                log.warn("CONFLICT: Slot {} already booked for selected time", slot.getId());
                throw new BadRequestException("Slot already booked for selected time");
            }

            BigDecimal fee = feeCalculationService.calculateFee(
                    location, request.getVehicleType(), startTime, request.getDurationHours());

            String bookingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String qrPayload = "SPP:" + bookingCode + ":" + slot.getId();

            Booking booking = Booking.builder()
                    .bookingCode(bookingCode)
                    .user(user)
                    .location(location)
                    .slot(slot)
                    .vehicleType(request.getVehicleType())
                    .startTime(startTime)
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

            ParkingSession session = ParkingSession.builder()
                    .booking(booking)
                    .status("SCHEDULED")
                    .build();
            sessionRepository.save(session);

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

            log.info("SUCCESS: Booking {} created. Publishing events...", bookingCode);

            try {
                eventPublisher.publishEvent(new BookingCreatedEvent(
                        booking.getId(), booking.getBookingCode(), user.getEmail(), location.getName()));
                eventPublisher.publishEvent(new PaymentCompletedEvent(
                        payment.getId(), payment.getTransactionId(), payment.getAmount(), user.getEmail()));
            } catch (Exception e) {
                log.error("EVENT ERROR: Failed to publish booking events: {}", e.getMessage());
            }

            broadcastSlotUpdate(location.getId());
            return bookingMapper.toResponse(booking);

        } catch (Exception e) {
            log.error("CRITICAL BOOKING FAILURE: {}", e.getMessage(), e);
            throw e;
        }
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
        return bookingRepository.findByBookingCode(code)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        User user = SecurityUtils.getCurrentUser();
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Cannot cancel booking in current state: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getSlot().setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(booking.getSlot());
        bookingRepository.save(booking);

        broadcastSlotUpdate(booking.getLocation().getId());
        return bookingMapper.toResponse(booking);
    }

    public BigDecimal estimateFee(Long locationId, com.smartparking.entity.enums.VehicleType vehicleType,
                                  LocalDateTime startTime, Integer durationHours) {
        ParkingLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return feeCalculationService.calculateFee(location, vehicleType, startTime, durationHours);
    }

    public com.smartparking.dto.response.PricingBreakdownResponse estimateDetailedFee(Long locationId, com.smartparking.entity.enums.VehicleType vehicleType,
                                                                                      LocalDateTime startTime, Integer durationHours) {
        ParkingLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return feeCalculationService.calculateDetailedBreakdown(location, vehicleType, startTime, durationHours);
    }

    private void broadcastSlotUpdate(Long locationId) {
        messagingTemplate.convertAndSend("/topic/parking/" + locationId + "/slots",
                java.util.Map.of("locationId", locationId, "updatedAt", LocalDateTime.now().toString()));
    }
}
