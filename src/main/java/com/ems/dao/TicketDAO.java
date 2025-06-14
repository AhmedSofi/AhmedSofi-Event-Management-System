package com.ems.dao;

import com.ems.models.Event;
import com.ems.models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ticket-related database operations.
 * Implements CRUD operations for tickets and manages ticket inventory.
 */
public class TicketDAO {
    private Connection connection;

    public TicketDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new ticket in the database.
     * @return The created ticket with generated ID
     * @throws SQLException If database error occurs
     */
    // In TicketDAO.java
    public List<Ticket> getAllTickets() throws SQLException {
        String sql = "SELECT * FROM tickets";
        List<Ticket> tickets = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        null, // Event will be set later
                        rs.getString("ticket_type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_available"),
                        null, // Sale start date
                        null, // Sale end date
                        rs.getString("description")
                );
                tickets.add(ticket);
            }
        }
        return tickets;
    }
    public Ticket createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (event_id, ticket_type, price, quantity_available, " +
                "sale_start_date, sale_end_date, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticket.getEvent().getEventId());
            stmt.setString(2, ticket.getTicketType());
            stmt.setDouble(3, ticket.getPrice());
            stmt.setInt(4, ticket.getQuantityAvailable());
            stmt.setTimestamp(5, ticket.getSaleStartDate() != null ?
                    Timestamp.valueOf(ticket.getSaleStartDate()) : null);
            stmt.setTimestamp(6, ticket.getSaleEndDate() != null ?
                    Timestamp.valueOf(ticket.getSaleEndDate()) : null);
            stmt.setString(7, ticket.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setTicketId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }

            return ticket;
        }
    }

    /**
     * Updates an existing ticket in the database.
     * @param ticket The ticket to update
     * @return The updated ticket
     * @throws SQLException If database error occurs
     */
    public Ticket updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET ticket_type = ?, price = ?, quantity_available = ?, " +
                "sale_start_date = ?, sale_end_date = ?, description = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE ticket_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTicketType());
            stmt.setDouble(2, ticket.getPrice());
            stmt.setInt(3, ticket.getQuantityAvailable());
            stmt.setTimestamp(4, ticket.getSaleStartDate() != null ?
                    Timestamp.valueOf(ticket.getSaleStartDate()) : null);
            stmt.setTimestamp(5, ticket.getSaleEndDate() != null ?
                    Timestamp.valueOf(ticket.getSaleEndDate()) : null);
            stmt.setString(6, ticket.getDescription());
            stmt.setInt(7, ticket.getTicketId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ticket failed, no rows affected.");
            }

            return getTicketById(ticket.getTicketId());
        }
    }

    /**
     * Deletes a ticket from the database.
     * @param ticketId The ID of the ticket to delete
     * @throws SQLException If database error occurs
     */
    public void deleteTicket(int ticketId) throws SQLException {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a ticket by its ID.
     * @param ticketId The ID of the ticket to retrieve
     * @return The ticket, or null if not found
     * @throws SQLException If database error occurs
     */
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT t.*, e.event_id, e.title, e.start_datetime, e.end_datetime " +
                "FROM tickets t " +
                "JOIN events e ON t.event_id = e.event_id " +
                "WHERE t.ticket_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTicket(rs);
                }
                return null;
            }
        }
    }

    /**
     * Retrieves all tickets for a specific event.
     * @param eventId The ID of the event
     * @return List of tickets for the event
     * @throws SQLException If database error occurs
     */
    public List<Ticket> getTicketsByEvent(int eventId) throws SQLException {
        String sql = "SELECT t.*, e.event_id, e.title, e.start_datetime, e.end_datetime " +
                "FROM tickets t " +
                "JOIN events e ON t.event_id = e.event_id " +
                "WHERE t.event_id = ? " +
                "ORDER BY t.ticket_type";

        List<Ticket> tickets = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapRowToTicket(rs));
                }
            }
        }

        return tickets;
    }

    /**
     * Updates the available quantity of a ticket.
     * @param ticketId The ID of the ticket
     * @param quantityChange The change in quantity (positive to add, negative to subtract)
     * @throws SQLException If database error occurs
     */
    public void updateTicketQuantity(int ticketId, int quantityChange) throws SQLException {
        String sql = "UPDATE tickets SET quantity_available = quantity_available + ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE ticket_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, ticketId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating ticket quantity failed, no rows affected.");
            }
        }
    }

    /**
     * Helper method to map a ResultSet row to a Ticket object.
     * @param rs The ResultSet containing ticket data
     * @return A Ticket object
     * @throws SQLException If database error occurs
     */
    private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
        // Create Event object
        Event event = new Event(
                rs.getInt("event_id"),
                rs.getString("title"),
                null, // description not needed here
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                null, // venue not needed here
                null, // organizer not needed here
                null  // image URL not needed here
        );

        // Create Ticket object
        Ticket ticket = new Ticket(
                rs.getInt("ticket_id"),
                event,
                rs.getString("ticket_type"),
                rs.getDouble("price"),
                rs.getInt("quantity_available"),
                rs.getTimestamp("sale_start_date").toLocalDateTime(),
                rs.getTimestamp("sale_end_date").toLocalDateTime(),
                rs.getString("description")
        );

        // Set optional dates
        Timestamp saleStart = rs.getTimestamp("sale_start_date");
        if (saleStart != null) {
            ticket.setSaleStartDate(saleStart.toLocalDateTime());
        }

        Timestamp saleEnd = rs.getTimestamp("sale_end_date");
        if (saleEnd != null) {
            ticket.setSaleEndDate(saleEnd.toLocalDateTime());
        }

        // Set timestamps
        ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        ticket.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return ticket;
    }
}
