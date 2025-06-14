package com.ems.services;

import com.ems.config.DatabaseConnection;
import com.ems.dao.TicketDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Event;
import com.ems.models.Ticket;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TicketService {
    private static TicketService instance; // Singleton instance
    private TicketDAO ticketDAO;
    private Connection connection;

    // Private constructor so external code can't instantiate
    public TicketService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.ticketDAO = new TicketDAO(connection);
    }

    // Public method to retrieve the singleton instance
    public static TicketService getInstance() {
        if (instance == null) {
            synchronized (TicketService.class) {
                if (instance == null) {
                    instance = new TicketService();
                }
            }
        }
        return instance;
    }

    // Create a new ticket for an event
    public Ticket createTicket(Event event, String ticketType, double price,
                               int quantityAvailable, LocalDateTime saleStartDate,
                               LocalDateTime saleEndDate, String description) {

        if (saleEndDate != null && saleStartDate != null && saleEndDate.isBefore(saleStartDate)) {
            throw new EventManagementException("Ticket sale end date cannot be before the start date");
        }

        Ticket ticket = new Ticket(0, event, ticketType, price, quantityAvailable, saleStartDate, saleEndDate, description);
        try {
            return ticketDAO.createTicket(ticket);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to create ticket", e);
        }
    }

    // Update an existing ticket
    public Ticket updateTicket(Ticket ticket) {

        if (ticket.getSaleEndDate() != null && ticket.getSaleStartDate() != null
                && ticket.getSaleEndDate().isBefore(ticket.getSaleStartDate())) {
            throw new EventManagementException("Ticket sale end date cannot be before the start date");
        }

        try {
            return ticketDAO.updateTicket(ticket);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update ticket", e);
        }
    }

    // Delete a ticket
    public void deleteTicket(int ticketId) {
        try {
            ticketDAO.deleteTicket(ticketId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to delete ticket", e);
        }
    }

    // Get ticket by ID
    public Ticket getTicketById(int ticketId) {
        try {
            return ticketDAO.getTicketById(ticketId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve ticket", e);
        }
    }

    // Get all tickets for an event
    public List<Ticket> getTicketsByEvent(int eventId) {
        try {
            return ticketDAO.getTicketsByEvent(eventId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve event tickets", e);
        }
    }

    // Check ticket availability
    public boolean isTicketAvailable(int ticketId, int quantity) {
        try {
            Ticket ticket = ticketDAO.getTicketById(ticketId);
            if (ticket == null) return false;

            return ticket.getQuantityAvailable() >= quantity &&
                    (ticket.getSaleStartDate() == null || LocalDateTime.now().isAfter(ticket.getSaleStartDate())) &&
                    (ticket.getSaleEndDate() == null || LocalDateTime.now().isBefore(ticket.getSaleEndDate()));
        } catch (SQLException e) {
            throw new EventManagementException("Failed to check ticket availability", e);
        }
    }

    // Update ticket quantity
    public void updateTicketQuantity(int ticketId, int quantityChange) {
        try {
            ticketDAO.updateTicketQuantity(ticketId, quantityChange);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update ticket quantity", e);
        }
    }

    // Get all tickets
    public List<Ticket> getAllTickets() {
        try {
            return ticketDAO.getAllTickets();
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve tickets", e);
        }
    }
}
