package com.smartparking.repository;

import com.smartparking.entity.ParkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingLocationRepository extends JpaRepository<ParkingLocation, Long> {

    boolean existsByNameAndCity(String name, String city);

    List<ParkingLocation> findByActiveTrueAndCityContainingIgnoreCase(String city);

    @Query("SELECT l FROM ParkingLocation l WHERE l.active = true AND " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(l.address) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(l.city) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<ParkingLocation> searchByQuery(@Param("q") String query);

    @Query("SELECT l FROM ParkingLocation l WHERE l.active = true")
    List<ParkingLocation> findAllActive();
}
