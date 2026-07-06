package com.example.artisanalfood.controller;

import com.example.artisanalfood.dto.ProducerAnalytics;
import com.example.artisanalfood.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/producer/{producerId}")
    public ResponseEntity<ProducerAnalytics> getProducerAnalytics(@PathVariable String producerId) {
        return ResponseEntity.ok(analyticsService.getProducerAnalytics(producerId));
    }
}
