package com.smartparking.dto.request;

import com.smartparking.entity.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
public class ParkingLocationRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    private String state;
    private String zipCode;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    private String imageUrl;
    @NotNull
    private BigDecimal hourlyRate;
    private BigDecimal peakHourRate;
    private BigDecimal bikeRate;
    private BigDecimal evRate;
    private boolean evChargingAvailable;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String description;
    private Set<VehicleType> supportedVehicleTypes;
}
