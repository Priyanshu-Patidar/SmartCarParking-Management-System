package com.smartparking.dto.response;

import com.smartparking.entity.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLocationResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private BigDecimal hourlyRate;
    private BigDecimal peakHourRate;
    private BigDecimal bikeRate;
    private BigDecimal evRate;
    private boolean evChargingAvailable;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String description;
    private Set<VehicleType> supportedVehicleTypes;
    private int totalSlots;
    private int availableSlots;
    private int occupiedSlots;
    private int reservedSlots;
    private Double distanceKm;
    private Double averageRating;
    private Long reviewCount;
    private boolean favorite;

    public Long getId() { return id; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public int getAvailableSlots() { return availableSlots; }
    public Double getAverageRating() { return averageRating; }
    public Double getDistanceKm() { return distanceKm; }
    public boolean isEvChargingAvailable() { return evChargingAvailable; }
}
