package com.example.artisanalfood.dto;

import com.example.artisanalfood.model.Order;
import java.util.List;
import java.util.Map;

public class ProducerAnalytics {
    private double totalRevenue;
    private int totalOrdersCount;
    private int activeSubscriptionsCount;
    private List<ProductSales> topProducts;
    private List<Order> recentOrders;

    public static class ProductSales {
        private String productName;
        private int quantity;
        private double revenue;

        public ProductSales(String productName, int quantity, double revenue) {
            this.productName = productName;
            this.quantity = quantity;
            this.revenue = revenue;
        }

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getRevenue() { return revenue; }
    }

    // Constructors
    public ProducerAnalytics() {}

    public ProducerAnalytics(double totalRevenue, int totalOrdersCount, int activeSubscriptionsCount, 
                             List<ProductSales> topProducts, List<Order> recentOrders) {
        this.totalRevenue = totalRevenue;
        this.totalOrdersCount = totalOrdersCount;
        this.activeSubscriptionsCount = activeSubscriptionsCount;
        this.topProducts = topProducts;
        this.recentOrders = recentOrders;
    }

    // Getters and Setters
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalOrdersCount() { return totalOrdersCount; }
    public void setTotalOrdersCount(int totalOrdersCount) { this.totalOrdersCount = totalOrdersCount; }

    public int getActiveSubscriptionsCount() { return activeSubscriptionsCount; }
    public void setActiveSubscriptionsCount(int activeSubscriptionsCount) { this.activeSubscriptionsCount = activeSubscriptionsCount; }

    public List<ProductSales> getTopProducts() { return topProducts; }
    public void setTopProducts(List<ProductSales> topProducts) { this.topProducts = topProducts; }

    public List<Order> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<Order> recentOrders) { this.recentOrders = recentOrders; }
}
