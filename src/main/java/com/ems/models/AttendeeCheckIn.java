package com.ems.models;

import java.time.LocalDateTime;

/**
 * Represents an attendee check-in record for an event.
 * Tracks when and by whom an attendee was checked in to an event.
 */
public class AttendeeCheckIn {
    private int checkInId;
    private OrderItem orderItem;
    private LocalDateTime checkInTime;
    private User checkedInBy;
    private String notes;

    /**
     * Constructs a new AttendeeCheckIn record.
     *
     * @param checkInId The unique identifier for this check-in record
     * @param orderItem The order item associated with this check-in
     * @param checkInTime The date and time when the check-in occurred
     * @param checkedInBy The user who performed the check-in (can be null for self-check-in)
     * @param notes Any additional notes about the check-in
     */
    public AttendeeCheckIn(int checkInId, OrderItem orderItem, LocalDateTime checkInTime,
                           User checkedInBy, String notes) {
        this.checkInId = checkInId;
        this.orderItem = orderItem;
        this.checkInTime = checkInTime;
        this.checkedInBy = checkedInBy;
        this.notes = notes;
    }

    // Getters and setters

    /**
     * @return The unique identifier for this check-in record
     */
    public int getCheckInId() {
        return checkInId;
    }

    /**
     * @param checkInId The unique identifier to set for this check-in record
     */
    public void setCheckInId(int checkInId) {
        this.checkInId = checkInId;
    }

    /**
     * @return The order item associated with this check-in
     */
    public OrderItem getOrderItem() {
        return orderItem;
    }

    /**
     * @param orderItem The order item to associate with this check-in
     */
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    /**
     * @return The date and time when the check-in occurred
     */
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    /**
     * @param checkInTime The date and time to set for this check-in
     */
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    /**
     * @return The user who performed the check-in (may be null for self-check-in)
     */
    public User getCheckedInBy() {
        return checkedInBy;
    }

    /**
     * @param checkedInBy The user to record as performing this check-in
     */
    public void setCheckedInBy(User checkedInBy) {
        this.checkedInBy = checkedInBy;
    }

    /**
     * @return Any additional notes about the check-in
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes Additional notes to record about this check-in
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns a string representation of the check-in record.
     * @return String containing check-in details
     */
    @Override
    public String toString() {
        return "AttendeeCheckIn{" +
                "checkInId=" + checkInId +
                ", orderItem=" + orderItem +
                ", checkInTime=" + checkInTime +
                ", checkedInBy=" + (checkedInBy != null ?
                checkedInBy.getFirstName() + " " + checkedInBy.getLastName() : "System") +
                ", notes='" + notes + '\'' +
                '}';
    }

    /**
     * Helper method to get a summary of the check-in
     * @return A formatted string with basic check-in information
     */
    public String getCheckInSummary() {
        String attendeeName = orderItem.getOrder().getAttendee().getFirstName() + " " +
                orderItem.getOrder().getAttendee().getLastName();
        String eventName = orderItem.getTicket().getEvent().getTitle();

        return String.format("Check-in #%d: %s for %s at %s",
                checkInId,
                attendeeName,
                eventName,
                checkInTime.format(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME));
    }
}
