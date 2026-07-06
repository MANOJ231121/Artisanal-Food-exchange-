package com.example.artisanalfood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private String id;
    private String userId;
    private String productId;
    private String productName;
    private String producerName;
    private Frequency frequency; // WEEKLY, BIWEEKLY, MONTHLY
    private Status status; // ACTIVE, PAUSED
    private int quantity;
    private LocalDate nextDeliveryDate;

    public enum Frequency {
        WEEKLY,
        BIWEEKLY,
        MONTHLY
    }

    public enum Status {
        ACTIVE,
        PAUSED
    }

    // Constructors
    public Subscription() {}

    public Subscription(String id, String userId, String productId, String productName, String producerName, 
                        Frequency frequency, Status status, int quantity, LocalDate nextDeliveryDate) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.producerName = producerName;
        this.frequency = frequency;
        this.status = status;
        this.quantity = quantity;
        this.nextDeliveryDate = nextDeliveryDate;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProducerName() { return producerName; }
    public void setProducerName(String producerName) { this.producerName = producerName; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getNextDeliveryDate() { return nextDeliveryDate; }
    public void setNextDeliveryDate(LocalDate nextDeliveryDate) { this.nextDeliveryDate = nextDeliveryDate; }
}
