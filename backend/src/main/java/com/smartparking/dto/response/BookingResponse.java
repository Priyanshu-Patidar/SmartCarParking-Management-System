package com.smartparking.dto.response;

import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long locationId;
    private String locationName;
    private String locationAddress;
    private Long slotId;
    private String slotNumber;
    private VehicleType vehicleType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationHours;
    private BigDecimal estimatedFee;
    private BigDecimal actualFee;
    private BookingStatus status;
    private String qrCodeData;
    private String vehicleNumber;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
}
