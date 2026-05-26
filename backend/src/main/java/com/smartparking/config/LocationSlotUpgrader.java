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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Upgrades legacy locations (e.g. only 30 slots with few EV) to 60 slots with 20 EV per location.
 */
@Component
@Profile("dev")
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class LocationSlotUpgrader implements CommandLineRunner {

    private final ParkingLocationRepository locationRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;

    @Override
    @Transactional
    public void run(String... args) {
        for (ParkingLocation location : locationRepository.findAll()) {
            long slotCount = slotRepository.findByLocationId(location.getId()).size();
            if (slotCount >= 50) continue;

            log.info("Upgrading slots for location: {} (had {} slots)", location.getName(), slotCount);
            int nextFloor = floorRepository.findByLocationId(location.getId()).stream()
                    .mapToInt(ParkingFloor::getFloorNumber)
                    .max().orElse(0) + 1;

            ParkingFloor floor = ParkingFloor.builder()
                    .location(location)
                    .floorNumber(nextFloor)
                    .floorName("Floor " + nextFloor + " (expanded)")
                    .build();

            List<ParkingSlot> slots = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                slots.add(slot(floor, nextFloor, "C", i, VehicleType.CAR));
                slots.add(slot(floor, nextFloor, "B", i, VehicleType.BIKE));
                slots.add(slot(floor, nextFloor, "E", i, VehicleType.EV));
            }
            floor.setSlots(slots);
            floorRepository.save(floor);
        }
    }

    private ParkingSlot slot(ParkingFloor floor, int floorNum, String prefix, int index, VehicleType type) {
        return ParkingSlot.builder()
                .floor(floor)
                .slotNumber(floorNum + "-" + prefix + String.format("%03d", index))
                .vehicleType(type)
                .status(SlotStatus.AVAILABLE)
                .evCharging(type == VehicleType.EV)
                .build();
    }
}
