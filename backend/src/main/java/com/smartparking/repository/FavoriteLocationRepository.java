package com.smartparking.repository;

import com.smartparking.entity.FavoriteLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteLocationRepository extends JpaRepository<FavoriteLocation, Long> {
    List<FavoriteLocation> findByUserId(Long userId);
    Optional<FavoriteLocation> findByUserIdAndLocationId(Long userId, Long locationId);
    boolean existsByUserIdAndLocationId(Long userId, Long locationId);
    void deleteByUserIdAndLocationId(Long userId, Long locationId);
}
