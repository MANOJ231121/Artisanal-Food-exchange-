package com.example.artisanalfood.repository;

import com.example.artisanalfood.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByUserId(String userId);
    List<Subscription> findByProductId(String productId);
}
