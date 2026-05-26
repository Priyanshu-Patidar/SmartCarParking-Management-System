package com.smartparking.repository;

import com.smartparking.entity.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitlistRepository extends JpaRepository<WaitlistEntry, Long> {
    List<WaitlistEntry> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndLocationId(Long userId, Long locationId);
}
