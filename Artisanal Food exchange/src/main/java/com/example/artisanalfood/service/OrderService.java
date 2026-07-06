package com.example.artisanalfood.service;

import com.example.artisanalfood.model.Order;
import com.example.artisanalfood.model.Product;
import com.example.artisanalfood.repository.OrderRepository;
import com.example.artisanalfood.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order placeOrder(Order order) {
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStock() >= order.getQuantity()) {
                product.setStock(product.getStock() - order.getQuantity());
                productRepository.save(product);
            } else {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        order.setTotalAmount(order.getPrice() * order.getQuantity());
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByProducer(String producerId) {
        return orderRepository.findByProducerId(producerId);
    }
}
