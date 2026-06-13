package com.smartparking.dto.response;

import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotResponse {
    private Long id;
    private String slotNumber;
    private SlotStatus status;
    private VehicleType vehicleType;
    private boolean evCharging;
    private Integer floorNumber;
    private String floorName;
}
