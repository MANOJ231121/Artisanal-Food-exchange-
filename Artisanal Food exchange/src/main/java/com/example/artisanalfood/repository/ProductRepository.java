package com.example.artisanalfood.repository;

import com.example.artisanalfood.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByProducerId(String producerId);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndNameContainingIgnoreCase(String category, String name);
}
