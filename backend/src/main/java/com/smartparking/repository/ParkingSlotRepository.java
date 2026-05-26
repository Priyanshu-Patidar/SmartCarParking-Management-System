package com.smartparking.repository;

import com.smartparking.entity.ParkingSlot;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.entity.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Query("SELECT s FROM ParkingSlot s JOIN s.floor f WHERE f.location.id = :locationId")
    List<ParkingSlot> findByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(s) FROM ParkingSlot s JOIN s.floor f WHERE f.location.id = :locationId AND s.status = :status")
    long countByLocationIdAndStatus(@Param("locationId") Long locationId, @Param("status") SlotStatus status);

    @Query("""
            SELECT s FROM ParkingSlot s JOIN FETCH s.floor f
            WHERE f.location.id = :locationId
            AND s.vehicleType = :vehicleType
            AND s.status IN :slotStatuses
            AND NOT EXISTS (
                SELECT 1 FROM Booking b
                WHERE b.slot = s
                AND b.status IN :bookingStatuses
                AND b.startTime < :endTime AND b.endTime > :startTime
            )
            """)
    List<ParkingSlot> findAvailableSlots(
            @Param("locationId") Long locationId,
            @Param("vehicleType") VehicleType vehicleType,
            @Param("slotStatuses") List<SlotStatus> slotStatuses,
            @Param("bookingStatuses") List<com.smartparking.entity.enums.BookingStatus> bookingStatuses,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("""
            SELECT s FROM ParkingSlot s JOIN FETCH s.floor f
            WHERE f.location.id = :locationId
            AND s.vehicleType = :vehicleType
            AND s.status <> com.smartparking.entity.enums.SlotStatus.OCCUPIED
            AND s.status <> com.smartparking.entity.enums.SlotStatus.MAINTENANCE
            """)
    List<ParkingSlot> findByLocationIdAndVehicleType(
            @Param("locationId") Long locationId,
            @Param("vehicleType") VehicleType vehicleType);

    Optional<ParkingSlot> findByIdAndStatus(Long id, SlotStatus status);
}
