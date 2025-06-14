package com.ems.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a ticket for an event.
 * Follows the Open/Closed Principle - can be extended for different ticket types.
 */
public class Ticket {
    private int ticketId;
    private Event event;
    private String ticketType;
    private double price;
    private int quantityAvailable;
    private LocalDateTime saleStartDate;
    private LocalDateTime saleEndDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(int ticketId, Event event, String ticketType, double price,
                  int quantityAvailable,LocalDateTime saleStartDate,
                  LocalDateTime saleEndDate, String description) {
        this.ticketId = ticketId;
        this.event = event;
        this.ticketType = ticketType;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
        this.saleStartDate = saleStartDate;
        this.saleEndDate = saleEndDate;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public LocalDateTime getSaleStartDate() { return saleStartDate; }
    public void setSaleStartDate(LocalDateTime saleStartDate) { this.saleStartDate = saleStartDate; }

    public LocalDateTime getSaleEndDate() { return saleEndDate; }
    public void setSaleEndDate(LocalDateTime saleEndDate) { this.saleEndDate = saleEndDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


    public String getFormattedStartDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a");
        return saleStartDate != null ? saleStartDate.format(formatter) : "";
    }

    public String getFormattedEndDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a");
        return saleEndDate != null ? saleEndDate.format(formatter) : "";
    }
    // Business methods
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        boolean withinSalePeriod = (saleStartDate == null || now.isAfter(saleStartDate)) &&
                (saleEndDate == null || now.isBefore(saleEndDate));
        return quantityAvailable > 0 && withinSalePeriod;
    }

    public void decreaseQuantity(int amount) {
        if (amount > quantityAvailable) {
            throw new IllegalArgumentException("Cannot decrease quantity below zero");
        }
        quantityAvailable -= amount;
    }

    public void increaseQuantity(int amount) {
        quantityAvailable += amount;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", ticketType='" + ticketType + '\'' +
                ", price=" + price +
                ", quantityAvailable=" + quantityAvailable +
                ", saleStartDate=" + saleStartDate +
                ", saleEndDate=" + saleEndDate +
                ", description='" + description + '\'' +
                '}';
    }
}
