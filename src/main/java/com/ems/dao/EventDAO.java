package com.ems.dao;

import com.ems.models.Event;
import com.ems.models.User;
import com.ems.models.Venue;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for event-related database operations.
 */
public class EventDAO {
    private Connection connection;

    public EventDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new event in the database
    public Event createEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, start_datetime, end_datetime, " +
                "venue_id, organizer_id, image_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDateTime()));
            stmt.setObject(5, event.getVenue() != null ? event.getVenue().getVenueId() : null, Types.INTEGER);
            stmt.setInt(6, event.getOrganizer().getUserId());
            stmt.setString(7, event.getImageUrl());
            stmt.setString(8, event.getStatus().toString());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating event failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setEventId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating event failed, no ID obtained.");
                }
            }

            return event;
        }
    }


    public List<Event> getPastEvents() throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "WHERE e.status != 'CANCELLED' AND e.status != 'DRAFT' " +
                "AND e.end_datetime < CURRENT_DATE() " +
                "ORDER BY e.start_datetime DESC";

        List<Event> events = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        }

        return events;
    }

    public boolean isVenueAvailable(int venueId,int eventId, LocalDateTime start, LocalDateTime end) throws SQLException {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        String sql = "SELECT COUNT(*) FROM events " +
                "WHERE venue_id = ? " +
                "AND event_id != ? " +
                "AND status != 'DRAFT' " +
                "AND status != 'CANCELLED'" +
                "AND ((DATE(start_datetime) <= ? AND DATE(end_datetime) >= ?) " +
                "OR (DATE(start_datetime) >= ? AND DATE(start_datetime) <= ?))";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, venueId);
            stmt.setInt(2, eventId);
            stmt.setDate(3, java.sql.Date.valueOf(endDate));
            stmt.setDate(4, java.sql.Date.valueOf(startDate));
            stmt.setDate(5, java.sql.Date.valueOf(startDate));
            stmt.setDate(6, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    public Event updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET title = ?, description = ?, start_datetime = ?, " +
                "end_datetime = ?, venue_id = ?, image_url = ?, status = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDateTime()));
            stmt.setObject(5, event.getVenue() != null ? event.getVenue().getVenueId() : null, Types.INTEGER);
            stmt.setString(6, event.getImageUrl());
            stmt.setString(7, event.getStatus().toString());
            stmt.setInt(8, event.getEventId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating event failed, no rows affected.");
            }

            return getEventById(event.getEventId());
        }
    }

    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "WHERE e.event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEvent(rs);
                }
                return null;
            }
        }
    }

    public List<Event> getAllEvents() throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "ORDER BY e.start_datetime";

        List<Event> events = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        }

        return events;
    }

    public List<Event> getEventsByOrganizer(int organizerId) throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "WHERE e.organizer_id = ? " +
                "ORDER BY e.start_datetime";

        List<Event> events = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, organizerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
        }

        return events;
    }

    public void addEventObserver(int eventId, int userId) throws SQLException {
        String sql = "INSERT INTO event_observers (event_id, user_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void removeEventObserver(int eventId, int userId) throws SQLException {
        String sql = "DELETE FROM event_observers WHERE event_id = ? AND user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> getEventObservers(int eventId) throws SQLException {
        String sql = "SELECT u.* FROM event_observers eo " +
                "JOIN users u ON eo.user_id = u.user_id " +
                "WHERE eo.event_id = ?";

        List<User> observers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    observers.add(mapRowToUser(rs));
                }
            }
        }

        return observers;
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        User organizer = mapRowToUser(rs);

        Venue venue = null;
        if (rs.getObject("venue_id") != null) {
            venue = new Venue(
                    rs.getInt("venue_id"),
                    rs.getString("venue_name"),
                    rs.getString("address"),
                    rs.getInt("capacity")
            );
        }

        Event event = new Event(
                rs.getInt("event_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                venue,
                organizer,
                rs.getString("image_url")
        );

        event.setStatus(Event.EventStatus.valueOf(rs.getString("status")));
        event.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        event.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return event;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                User.UserRole.valueOf(rs.getString("role"))
        );
    }

    public List<Event> getAllNotCancelledEvents() throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "WHERE e.status != 'CANCELLED' AND e.status != 'DRAFT' " +
                "AND e.end_datetime >= CURRENT_DATE() " + // Add this condition
                "ORDER BY e.start_datetime";

        List<Event> events = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        }

        return events;
    }

    public List<Event> getCanceledEvents() throws SQLException {
        String sql = "SELECT e.*, u.user_id, u.username, u.email, u.first_name, u.last_name, u.role, u.password_hash, " +
                "v.venue_id, v.name as venue_name, v.address, v.capacity " +
                "FROM events e " +
                "JOIN users u ON e.organizer_id = u.user_id " +
                "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                "WHERE e.status = 'CANCELLED' " +
                "ORDER BY e.start_datetime";

        List<Event> events = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        }

        return events;
    }
}
