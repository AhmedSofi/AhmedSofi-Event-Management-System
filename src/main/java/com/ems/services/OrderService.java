package com.ems.services;

import com.ems.config.DatabaseConnection;
import com.ems.dao.OrderDAO;
import com.ems.dao.SoldTicketDAO;
import com.ems.dao.TicketDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Order;
import com.ems.models.OrderItem;
import com.ems.models.Ticket;
import com.ems.models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private OrderDAO orderDAO;
    private TicketDAO ticketDAO;
    private Connection connection;
    private SoldTicketDAO soldTicketDAO ;

    private static OrderService instance;

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public OrderService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.orderDAO = new OrderDAO(connection);
        this.ticketDAO = new TicketDAO(connection);
        this.soldTicketDAO = new SoldTicketDAO(connection);
    }
    public Order getPendingOrderByUser(int userId) {
        try {
            List<Order> userOrders = orderDAO.getOrdersByUser(userId);
            return userOrders.stream()
                    .filter(order -> Order.OrderStatus.PENDING.toString().equals(order.getStatus()))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve pending orders", e);
        }
    }

    public void updateOrderPaymentMethod(int orderId, String paymentMethod)
    {
        orderDAO.updateOrderPaymentMethod(orderId, paymentMethod);
    }

    // Add method to add items to an existing order
    public Order addItemsToOrder(Order order, List<OrderItem> newItems) {
        try {
            double additionalAmount = calculateTotal(newItems);

            // Update the order's total amount
            order.setTotalAmount(order.getTotalAmount() + additionalAmount);

            // Add each item to the order
            for (OrderItem item : newItems) {
                item.setOrder(order);
                order.addOrderItem(item);

                // Update ticket quantities
                //Ticket ticket = item.getTicket();
               // ticketDAO.updateTicketQuantity(ticket.getTicketId(), -item.getQuantity());

                // Add the item to the database
                orderDAO.createOrderItem(item, order.getOrderId());
            }


            return order;
        } catch (SQLException e) {
            throw new EventManagementException("Failed to add items to order", e);
        }
    }
    // Replace the existing createOrder method
    public Order createOrder(User attendee, List<OrderItem> items, String paymentMethod) {
        try {
            // Check for existing pending order
            Order pendingOrder = getPendingOrderByUser(attendee.getUserId());

            if (pendingOrder != null) {
                // Add items to existing order
                return addItemsToOrder(pendingOrder, items);
            } else {
                // Create new order
                double totalAmount = calculateTotal(items);

                Order order = new Order(
                        0,
                        attendee,
                        LocalDateTime.now().withNano(0),
                        Order.OrderStatus.PENDING.toString(),
                        totalAmount,
                        paymentMethod,
                        generateTransactionId()
                );

                for (OrderItem item : items) {
                    item.setOrder(order);
                    order.addOrderItem(item);

                    // Update ticket quantities
                    //Ticket ticket = item.getTicket();
                  //  ticketDAO.updateTicketQuantity(ticket.getTicketId(), -item.getQuantity());
                }

                return orderDAO.createOrder(order);
            }
        } catch (SQLException e) {
            throw new EventManagementException("Failed to create order", e);
        }
    }
    // Get order by ID
    public Order getOrderById(int orderId) {
        try {
            return orderDAO.getOrderById(orderId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve order", e);
        }
    }

    // Get orders by user
    public List<Order> getOrdersByUser(int userId) {
        try {
            return orderDAO.getOrdersByUser(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve user orders", e);
        }
    }

    // Cancel an order
    public void cancelOrder(int orderId) {
        try {
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                throw new EventManagementException("Order not found");
            }

            // Only return tickets to inventory if the order was PAID
            if (Order.OrderStatus.PAID.toString().equals(order.getStatus())) {
                for (OrderItem item : order.getOrderItems()) {
                    ticketDAO.updateTicketQuantity(item.getTicket().getTicketId(), item.getQuantity());
                }
            }

            orderDAO.updateOrderStatus(orderId, Order.OrderStatus.CANCELLED.toString());
            orderDAO.removeOrderItem(orderId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to cancel order", e);
        }
    }

    public int getSoldQuantityByEventID(int ticketId) {
        try {
            return soldTicketDAO.getSoldQuantityByEventID(ticketId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve sold quantity for ticket", e);
        }
    }

    public int getTotalQuantityByTicketId(int ticketId) {
        try {
            return orderDAO.getTotalQuantityByTicketId(ticketId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve total quantity", e);
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        try {
            return orderDAO.getOrderItemsByOrderId(orderId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve order items", e);
        }
    }

    // Complete an order (after payment)
    public void completeOrder(int orderId) {
        try {
            // Get the order with items
            Order order = orderDAO.getOrderById(orderId);

            if (order == null) {
                throw new EventManagementException("Order not found");
            }

            // Update order status to PAID
            orderDAO.updateOrderStatus(orderId, Order.OrderStatus.PAID.toString());

            // NOW decrease the ticket quantities
            for (OrderItem item : order.getOrderItems()) {
                ticketDAO.updateTicketQuantity(item.getTicket().getTicketId(), -item.getQuantity());

                soldTicketDAO.insertSoldTicket(
                        item.getTicket().getTicketId(),
                        item.getTicket().getEvent().getEventId(),
                        item.getOrderItemId(),
                        order.getOrderId(),
                        item.getQuantity(),
                        item.getPricePerUnit()
                );
            }

        } catch (SQLException e) {
            throw new EventManagementException("Failed to complete order", e);
        }
    }

    // Helper methods
    private double calculateTotal(List<OrderItem> items) {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}