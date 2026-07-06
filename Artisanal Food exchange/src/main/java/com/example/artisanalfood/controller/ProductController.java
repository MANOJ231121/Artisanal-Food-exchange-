package com.example.artisanalfood.controller;

import com.example.artisanalfood.model.Product;
import com.example.artisanalfood.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double maxDistance) {

        if (latitude != null && longitude != null) {
            // Geolocation filtering mode
            List<ProductService.ProductDistanceWrapper> nearProducts = productService.getProductsNear(
                    latitude, longitude, maxDistance, category, search);
            return ResponseEntity.ok(nearProducts);
        } else {
            // Normal retrieval mode (mapped to the wrapper format with distance -1 for frontend compatibility)
            List<Product> products = productService.getAllProducts(category, search);
            List<ProductService.ProductDistanceWrapper> wrapped = products.stream()
                    .map(p -> new ProductService.ProductDistanceWrapper(p, -1.0))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(wrapped);
        }
    }

    @GetMapping("/producer/{producerId}")
    public ResponseEntity<List<Product>> getProductsByProducer(@PathVariable String producerId) {
        return ResponseEntity.ok(productService.getProductsByProducer(producerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
