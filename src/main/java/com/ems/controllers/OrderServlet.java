package com.ems.controllers;

import com.ems.config.DatabaseConnection;
import com.ems.dao.EventDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.*;
import com.ems.services.EventService;
import com.ems.services.OrderService;
import com.ems.services.TicketService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/orders/*", "/checkout"})
public class OrderServlet extends HttpServlet {
    private OrderService orderService;
    private TicketService ticketService;
    private EventDAO eventDAO;
    private Connection connection;

    @Override
    public void init() throws ServletException {
        super.init();
        this.orderService = OrderService.getInstance();
        this.ticketService = TicketService.getInstance();
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.eventDAO = new  EventDAO(connection);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (path.equals("/orders") && (pathInfo == null || pathInfo.equals("/"))) {
            viewUserOrders(request, response);
        } else if (path.equals("/orders") && pathInfo.matches("/\\d+")) {
            System.out.println("ss1");
            viewOrderDetails(request, response);
        } else if (path.equals("/checkout")) {
            showCheckoutPage(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (path.equals("/checkout")) {
            processCheckout(request, response);
        } else if (path.equals("/orders") && pathInfo != null && pathInfo.matches("/\\d+/cancel")) {
            cancelOrder(request, response);
        } else if (path.equals("/orders") && pathInfo != null && pathInfo.equals("/purchase")) {
            processDirectPurchase(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // Add this new method to handle direct purchases
    private void processDirectPurchase(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        try {
            int ticketId;
            int quantity;

            try {
                ticketId = Integer.parseInt(request.getParameter("ticketId"));
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid ticket ID format");
                response.sendRedirect(request.getContextPath() + "/events");
                return;
            }

            try {
                String quantityStr = request.getParameter("quantity");
                // Check for valid integer format
                if (!quantityStr.matches("^\\d+$")) {
                    session.setAttribute("error", "Quantity must be a positive whole number");
                    Ticket ticket = ticketService.getTicketById(ticketId);
                    if (ticket != null) {
                        response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                    } else {
                        response.sendRedirect(request.getContextPath() + "/events");
                    }
                    return;
                }
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid quantity format");
                Ticket ticket = ticketService.getTicketById(ticketId);
                if (ticket != null) {
                    response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                } else {
                    response.sendRedirect(request.getContextPath() + "/events");
                }
                return;
            }

            if (quantity <= 0) {
                session.setAttribute("error", "Quantity must be greater than zero");
                Ticket ticket = ticketService.getTicketById(ticketId);
                if (ticket != null) {
                    response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                } else {
                    response.sendRedirect(request.getContextPath() + "/events");
                }
                return;
            }

            // Rest of the method remains the same
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                throw new EventManagementException("Ticket not found");
            }

            // Check if sufficient tickets are available
            int availableQuantity = ticket.getQuantityAvailable();

            if (quantity > availableQuantity) {
                session.setAttribute("error", "Cannot purchase " + quantity + " tickets. Only " +
                        availableQuantity + " tickets available.");
                response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                return;
            }

            // Create order items and complete process
            List<OrderItem> orderItems = new ArrayList<>();
            OrderItem item = new OrderItem(0, null, ticket, quantity, ticket.getPrice(), 0.0, ticket.getPrice() * quantity);
            orderItems.add(item);

            Order order = orderService.createOrder(user, orderItems, "CREDIT_CARD");
            List<User>observers = eventDAO.getEventObservers(ticket.getEvent().getEventId());
            boolean exists = observers.stream()
                    .anyMatch(u -> u.getUserId() == user.getUserId());

            if(!exists) {
                eventDAO.addEventObserver(ticket.getEvent().getEventId(), user.getUserId());
            }

            session.setAttribute("message", "Order completed successfully!");
            response.sendRedirect(request.getContextPath() + "/orders/" + order.getOrderId());
        } catch (Exception e) {
            System.err.println("Error processing purchase: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error processing purchase: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/events");
        }
    }

    private void viewUserOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            List<Order> orders = orderService.getOrdersByUser(user.getUserId());

            if (orders == null) {
                orders = new ArrayList<>();
            }

            // Sort orders by orderDate descending (latest first)
            orders.sort(Comparator.comparing(Order::getOrderDate).reversed());

            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("error", "There was a problem retrieving your orders. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }


    private void viewOrderDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getPathInfo().substring(1));
            Order order = orderService.getOrderById(orderId);

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            // Check if the order belongs to the current user
            if (order.getAttendee().getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/orders/view.jsp").forward(request, response);
        } catch (NumberFormatException | EventManagementException e) {
            handleError(request, response, "Failed to retrieve order: " + e.getMessage());
        }
    }

    private void showCheckoutPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String orderIdParam = request.getParameter("orderId");

        // If orderId is provided, use that order's items for checkout
        if (orderIdParam != null && !orderIdParam.isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdParam);
                Order order = orderService.getOrderById(orderId);

                // Verify the order belongs to this user and is in PENDING state
                if (order != null && order.getAttendee().getUserId() == user.getUserId()
                        && order.getStatus().equals(Order.OrderStatus.PENDING.toString())) {

                    request.setAttribute("orderItems", order.getOrderItems());
                    request.setAttribute("totalAmount", order.getTotalAmount());
                    request.setAttribute("orderId", orderId); // Pass orderId to the form
                    request.getRequestDispatcher("/WEB-INF/views/orders/checkout.jsp").forward(request, response);
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Cannot checkout this order - it may not belong to you or is not in PENDING state");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error loading order for checkout: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Regular cart checkout logic
        Map<Integer, Integer> cart = getCart(session);
        if (cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/events");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        try {
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int ticketId = entry.getKey();
                int quantity = entry.getValue();

                Ticket ticket = ticketService.getTicketById(ticketId);
                if (ticket != null) {
                    double itemPrice = ticket.getPrice() * quantity;
                    OrderItem item = new OrderItem(0, null, ticket, quantity, ticket.getPrice(), 0.0, itemPrice);
                    orderItems.add(item);
                    totalAmount += itemPrice;
                }
            }

            request.setAttribute("orderItems", orderItems);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute("orderId",orderIdParam);
            request.getRequestDispatcher("/WEB-INF/views/orders/checkout.jsp").forward(request, response);
        } catch (Exception e) {
            handleError(request, response, "Failed to prepare checkout: " + e.getMessage());
        }
    }

    private void processCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            String orderIdParam = request.getParameter("orderId");
            int orderId = Integer.parseInt(orderIdParam);
            String paymentMethod = request.getParameter("paymentMethod");

            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            // Update the payment method before completing the order
            order.setPaymentMethod(paymentMethod);

            // You'll need to add this method to OrderService to update the payment method
            orderService.updateOrderPaymentMethod(order.getOrderId(), paymentMethod);

            orderService.completeOrder(order.getOrderId());
            session.setAttribute("message", "Order completed successfully!");
            response.sendRedirect(request.getContextPath() + "/orders");
        }
        catch (EventManagementException e) {
            handleError(request, response, "Failed to process order: " + e.getMessage());
        }
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            Order order = orderService.getOrderById(orderId);

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            // Check if the order belongs to the current user
            if (order.getAttendee().getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            // Check if the order can be cancelled
//            if (!order.getStatus().equals(Order.OrderStatus.PENDING.toString()) &&
//                    !order.getStatus().equals(Order.OrderStatus.CANCELLED.toString())) {
//                request.setAttribute("error", "This order cannot be cancelled");
//                request.getRequestDispatcher("/WEB-INF/views/orders/" + orderId).forward(request, response);
//                return;
//            }


            orderService.cancelOrder(orderId);

            session.setAttribute("message", "Order cancelled successfully");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (NumberFormatException | EventManagementException e) {
            handleError(request, response, "Failed to cancel order: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getCart(HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
    }
}