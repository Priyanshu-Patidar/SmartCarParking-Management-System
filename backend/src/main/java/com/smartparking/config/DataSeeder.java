package com.smartparking.config;

import com.smartparking.entity.*;
import com.smartparking.entity.enums.*;
import com.smartparking.repository.*;
import com.smartparking.util.CityParkingAreas;
import com.smartparking.util.CityParkingAreas.Area;
import com.smartparking.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final int LOCATIONS_PER_CITY = 11;
    private static final int TARGET_LOCATIONS = 15 * LOCATIONS_PER_CITY;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ParkingLocationRepository locationRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final AuditLogRepository auditLogRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String[][] CITIES = {
            {"Mumbai"}, {"Bangalore"}, {"Delhi"}, {"Pune"}, {"Hyderabad"},
            {"Chennai"}, {"Kolkata"}, {"Ahmedabad"}, {"Jaipur"}, {"Lucknow"},
            {"Chandigarh"}, {"Kochi"}, {"Indore"}, {"Nagpur"}, {"Surat"},
    };

    @Override
    public void run(String... args) {
        seedUsersIfNeeded();

        long existing = locationRepository.count();
        if (existing < TARGET_LOCATIONS) {
            log.info("Seeding parking data current: {}, target: {}", existing, TARGET_LOCATIONS);
            seedLocations();
        } else {
            log.info("Parking data already seeded ({} locations)", existing);
        }

        if (bookingRepository.count() == 0) {
            seedAnalyticsData();
        }
    }

    private void seedUsersIfNeeded() {
        if (roleRepository.count() > 0) return;

        try {
            Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build()));
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build()));

            if (!userRepository.existsByEmail("admin@smartparking.com")) {
                userRepository.save(User.builder()
                        .fullName("Admin User")
                        .email("admin@smartparking.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .phone("9999999999")
                        .roles(Set.of(adminRole, userRole))
                        .emailVerified(true)
                        .enabled(true)
                        .build());
            }

            if (!userRepository.existsByEmail("user@smartparking.com")) {
                userRepository.save(User.builder()
                        .fullName("Demo User")
                        .email("user@smartparking.com")
                        .password(passwordEncoder.encode("User@123"))
                        .phone("8888888888")
                        .roles(Set.of(userRole))
                        .emailVerified(true)
                        .enabled(true)
                        .build());
            }

            for (int i = 1; i <= 5; i++) {
                String dummyEmail = "user" + i + "@example.com";
                if (!userRepository.existsByEmail(dummyEmail)) {
                    userRepository.save(User.builder()
                            .fullName("Dummy User " + i)
                            .email(dummyEmail)
                            .password(passwordEncoder.encode("Password@123"))
                            .phone("777777770" + i)
                            .roles(Set.of(userRole))
                            .emailVerified(true)
                            .enabled(true)
                            .build());
                }
            }
        } catch (Exception e) {
            log.warn("Skipping user seeding, likely locked by another instance: {}", e.getMessage());
        }
    }

    private void seedLocations() {
        List<ParkingLocation> locationsToSave = new ArrayList<>();
        for (String[] cityData : CITIES) {
            String city = cityData[0];
            for (int i = 0; i < LOCATIONS_PER_CITY; i++) {
                String suffix = CityParkingAreas.LOCATION_SUFFIXES[i];
                String name = city + " " + suffix;
                if (locationRepository.existsByNameAndCity(name, city)) continue;

                Area area = CityParkingAreas.getArea(city, i);
                BigDecimal rate = BigDecimal.valueOf(25 + (i * 5) + (Math.abs(city.hashCode()) % 20));
                boolean evHub = i == 10 || i % 3 == 0;

                locationsToSave.add(ParkingLocation.builder()
                        .name(name).address(area.address() + ", " + city).city(city)
                        .latitude(area.latitude()).longitude(area.longitude())
                        .hourlyRate(rate).evChargingAvailable(evHub)
                        .supportedVehicleTypes(Set.of(VehicleType.CAR, VehicleType.BIKE, VehicleType.EV))
                        .openTime(LocalTime.of(6, 0)).closeTime(LocalTime.of(23, 59))
                        .build());
            }
        }
        
        if (locationsToSave.isEmpty()) return;
        
        List<ParkingLocation> savedLocations = locationRepository.saveAll(locationsToSave);
        List<ParkingFloor> floorsToSave = new ArrayList<>();
        
        for (ParkingLocation loc : savedLocations) {
            floorsToSave.add(ParkingFloor.builder()
                    .floorNumber(1).floorName("Ground Floor").location(loc)
                    .build());
        }
        
        List<ParkingFloor> savedFloors = floorRepository.saveAll(floorsToSave);
        List<ParkingSlot> slotsToSave = new ArrayList<>();
        
        for (ParkingFloor floor : savedFloors) {
            boolean ev = floor.getLocation().isEvChargingAvailable();
            int counter = 1;
            for (int i = 0; i < 5; i++) {
                slotsToSave.add(ParkingSlot.builder().slotNumber("C-"+(counter++)).status(SlotStatus.AVAILABLE).vehicleType(VehicleType.CAR).floor(floor).build());
            }
            for (int i = 0; i < 5; i++) {
                slotsToSave.add(ParkingSlot.builder().slotNumber("B-"+(counter++)).status(SlotStatus.AVAILABLE).vehicleType(VehicleType.BIKE).floor(floor).build());
            }
            if (ev) {
                for (int i = 0; i < 5; i++) {
                    slotsToSave.add(ParkingSlot.builder().slotNumber("E-"+(counter++)).status(SlotStatus.AVAILABLE).vehicleType(VehicleType.EV).evCharging(true).floor(floor).build());
                }
            }
        }
        slotRepository.saveAll(slotsToSave);
        log.info("Successfully bulk-seeded {} locations and {} slots.", savedLocations.size(), slotsToSave.size());
    }

    private void seedAnalyticsData() {
        log.info("Seeding condensed analytics data for production performance...");
        List<User> users = userRepository.findAll().stream().filter(u -> !u.getEmail().contains("admin")).limit(5).toList();
        List<ParkingLocation> locations = locationRepository.findAll().stream().limit(20).toList();
        Random random = new Random();

        if (users.isEmpty() || locations.isEmpty()) return;

        List<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            User user = users.get(random.nextInt(users.size()));
            ParkingLocation location = locations.get(random.nextInt(locations.size()));
            List<ParkingSlot> slots = slotRepository.findByLocationId(location.getId());
            if (slots.isEmpty()) continue;
            
            ParkingSlot slot = slots.get(random.nextInt(slots.size()));
            LocalDateTime start = LocalDateTime.now().minusDays(random.nextInt(10)).minusHours(random.nextInt(24));
            int duration = 2;
            BigDecimal fee = location.getHourlyRate().multiply(BigDecimal.valueOf(duration));

            bookings.add(Booking.builder()
                    .bookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .user(user).location(location).slot(slot).vehicleType(slot.getVehicleType())
                    .startTime(start).endTime(start.plusHours(duration)).durationHours(duration)
                    .estimatedFee(fee).actualFee(fee).status(BookingStatus.COMPLETED)
                    .vehicleNumber("MH" + (10 + random.nextInt(90)) + "AB1234")
                    .createdAt(start.minusMinutes(30)).build());
        }
        List<Booking> savedBookings = bookingRepository.saveAll(bookings);
        List<Payment> payments = savedBookings.stream().map(b -> Payment.builder()
                .booking(b).amount(b.getActualFee()).status(PaymentStatus.PAID)
                .transactionId("TXN-"+b.getBookingCode()).paymentMethod("UPI").paidAt(b.getStartTime()).build()
        ).toList();
        paymentRepository.saveAll(payments);
        log.info("Analytics seeding complete.");
    }

    private void seedLocation(String name, String address, String city, double lat, double lng,
                              BigDecimal rate, boolean ev) {
        // Method replaced by bulk seedLocations logic
    }
}
