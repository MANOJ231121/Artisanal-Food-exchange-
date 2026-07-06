package com.example.artisanalfood.service;

import com.example.artisanalfood.model.Product;
import com.example.artisanalfood.model.Review;
import com.example.artisanalfood.model.User;
import com.example.artisanalfood.repository.ProductRepository;
import com.example.artisanalfood.repository.ReviewRepository;
import com.example.artisanalfood.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Review addReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        // Recalculate producer rating
        updateProducerRatingByProductId(review.getProductId());

        return savedReview;
    }

    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    private void updateProducerRatingByProductId(String productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            String producerId = product.getProducerId();
            
            // Find all products by this producer
            List<Product> producerProducts = productRepository.findByProducerId(producerId);
            
            // Collect all reviews for these products
            double totalRating = 0;
            int reviewCount = 0;
            
            for (Product p : producerProducts) {
                List<Review> reviews = reviewRepository.findByProductId(p.getId());
                for (Review r : reviews) {
                    totalRating += r.getRating();
                    reviewCount++;
                }
            }
            
            if (reviewCount > 0) {
                double averageRating = Math.round((totalRating / reviewCount) * 10.0) / 10.0;
                Optional<User> producerOpt = userRepository.findById(producerId);
                if (producerOpt.isPresent()) {
                    User producer = producerOpt.get();
                    producer.setRating(averageRating);
                    userRepository.save(producer);
                }
            }
        }
    }
}
