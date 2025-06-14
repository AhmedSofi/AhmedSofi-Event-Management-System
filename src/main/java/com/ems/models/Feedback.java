package com.ems.models;

import java.time.LocalDateTime;

/**
 * Represents feedback provided by an attendee for an event.
 */
public class Feedback {
    private int feedbackId;
    private Event event;
    private User attendee;
    private int rating;
    private String comments;
    private LocalDateTime submittedAt;

    public Feedback(int feedbackId, Event event, User attendee, int rating) {
        this.feedbackId = feedbackId;
        this.event = event;
        this.attendee = attendee;
        this.rating = rating;
        this.submittedAt = LocalDateTime.now();
    }

    public Feedback() {
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and setters
    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public User getAttendee() { return attendee; }
    public void setAttendee(User attendee) { this.attendee = attendee; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", event=" + event +
                ", attendee=" + attendee +
                ", rating=" + rating +
                '}';
    }
}
