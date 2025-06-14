package com.ems.dao;

import com.ems.models.AgendaItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaItemDAO {
    private Connection connection;

    public AgendaItemDAO(Connection connection) {
        this.connection = connection;
    }

    public AgendaItem createAgendaItem(AgendaItem agendaItem) throws SQLException {
        String sql = "INSERT INTO agenda_items (event_id, title, description) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, agendaItem.getEventId());
            stmt.setString(2, agendaItem.getTitle());
            stmt.setString(3, agendaItem.getDescription());


            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating agenda item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    agendaItem.setAgendaItemId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating agenda item failed, no ID obtained.");
                }
            }

            return agendaItem;
        }
    }

    public List<AgendaItem> getAgendaItemsByEventId(int eventId) throws SQLException {
        String sql = "SELECT * FROM agenda_items WHERE event_id = ?";

        List<AgendaItem> agendaItems = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agendaItems.add(new AgendaItem(
                            rs.getInt("agenda_item_id"),
                            rs.getInt("event_id"),
                            rs.getString("title"),
                            rs.getString("description")
                    ));
                }
            }
        }

        return agendaItems;
    }

    public void updateAgendaItem(AgendaItem agendaItem) throws SQLException {
        String sql = "UPDATE agenda_items SET title = ?, description = ?" +
                "WHERE agenda_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, agendaItem.getTitle());
            stmt.setString(2, agendaItem.getDescription());
            stmt.setInt(3, agendaItem.getAgendaItemId());

            stmt.executeUpdate();
        }
    }

    public void deleteAgendaItem(int agendaItemId) throws SQLException {
        String sql = "DELETE FROM agenda_items WHERE agenda_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, agendaItemId);
            stmt.executeUpdate();
        }
    }
}