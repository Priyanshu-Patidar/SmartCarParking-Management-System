package com.smartparking.config;

import com.smartparking.entity.ParkingFloor;
import com.smartparking.entity.ParkingLocation;
import com.smartparking.entity.ParkingSlot;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import com.smartparking.repository.ParkingFloorRepository;
import com.smartparking.repository.ParkingLocationRepository;
import com.smartparking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Ensures all existing parking locations have floors and slots (migration support).
 */
@Component
@Profile("dev")
@Order(3)
@RequiredArgsConstructor
public class LocationSlotUpgrader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LocationSlotUpgrader.class);
    private final ParkingLocationRepository locationRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<ParkingLocation> locations = locationRepository.findAll();
        int upgraded = 0;

        for (ParkingLocation loc : locations) {
            if (loc.getFloors().isEmpty()) {
                // Add 2 floors by default
                addFloor(loc, 1, "Level 1", 30, VehicleType.CAR);
                addFloor(loc, 2, "Level 2", 20, VehicleType.BIKE);
                addFloor(loc, 3, "EV Zone", 10, VehicleType.EV);
                upgraded++;
            }
        }

        if (upgraded > 0) {
            log.info("Upgraded {} parking locations with default floors and slots", upgraded);
        }
    }

    private void addFloor(ParkingLocation loc, int floorNum, String name, int count, VehicleType type) {
        ParkingFloor floor = ParkingFloor.builder()
                .location(loc)
                .floorNumber(floorNum)
                .floorName(name)
                .build();
        
        for (int i = 1; i <= count; i++) {
            String prefix = type == VehicleType.CAR ? "C" : (type == VehicleType.BIKE ? "B" : "E");
            ParkingSlot slot = ParkingSlot.builder()
                    .floor(floor)
                    .slotNumber(floorNum + "-" + prefix + String.format("%03d", i))
                    .vehicleType(type)
                    .status(SlotStatus.AVAILABLE)
                    .evCharging(type == VehicleType.EV)
                    .build();
            floor.getSlots().add(slot);
        }
        floorRepository.save(floor);
    }
}
