package com.ems.models;

public class OrderItem {
    private int orderItemId;
    private Order order;
    private Ticket ticket;
    private int quantity;
    private double pricePerUnit;
    private double discountApplied;
    private double finalPrice;

    public OrderItem(int orderItemId, Order order, Ticket ticket, int quantity,
                     double pricePerUnit, double discountApplied, double finalPrice) {
        this.orderItemId = orderItemId;
        this.order = order;
        this.ticket = ticket;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.discountApplied = discountApplied;
        this.finalPrice = finalPrice;
    }

    // Getters and setters
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateFinalPrice();
    }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        calculateFinalPrice();
    }

    public double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
        calculateFinalPrice();
    }

    public double getFinalPrice() { return finalPrice; }
    private void calculateFinalPrice() {
        this.finalPrice = (pricePerUnit * quantity) - discountApplied;
    }

    // Add this method to calculate the subtotal
    public double getSubtotal() {
        return (pricePerUnit * quantity) - discountApplied;
    }
}