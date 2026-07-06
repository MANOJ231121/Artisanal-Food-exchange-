package com.example.artisanalfood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private Role role; // CONSUMER, PRODUCER
    private String bio; // For producers
    private double latitude;
    private double longitude;
    private String address;
    private double rating; // For producers (1.0 to 5.0)

    public enum Role {
        CONSUMER,
        PRODUCER
    }

    // Constructors
    public User() {}

    public User(String id, String name, String email, Role role, String bio, double latitude, double longitude, String address, double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.bio = bio;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
