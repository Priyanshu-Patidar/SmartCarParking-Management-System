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
    @Transactional
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

        Role adminRole = roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build());
        Role userRole = roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build());

        userRepository.save(User.builder()
                .fullName("Admin User")
                .email("admin@smartparking.com")
                .password(passwordEncoder.encode("Admin@123"))
                .phone("9999999999")
                .roles(Set.of(adminRole, userRole))
                .emailVerified(true)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .fullName("Demo User")
                .email("user@smartparking.com")
                .password(passwordEncoder.encode("User@123"))
                .phone("8888888888")
                .roles(Set.of(userRole))
                .emailVerified(true)
                .enabled(true)
                .build());

        for (int i = 1; i <= 5; i++) {
            userRepository.save(User.builder()
                    .fullName("Dummy User " + i)
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("777777770" + i)
                    .roles(Set.of(userRole))
                    .emailVerified(true)
                    .enabled(true)
                    .build());
        }
    }

    private void seedLocations() {
        int created = 0;
        for (String[] cityData : CITIES) {
            String city = cityData[0];
            for (int i = 0; i < LOCATIONS_PER_CITY; i++) {
                String suffix = CityParkingAreas.LOCATION_SUFFIXES[i];
                String name = city + " " + suffix;
                if (locationRepository.existsByNameAndCity(name, city)) {
                    continue;
                }

                Area area = CityParkingAreas.getArea(city, i);
                BigDecimal rate = BigDecimal.valueOf(25 + (i * 5) + (Math.abs(city.hashCode()) % 20));
                boolean evHub = i == 10 || i % 3 == 0;

                seedLocation(name, area.address() + ", " + city, city,
                        area.latitude(), area.longitude(), rate, evHub);
                created++;
            }
        }
        log.info("Created {} new parking locations. Total: {}", created, locationRepository.count());
    }

    private void seedAnalyticsData() {
        log.info("Seeding analytics data...");
        List<User> users = userRepository.findAll().stream().filter(u -> !u.getEmail().contains("admin")).toList();
        List<ParkingLocation> locations = locationRepository.findAll();
        Random random = new Random();

        if (users.isEmpty() || locations.isEmpty()) return;

        for (int i = 0; i < 500; i++) {
            User user = users.get(random.nextInt(users.size()));
            ParkingLocation location = locations.get(random.nextInt(locations.size()));
            List<ParkingSlot> slots = slotRepository.findByLocationId(location.getId());
            
            if (slots == null || slots.isEmpty()) {
                continue;
            }
            
            ParkingSlot slot = slots.get(random.nextInt(slots.size()));

            LocalDateTime start = LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24));
            int duration = 1 + random.nextInt(8);
            LocalDateTime end = start.plusHours(duration);
            BigDecimal fee = location.getHourlyRate().multiply(BigDecimal.valueOf(duration));

            Booking booking = Booking.builder()
                    .bookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .user(user)
                    .location(location)
                    .slot(slot)
                    .vehicleType(slot.getVehicleType())
                    .startTime(start)
                    .endTime(end)
                    .durationHours(duration)
                    .estimatedFee(fee)
                    .actualFee(fee)
                    .status(BookingStatus.COMPLETED)
                    .vehicleNumber("MH" + (10 + random.nextInt(90)) + "AB" + (1000 + random.nextInt(8999)))
                    .createdAt(start.minusMinutes(30))
                    .build();
            bookingRepository.save(booking);

            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(fee)
                    .status(PaymentStatus.PAID)
                    .transactionId("TXN-" + booking.getBookingCode())
                    .paymentMethod(random.nextBoolean() ? "UPI" : "CARD")
                    .paidAt(start.minusMinutes(25))
                    .build();
            paymentRepository.save(payment);

            if (random.nextInt(10) > 6) {
                reviewRepository.save(Review.builder()
                        .user(user)
                        .location(location)
                        .rating(3 + random.nextInt(3))
                        .comment("Great facility and easy to use!")
                        .build());
            }
        }

        for (int i = 0; i < 100; i++) {
            User user = users.get(random.nextInt(users.size()));
            String[] queries = {"Mumbai", "Airport", "Mall", "Railway Station", "Central"};
            SearchHistory history = new SearchHistory();
            history.setUser(user);
            history.setQuery(queries[random.nextInt(queries.length)]);
            history.setSearchedAt(LocalDateTime.now().minusDays(random.nextInt(10)));
            searchHistoryRepository.save(history);
        }

        for (int i = 0; i < 50; i++) {
            User user = users.get(random.nextInt(users.size()));
            String[] actions = {"USER_LOGIN", "PROFILE_UPDATE", "PASSWORD_CHANGE"};
            auditLogRepository.save(AuditLog.builder()
                    .userEmail(user.getEmail())
                    .action(actions[random.nextInt(actions.length)])
                    .details("Activity performed from local IP")
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(5)))
                    .build());
        }

        log.info("Successfully seeded 500 bookings, payments, and reviews for analytics.");
    }

    private void seedLocation(String name, String address, String city, double lat, double lng,
                              BigDecimal rate, boolean ev) {
        ParkingLocation location = ParkingLocation.builder()
                .name(name)
                .address(address)
                .city(city)
                .latitude(lat)
                .longitude(lng)
                .hourlyRate(rate)
                .evChargingAvailable(ev)
                .supportedVehicleTypes(Set.of(VehicleType.CAR, VehicleType.BIKE, VehicleType.EV))
                .openTime(LocalTime.of(6, 0))
                .closeTime(LocalTime.of(23, 59))
                .build();
        locationRepository.save(location);
    }
}
