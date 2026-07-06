package com.example.artisanalfood.controller;

import com.example.artisanalfood.model.Order;
import com.example.artisanalfood.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.placeOrder(order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/producer/{producerId}")
    public ResponseEntity<List<Order>> getOrdersByProducer(@PathVariable String producerId) {
        return ResponseEntity.ok(orderService.getOrdersByProducer(producerId));
    }
}
