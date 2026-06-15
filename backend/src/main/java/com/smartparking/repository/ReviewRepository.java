package com.smartparking.repository;

import com.smartparking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.location.id = :locationId")
    Double getAverageRating(@Param("locationId") Long locationId);

    @Query("SELECT r.location.id, AVG(r.rating) FROM Review r GROUP BY r.location.id")
    List<Object[]> getAllAverageRatings();

    @Query("SELECT r.location.id, COUNT(r) FROM Review r GROUP BY r.location.id")
    List<Object[]> getAllReviewCounts();

    @Query("SELECT r.location.id, AVG(r.rating) FROM Review r WHERE r.location.id IN :locationIds GROUP BY r.location.id")
    List<Object[]> getAverageRatingsForLocations(@Param("locationIds") List<Long> locationIds);

    @Query("SELECT r.location.id, COUNT(r) FROM Review r WHERE r.location.id IN :locationIds GROUP BY r.location.id")
    List<Object[]> getReviewCountsForLocations(@Param("locationIds") List<Long> locationIds);
}
