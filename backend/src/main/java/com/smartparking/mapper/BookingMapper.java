package com.smartparking.mapper;

import com.smartparking.dto.response.BookingResponse;
import com.smartparking.entity.Booking;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        Hibernate.initialize(booking.getLocation());
        Hibernate.initialize(booking.getSlot());
        if (booking.getPayment() != null) {
            Hibernate.initialize(booking.getPayment());
        }
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .locationId(booking.getLocation().getId())
                .locationName(booking.getLocation().getName())
                .locationAddress(booking.getLocation().getAddress())
                .slotId(booking.getSlot().getId())
                .slotNumber(booking.getSlot().getSlotNumber())
                .vehicleType(booking.getVehicleType())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHours(booking.getDurationHours())
                .estimatedFee(booking.getEstimatedFee())
                .actualFee(booking.getActualFee())
                .status(booking.getStatus())
                .qrCodeData(booking.getQrCodeData())
                .vehicleNumber(booking.getVehicleNumber())
                .createdAt(booking.getCreatedAt())
                .paymentMethod(booking.getPayment() != null ? booking.getPayment().getPaymentMethod() : null)
                .paymentStatus(booking.getPayment() != null ? booking.getPayment().getStatus().name() : null)
                .transactionId(booking.getPayment() != null ? booking.getPayment().getTransactionId() : null)
                .build();
    }
}
