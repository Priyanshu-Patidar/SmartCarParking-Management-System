package com.smartparking.repository;

import com.smartparking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.roles",
           countQuery = "SELECT COUNT(u) FROM User u")
    Page<User> findAllWithRoles(Pageable pageable);
}
