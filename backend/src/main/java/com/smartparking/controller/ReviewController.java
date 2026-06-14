package com.smartparking.controller;

import com.smartparking.dto.request.ReviewRequest;
import com.smartparking.entity.Review;
import com.smartparking.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }
}
