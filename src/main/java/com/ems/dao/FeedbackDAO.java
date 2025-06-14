package com.ems.dao;

import com.ems.models.Event;
import com.ems.models.Feedback;
import com.ems.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    private final Connection connection;

    public FeedbackDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean saveFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (event_id, attendee_id, rating, comments, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feedback.getEvent().getEventId());
            stmt.setInt(2, feedback.getAttendee().getUserId());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComments());
            stmt.setTimestamp(5, Timestamp.valueOf(feedback.getSubmittedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Feedback> getFeedbackByUser(int userId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, e.title, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role\n" +
                "FROM feedback f\n" +
                "JOIN events e ON f.event_id = e.event_id\n" +
                "JOIN users u ON f.attendee_id = u.user_id\n" +
                "WHERE f.attendee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));

                    User attendee = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"), User.UserRole.valueOf(rs.getString("role")));
                    attendee.setUserId(userId);

                    Feedback feedback = new Feedback(
                            rs.getInt("feedback_id"),
                            event,
                            attendee,
                            rs.getInt("rating")
                    );
                    feedback.setComments(rs.getString("comments"));
                    feedback.setSubmittedAt(rs.getTimestamp("submitted_at").toLocalDateTime());

                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }


    public List<Event> getEventsAttendedByUser(int userId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = """
        SELECT DISTINCT e.*
        FROM events e
        JOIN tickets t ON t.event_id = e.event_id
        JOIN order_items oi ON oi.ticket_id = t.ticket_id
        JOIN orders o ON o.order_id = oi.order_id
        WHERE o.attendee_id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
                    event.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
                    events.add(event);
                }
            }
        }

        return events;
    }
    public double getAverageRating(int eventId) throws SQLException {
        String sql = "SELECT AVG(rating) AS average_rating FROM feedback WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average_rating");
                }
            }
        }
        return 0.0;
    }


    public boolean isFeedbackExists(int eventId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE event_id = ? AND attendee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    public void updateFeedback(Feedback feedback) throws SQLException {
        String sql = "UPDATE feedback SET rating = ?, comments = ? WHERE event_id = ? AND attendee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feedback.getRating());
            stmt.setString(2, feedback.getComments());
            stmt.setInt(3, feedback.getEvent().getEventId());
            stmt.setInt(4, feedback.getAttendee().getUserId());
            stmt.executeUpdate();
        }
    }

    public List<Feedback> getFeedbackByEvent(int eventId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT f.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role\n" +
                "FROM feedback f\n" +
                "JOIN users u ON f.attendee_id = u.user_id\n" +
                "WHERE f.event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User attendee = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"), User.UserRole.valueOf(rs.getString("role")));
                    Feedback feedback = new Feedback(
                            rs.getInt("feedback_id"),
                            null,
                            attendee,
                            rs.getInt("rating")
                    );
                    feedback.setComments(rs.getString("comments"));
                    feedback.setSubmittedAt(rs.getTimestamp("submitted_at").toLocalDateTime());

                    feedbackList.add(feedback);
                }
            }
        }
        return feedbackList;
    }
    public void deleteFeedback(int eventId, int userId) throws SQLException {
        String sql = "DELETE FROM feedback WHERE event_id = ? AND attendee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}
