package com.PROJECT.kitchenkart.Models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/** @noinspection ALL*/ // This is the corrected FoodProduct model class.
// It includes a public no-argument constructor which is required
// by Firebase Firestore for automatic deserialization.
public class FoodProduct {
    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String imageUrl;
    private String sellerId;
    @ServerTimestamp
    private Date createdAt;

    /**
     * Required public no-argument constructor for Firebase Firestore.
     * This is crucial for Firestore to automatically map a Firestore document
     * to a Java object when calling `documentSnapshot.toObject(FoodProduct.class)`.
     */
    public FoodProduct() {
        // Default constructor required by Firestore.
        // It must be public and take no arguments.
    }

    // You can keep other constructors if you have them,
    // as long as the no-argument constructor is also present.
    public FoodProduct(String id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
    }

    // Getters and setters for all fields
    // The fragment code uses setId() and getId(), so these methods
    // correctly handle the product's document ID.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setAvailableQuantity(int quantity) {
    }

    public void setProductImageUrl(String imageUrl) {
    }
}
