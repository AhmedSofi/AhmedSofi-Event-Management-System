package com.ems.dao;

import com.ems.models.Speaker;
import com.ems.exceptions.EventManagementException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpeakerDAO {
    private Connection connection;

    public SpeakerDAO(Connection connection) {
        this.connection = connection;
    }

    public Speaker createSpeaker(Speaker speaker) throws SQLException {
        String sql = "INSERT INTO speakers (name, bio) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, speaker.getName());
            stmt.setString(2, speaker.getBio());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating speaker failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    speaker.setSpeakerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating speaker failed, no ID obtained.");
                }
            }

            return speaker;
        }
    }

    public void addSpeakerToEvent(int eventId, int speakerId) throws SQLException {
        String sql = "INSERT INTO event_speakers (event_id, speaker_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, speakerId);
            stmt.executeUpdate();
        }
    }

    public List<Speaker> getSpeakersByEventId(int eventId) throws SQLException {
        String sql = "SELECT s.* FROM speakers s " +
                "JOIN event_speakers es ON s.speaker_id = es.speaker_id " +
                "WHERE es.event_id = ?";

        List<Speaker> speakers = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    speakers.add(new Speaker(
                            rs.getInt("speaker_id"),
                            rs.getString("name"),
                            rs.getString("bio")
                    ));
                }
            }
        }

        return speakers;
    }

    public void removeSpeakerFromEvent(int eventId, int speakerId) throws SQLException {
        String sql = "DELETE FROM event_speakers WHERE event_id = ? AND speaker_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, speakerId);
            stmt.executeUpdate();
        }
    }

    public Speaker getSpeakerById(int speakerId) throws SQLException {
        String sql = "SELECT * FROM speakers WHERE speaker_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, speakerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Speaker(
                            rs.getInt("speaker_id"),
                            rs.getString("name"),
                            rs.getString("bio")
                    );
                }
            }
        }

        return null;
    }
    public void updateSpeaker(Speaker speaker) throws SQLException {
        String sql = "UPDATE speakers SET name = ?, bio = ? WHERE speaker_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, speaker.getName());
            stmt.setString(2, speaker.getBio());
            stmt.setInt(3, speaker.getSpeakerId());
            stmt.executeUpdate();
        }
    }
}