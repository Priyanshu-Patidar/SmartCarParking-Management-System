package com.smartparking.service;

import com.smartparking.dto.request.ReviewRequest;
import com.smartparking.entity.Review;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.ParkingLocationRepository;
import com.smartparking.repository.ReviewRepository;
import com.smartparking.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ParkingLocationRepository locationRepository;

    @Transactional
    public Review addReview(ReviewRequest request) {
        return reviewRepository.save(Review.builder()
                .user(SecurityUtils.getCurrentUser())
                .location(locationRepository.findById(request.getLocationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Location not found")))
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }
}
