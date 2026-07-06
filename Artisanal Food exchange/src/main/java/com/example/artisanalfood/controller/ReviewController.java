package com.example.artisanalfood.controller;

import com.example.artisanalfood.model.Review;
import com.example.artisanalfood.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable String productId, @RequestBody Review review) {
        review.setProductId(productId);
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}
