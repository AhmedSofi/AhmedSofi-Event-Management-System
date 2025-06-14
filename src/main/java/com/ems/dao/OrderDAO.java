package com.ems.dao;

import com.ems.exceptions.EventManagementException;
import com.ems.models.Order;
import com.ems.models.OrderItem;
import com.ems.models.Ticket;
import com.ems.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection connection;
    private TicketDAO ticketDAO;
    private UserDAO userDAO;

    public OrderDAO(Connection connection) {
        this.connection = connection;
        this.ticketDAO = new TicketDAO(connection);
        this.userDAO = new UserDAO(connection);
    }

    // Create a new order
    public Order createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (attendee_id, order_date, status, total_amount, " +
                "payment_method, transaction_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getAttendee().getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            stmt.setString(3, order.getStatus());
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getPaymentMethod());
            stmt.setString(6, order.getTransactionId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Save order items
            for (OrderItem item : order.getOrderItems()) {
                createOrderItem(item, order.getOrderId());
            }

            return order;
        }
    }
    public void updateOrderPaymentMethod(int orderId, String paymentMethod) {
        try {
            String sql = "UPDATE orders SET payment_method = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, paymentMethod);
                stmt.setInt(2, orderId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update payment method", e);
        }
    }

    // Updated OrderDAO.java
    public void createOrderItem(OrderItem item, int orderId) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, ticket_id, quantity, unit_price, discount_applied, final_price, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, item.getTicket().getTicketId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPricePerUnit());
            stmt.setDouble(5, item.getDiscountApplied());
            stmt.setDouble(6, item.getFinalPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setOrderItemId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order item failed, no ID obtained.");
                }
            }
        }
    }

    private void loadOrderItems(Order order) throws SQLException {
        String sql = "SELECT oi.*, t.* FROM order_items oi " +
                "JOIN tickets t ON oi.ticket_id = t.ticket_id " +
                "WHERE oi.order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = ticketDAO.getTicketById(rs.getInt("ticket_id"));
                    OrderItem item = new OrderItem(
                            rs.getInt("item_id"),
                            order,
                            ticket,
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("discount_applied"),
                            rs.getDouble("final_price")
                    );
                    order.addOrderItem(item);
                }
            }
        }
    }
    // Get order by ID
    public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    loadOrderItems(order);
                    return order;
                }
                return null;
            }
        }
    }

    // Get orders by user
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE attendee_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    loadOrderItems(order);
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    public int getTotalQuantityByTicketId(int ticketId) throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_quantity FROM order_items WHERE ticket_id = ?";
        int totalQuantity = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalQuantity = rs.getInt("total_quantity");
                }
            }
        }

        return totalQuantity;
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> orderItems = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                            rs.getInt("item_id"),
                            null, // Order will be set later
                            ticketDAO.getTicketById(rs.getInt("ticket_id")),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("discount_applied"),
                            rs.getDouble("final_price")
                    );
                    orderItems.add(item);
                }
            }
        }

        return orderItems;
    }

    // Update order status
    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }
    public void removeOrderItem(int orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }


    // Helper method to map a row to an Order object
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        User attendee = userDAO.getUserById(rs.getInt("attendee_id"));

        Order order = new Order(
                rs.getInt("order_id"),
                attendee,
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getString("status"),
                rs.getDouble("total_amount"),
                rs.getString("payment_method"),
                rs.getString("transaction_id")
        );

        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return order;
    }
}