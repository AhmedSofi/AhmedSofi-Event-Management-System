package com.ems.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private User attendee;
    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;
    private String paymentMethod;
    private String transactionId;
    private List<OrderItem> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        PENDING, PAID, CANCELLED, REFUNDED
    }

    public Order(int orderId, User attendee, LocalDateTime orderDate,
                 String status, double totalAmount, String paymentMethod,
                 String transactionId) {
        this.orderId = orderId;
        this.attendee = attendee;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.orderItems = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public User getAttendee() { return attendee; }
    public void setAttendee(User attendee) { this.attendee = attendee; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // public double getTotalAmount() { return totalAmount; }

    public double getTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0.0;
        }

        return orderItems.stream().mapToDouble(OrderItem::getFinalPrice).sum();
    }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public void addOrderItem(OrderItem item) { this.orderItems.add(item); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a");
        return orderDate != null ? orderDate.format(formatter) : "";
    }
}