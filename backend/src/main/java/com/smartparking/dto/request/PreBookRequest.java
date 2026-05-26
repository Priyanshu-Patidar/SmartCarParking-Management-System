package com.smartparking.dto.request;

import com.smartparking.entity.enums.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PreBookRequest {
    @NotNull
    private Long locationId;
    private Long slotId;
    @NotNull
    private VehicleType vehicleType;
    @NotNull @Future
    private LocalDateTime startTime;
    @NotNull @Min(1)
    private Integer durationHours;
    private String vehicleNumber;
    @Valid
    private PaymentDetailsRequest payment;
}
