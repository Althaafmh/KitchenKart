package com.PROJECT.kitchenkart.Models;

import java.util.List;

/** @noinspection ALL*/
public class Order {
    private String orderId;
    private String customerName;
    private double totalAmount;
    private String orderDate; // You might use long for timestamp or Date object
    private String status; // e.g., "Pending", "Accepted", "Delivered", "Cancelled"
    private List<String> itemNames; // Simple list of item names, you might need a more complex structure for actual items
    private String deliveryAddress;

    public Order(String orderId, String customerName, double totalAmount, String orderDate, String status, List<String> itemNames, String deliveryAddress) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.itemNames = itemNames;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setOrderId(String id) {

    }
}