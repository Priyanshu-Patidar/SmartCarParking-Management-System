package com.smartparking.entity;

import com.smartparking.entity.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "parking_locations", indexes = {
        @Index(name = "idx_location_city", columnList = "city"),
        @Index(name = "idx_location_coords", columnList = "latitude, longitude")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE parking_locations SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Audited
public class ParkingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String zipCode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal peakHourRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal bikeRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal evRate;

    @Builder.Default
    private boolean evChargingAvailable = false;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean deleted = false;

    private LocalTime openTime;
    private LocalTime closeTime;

    @Column(length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "parking_vehicle_types", joinColumns = @JoinColumn(name = "location_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    @Builder.Default
    @org.hibernate.envers.NotAudited
    private Set<VehicleType> supportedVehicleTypes = new HashSet<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @org.hibernate.envers.NotAudited
    private List<ParkingFloor> floors = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @org.hibernate.envers.NotAudited
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public BigDecimal getPeakHourRate() { return peakHourRate; }
    public void setPeakHourRate(BigDecimal peakHourRate) { this.peakHourRate = peakHourRate; }
    public BigDecimal getBikeRate() { return bikeRate; }
    public void setBikeRate(BigDecimal bikeRate) { this.bikeRate = bikeRate; }
    public BigDecimal getEvRate() { return evRate; }
    public void setEvRate(BigDecimal evRate) { this.evRate = evRate; }
    public boolean isEvChargingAvailable() { return evChargingAvailable; }
    public void setEvChargingAvailable(boolean evChargingAvailable) { this.evChargingAvailable = evChargingAvailable; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }
    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<VehicleType> getSupportedVehicleTypes() { return supportedVehicleTypes; }
    public void setSupportedVehicleTypes(Set<VehicleType> supportedVehicleTypes) { this.supportedVehicleTypes = supportedVehicleTypes; }
    public List<ParkingFloor> getFloors() { return floors; }
    public void setFloors(List<ParkingFloor> floors) { this.floors = floors; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
