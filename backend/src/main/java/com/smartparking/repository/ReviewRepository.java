package com.smartparking.repository;

import com.smartparking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.location.id = :locationId")
    Double getAverageRating(@Param("locationId") Long locationId);

    long countByLocationId(Long locationId);
}
