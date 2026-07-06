package com.example.artisanalfood.repository;

import com.example.artisanalfood.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductId(String productId);
}
