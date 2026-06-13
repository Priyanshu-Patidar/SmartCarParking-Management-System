package com.smartparking.dto.request;

import com.smartparking.entity.enums.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreBookRequest {
    @NotNull
    private Long locationId;
    private Long slotId;
    @NotNull
    private VehicleType vehicleType;
    @NotNull @jakarta.validation.constraints.FutureOrPresent
    private LocalDateTime startTime;
    @NotNull @Min(1)
    private Integer durationHours;
    private String vehicleNumber;
    @Valid
    private PaymentDetailsRequest payment;

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public PaymentDetailsRequest getPayment() { return payment; }
    public void setPayment(PaymentDetailsRequest payment) { this.payment = payment; }
}
