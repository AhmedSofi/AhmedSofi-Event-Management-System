package com.ems.dao;

import java.sql.*;

public class SoldTicketDAO {
    private Connection connection;

    public SoldTicketDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertSoldTicket(int ticketId,int eventId, int orderItemId, int orderId,
                                 int quantity, double salePrice) throws SQLException {
        String sql = "INSERT INTO sold_tickets (ticket_id,event_id, order_item_id, order_id, " +
                "quantity, sale_price, sold_at) VALUES (?,?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, orderItemId);
            stmt.setInt(4, orderId);
            stmt.setInt(5, quantity);
            stmt.setDouble(6, salePrice);

            stmt.executeUpdate();
        }
    }

    public int getSoldQuantityByEventID(int ticketId) throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_quantity FROM sold_tickets WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_quantity");
                }
                return 0; // Return 0 if no sold tickets found
            }
        }
    }
}