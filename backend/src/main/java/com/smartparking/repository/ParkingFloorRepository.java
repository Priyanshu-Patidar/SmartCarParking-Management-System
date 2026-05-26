package com.smartparking.repository;

import com.smartparking.entity.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    List<ParkingFloor> findByLocationId(Long locationId);
}
