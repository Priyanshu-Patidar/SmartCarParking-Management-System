package com.smartparking.repository;

import com.smartparking.entity.Booking;
import com.smartparking.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slot.id = :slotId AND b.status IN :statuses " +
           "AND b.startTime < :endTime AND b.endTime > :startTime AND (:excludeId IS NULL OR b.id <> :excludeId)")
    long countOverlappingBookings(
            @Param("slotId") Long slotId,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId);

    @Query("SELECT COALESCE(SUM(b.actualFee), SUM(b.estimatedFee)) FROM Booking b WHERE b.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :since")
    long countBookingsSince(@Param("since") LocalDateTime since);

    List<Booking> findByStatusAndEndTimeBefore(BookingStatus status, LocalDateTime endTime);
}
