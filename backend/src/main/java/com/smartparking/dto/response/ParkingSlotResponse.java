package com.smartparking.dto.response;

import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingSlotResponse {
    private Long id;
    private String slotNumber;
    private SlotStatus status;
    private VehicleType vehicleType;
    private boolean evCharging;
    private Integer floorNumber;
    private String floorName;
}
