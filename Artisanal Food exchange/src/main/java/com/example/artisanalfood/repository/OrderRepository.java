package com.example.artisanalfood.repository;

import com.example.artisanalfood.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByProducerId(String producerId);
    List<Order> findByUserId(String userId);
}
