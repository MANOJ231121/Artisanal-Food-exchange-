package com.example.artisanalfood.service;

import com.example.artisanalfood.dto.ProducerAnalytics;
import com.example.artisanalfood.model.Order;
import com.example.artisanalfood.model.Product;
import com.example.artisanalfood.model.Subscription;
import com.example.artisanalfood.repository.OrderRepository;
import com.example.artisanalfood.repository.ProductRepository;
import com.example.artisanalfood.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ProductRepository productRepository;

    public AnalyticsService(OrderRepository orderRepository, 
                            SubscriptionRepository subscriptionRepository, 
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.productRepository = productRepository;
    }

    public ProducerAnalytics getProducerAnalytics(String producerId) {
        List<Order> orders = orderRepository.findByProducerId(producerId);
        
        // 1. Calculate total revenue and total orders
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
        int totalOrdersCount = orders.size();

        // 2. Calculate active subscriptions for producer's products
        List<Product> products = productRepository.findByProducerId(producerId);
        int activeSubscriptionsCount = 0;
        for (Product product : products) {
            List<Subscription> subs = subscriptionRepository.findByProductId(product.getId());
            activeSubscriptionsCount += (int) subs.stream()
                .filter(s -> s.getStatus() == Subscription.Status.ACTIVE)
                .count();
        }

        // 3. Group sales by product
        Map<String, Integer> productQuantities = new HashMap<>();
        Map<String, Double> productRevenues = new HashMap<>();
        for (Order order : orders) {
            String name = order.getProductName();
            productQuantities.put(name, productQuantities.getOrDefault(name, 0) + order.getQuantity());
            productRevenues.put(name, productRevenues.getOrDefault(name, 0.0) + order.getTotalAmount());
        }

        List<ProducerAnalytics.ProductSales> topProducts = productQuantities.entrySet().stream()
            .map(entry -> {
                String name = entry.getKey();
                int qty = entry.getValue();
                double rev = Math.round(productRevenues.get(name) * 100.0) / 100.0;
                return new ProducerAnalytics.ProductSales(name, qty, rev);
            })
            .sorted((p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()))
            .limit(5) // top 5 products
            .collect(Collectors.toList());

        // 4. Get recent 10 orders
        List<Order> recentOrders = orders.stream()
            .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
            .limit(10)
            .collect(Collectors.toList());

        return new ProducerAnalytics(totalRevenue, totalOrdersCount, activeSubscriptionsCount, topProducts, recentOrders);
    }
}
