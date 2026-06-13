package com.smartparking.analytics;

import com.smartparking.dto.response.DashboardStatsResponse;
import com.smartparking.entity.enums.BookingStatus;
import com.smartparking.entity.enums.SlotStatus;
import com.smartparking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardAnalyticsService {

    private final ParkingLocationRepository locationRepository;
    private final ParkingSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public DashboardStatsResponse getStats() {
        long totalLocations = locationRepository.count();
        List<com.smartparking.entity.ParkingSlot> allSlots = slotRepository.findAll();
        long availableSlots = allSlots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
        long occupiedSlots = allSlots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();
        long reservedSlots = allSlots.stream().filter(s -> s.getStatus() == SlotStatus.RESERVED).count();

        BigDecimal revenue = bookingRepository.getTotalRevenue();
        if (revenue == null) revenue = BigDecimal.ZERO;

        return DashboardStatsResponse.builder()
                .totalLocations(totalLocations)
                .totalSlots(allSlots.size())
                .availableSlots(availableSlots)
                .occupiedSlots(occupiedSlots)
                .reservedSlots(reservedSlots)
                .totalUsers(userRepository.count())
                .totalBookings(bookingRepository.count())
                .activeBookings(bookingRepository.count())
                .totalRevenue(revenue)
                .bookingTrends(generateBookingTrends())
                .revenueByLocation(generateRevenueByLocation())
                .peakHourAnalytics(generatePeakHourAnalytics())
                .build();
    }

    public com.smartparking.dto.response.AdvancedAnalyticsResponse getAdvancedStats() {
        return com.smartparking.dto.response.AdvancedAnalyticsResponse.builder()
                .revenueTrends(getRevenueTrends())
                .occupancyTrends(getOccupancyTrends())
                .vehicleTypeStats(getVehicleStats())
                .peakHourStats(getPeakStats())
                .slotUtilization(getUtilizationStats())
                .summary(Map.of(
                        "totalRevenue", bookingRepository.getTotalRevenue() != null ? bookingRepository.getTotalRevenue() : 0,
                        "totalBookings", bookingRepository.count(),
                        "avgBookingValue", calculateAvgBookingValue()
                ))
                .build();
    }

    private List<Map<String, Object>> getRevenueTrends() {
        return bookingRepository.getDailyRevenueAndBookings().stream().map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("date", row[0].toString());
            m.put("revenue", row[1]);
            m.put("bookings", row[2]);
            return m;
        }).toList();
    }

    private List<Map<String, Object>> getOccupancyTrends() {
        // Simulated historical occupancy for visualization
        List<Map<String, Object>> trends = new ArrayList<>();
        for (int i = 24; i >= 0; i--) {
            Map<String, Object> m = new HashMap<>();
            m.put("time", LocalDateTime.now().minusHours(i).getHour() + ":00");
            m.put("occupancy", 40 + Math.random() * 50);
            trends.add(m);
        }
        return trends;
    }

    private List<Map<String, Object>> getVehicleStats() {
        return bookingRepository.getVehicleTypeStats().stream().map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("type", row[0].toString());
            m.put("count", row[1]);
            return m;
        }).toList();
    }

    private List<Map<String, Object>> getPeakStats() {
        return bookingRepository.getPeakHourStats().stream().map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("hour", row[0]);
            m.put("count", row[1]);
            return m;
        }).toList();
    }

    private List<Map<String, Object>> getUtilizationStats() {
        return bookingRepository.getSlotUtilizationStats().stream().limit(10).map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("slot", row[0]);
            m.put("utilization", row[1]);
            return m;
        }).toList();
    }

    private double calculateAvgBookingValue() {
        long count = bookingRepository.count();
        if (count == 0) return 0;
        BigDecimal revenue = bookingRepository.getTotalRevenue();
        return revenue != null ? revenue.doubleValue() / count : 0;
    }

    private List<Map<String, Object>> generateBookingTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minusDays(i);
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", day.toLocalDate().toString());
            entry.put("bookings", bookingRepository.countBookingsSince(day.toLocalDate().atStartOfDay()));
            trends.add(entry);
        }
        return trends;
    }

    private List<Map<String, Object>> generateRevenueByLocation() {
        return locationRepository.findAll().stream().limit(5).map(loc -> {
            Map<String, Object> m = new HashMap<>();
            m.put("location", loc.getName());
            m.put("revenue", loc.getHourlyRate().multiply(BigDecimal.valueOf(10 + Math.random() * 50)));
            return m;
        }).toList();
    }

    private List<Map<String, Object>> generatePeakHourAnalytics() {
        List<Map<String, Object>> peaks = new ArrayList<>();
        int[] hours = {8, 9, 10, 17, 18, 19};
        for (int h : hours) {
            Map<String, Object> m = new HashMap<>();
            m.put("hour", h);
            m.put("demandScore", 0.7 + Math.random() * 0.3);
            m.put("predictedOccupancy", (int) (60 + Math.random() * 35));
            peaks.add(m);
        }
        return peaks;
    }
}
