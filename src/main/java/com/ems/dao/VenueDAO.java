package com.ems.dao;

import com.ems.models.Venue;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {
    private final Connection connection;

    public VenueDAO(Connection connection) {
        this.connection = connection;
    }



    // Create a new venue
    public boolean createVenue(Venue venue) throws SQLException {
        String sql = "INSERT INTO venues (name, address, capacity, contact_person, contact_phone, contact_email, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, venue.getName());
            statement.setString(2, venue.getAddress());
            statement.setInt(3, venue.getCapacity());
            statement.setString(4, venue.getContactPerson());
            statement.setString(5, venue.getContactPhone());
            statement.setString(6, venue.getContactEmail());
            statement.setTimestamp(7, Timestamp.valueOf(venue.getCreatedAt()));
            statement.setTimestamp(8, Timestamp.valueOf(venue.getUpdatedAt()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venue.setVenueId(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    // Get all venues
    public List<Venue> getAllVenues() throws SQLException {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY name";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Venue venue = extractVenueFromResultSet(resultSet);
                venues.add(venue);
            }
        }
        return venues;
    }

    // Get venue by ID
    public Venue getVenueById(int venueId) throws SQLException {
        String sql = "SELECT * FROM venues WHERE venue_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, venueId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractVenueFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    // Update venue
    public boolean updateVenue(Venue venue) throws SQLException {
        String sql = "UPDATE venues SET name = ?, address = ?, capacity = ?, contact_person = ?, " +
                "contact_phone = ?, contact_email = ?, updated_at = ? WHERE venue_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, venue.getName());
            statement.setString(2, venue.getAddress());
            statement.setInt(3, venue.getCapacity());
            statement.setString(4, venue.getContactPerson());
            statement.setString(5, venue.getContactPhone());
            statement.setString(6, venue.getContactEmail());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(8, venue.getVenueId());

            return statement.executeUpdate() > 0;
        }
    }

    // Delete venue
    public boolean deleteVenue(int venueId) throws SQLException {
        String sql = "DELETE FROM venues WHERE venue_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, venueId);
            return statement.executeUpdate() > 0;
        }
    }

    // Helper method to extract Venue from ResultSet
    private Venue extractVenueFromResultSet(ResultSet resultSet) throws SQLException {
        Venue venue = new Venue(
                resultSet.getInt("venue_id"),
                resultSet.getString("name"),
                resultSet.getString("address"),
                resultSet.getInt("capacity")
        );

        venue.setContactPerson(resultSet.getString("contact_person"));
        venue.setContactPhone(resultSet.getString("contact_phone"));
        venue.setContactEmail(resultSet.getString("contact_email"));
        venue.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        venue.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());

        return venue;
    }

    public boolean isVenueAvailable(int venueId,int eventId, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events " +
                "WHERE venue_id = ? " +
                "AND event_id != ? " +
                "AND ((start_datetime < ? AND end_datetime > ?) " + // overlap before
                "OR (start_datetime >= ? AND start_datetime < ?))";   // starts during

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, venueId);
            stmt.setInt(2, eventId);
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            stmt.setTimestamp(4, Timestamp.valueOf(start));
            stmt.setTimestamp(5, Timestamp.valueOf(start));
            stmt.setTimestamp(6, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0; // true if 0 conflicts
            }
        }
    }



}