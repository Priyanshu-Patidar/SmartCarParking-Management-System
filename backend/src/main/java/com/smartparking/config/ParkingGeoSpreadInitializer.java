package com.smartparking.config;

import com.smartparking.entity.ParkingLocation;
import com.smartparking.repository.ParkingLocationRepository;
import com.smartparking.util.CityParkingAreas;
import com.smartparking.util.CityParkingAreas.Area;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spreads existing parking locations across different areas within each city.
 */
@Component
@Profile("dev")
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class ParkingGeoSpreadInitializer implements CommandLineRunner {

    private final ParkingLocationRepository locationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        AtomicInteger updated = new AtomicInteger(0);

        locationRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(ParkingLocation::getCity))
                .forEach((city, locations) -> {
                    List<ParkingLocation> sorted = locations.stream()
                            .sorted(Comparator.comparing(ParkingLocation::getId))
                            .toList();
                    List<Area> areas = CityParkingAreas.getAreas(city);

                    for (int i = 0; i < sorted.size(); i++) {
                        ParkingLocation loc = sorted.get(i);
                        Area area = areas.get(i % areas.size());
                        boolean changed = loc.getLatitude() != area.latitude()
                                || loc.getLongitude() != area.longitude();
                        loc.setLatitude(area.latitude());
                        loc.setLongitude(area.longitude());
                        loc.setAddress(area.address() + ", " + city);
                        locationRepository.save(loc);
                        if (changed) updated.incrementAndGet();
                    }
                });

        log.info("Geo spread: updated coordinates for {} parking locations across distinct areas", updated.get());
    }
}
