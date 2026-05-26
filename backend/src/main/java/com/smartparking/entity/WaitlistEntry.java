package com.smartparking.entity;

import com.smartparking.entity.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private ParkingLocation location;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private LocalDateTime preferredStartTime;
    private Integer durationHours;

    @Builder.Default
    private boolean notified = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
