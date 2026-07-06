package com.example.artisanalfood.service;

import com.example.artisanalfood.model.Product;
import com.example.artisanalfood.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts(String category, String searchQuery) {
        if (category != null && !category.isEmpty() && searchQuery != null && !searchQuery.isEmpty()) {
            return productRepository.findByCategoryAndNameContainingIgnoreCase(category, searchQuery);
        } else if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory(category);
        } else if (searchQuery != null && !searchQuery.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(searchQuery);
        }
        return productRepository.findAll();
    }

    public List<Product> getProductsByProducer(String producerId) {
        return productRepository.findByProducerId(producerId);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    // Geolocation distance filtering
    public List<ProductDistanceWrapper> getProductsNear(double userLat, double userLon, Double maxDistanceKm, String category, String searchQuery) {
        List<Product> products = getAllProducts(category, searchQuery);
        
        return products.stream()
            .map(product -> {
                double distance = calculateDistance(userLat, userLon, product.getLatitude(), product.getLongitude());
                return new ProductDistanceWrapper(product, distance);
            })
            .filter(wrapper -> maxDistanceKm == null || wrapper.getDistance() <= maxDistanceKm)
            .sorted((w1, w2) -> Double.compare(w1.getDistance(), w2.getDistance()))
            .collect(Collectors.toList());
    }

    // Haversine Formula for distance calculation in kilometers
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radious of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // returns distance in km
    }

    // Wrapper class to return product along with calculated distance
    public static class ProductDistanceWrapper {
        private Product product;
        private double distance;

        public ProductDistanceWrapper(Product product, double distance) {
            this.product = product;
            this.distance = distance;
        }

        public Product getProduct() { return product; }
        public double getDistance() { return distance; }
    }
}
