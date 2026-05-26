package com.smartparking.entity;

import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_slots", indexes = {
        @Index(name = "idx_slot_status", columnList = "status"),
        @Index(name = "idx_slot_floor", columnList = "floor_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Builder.Default
    private boolean evCharging = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private ParkingFloor floor;
}
