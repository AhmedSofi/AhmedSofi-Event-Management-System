package com.ems.controllers;

import com.ems.exceptions.EventManagementException;
import com.ems.models.Notification;
import com.ems.models.User;
import com.ems.services.NotificationService;
// Import necessary classes for JSON handling (using a simple approach here)
// You might need a library like Gson or Jackson for more robust JSON handling
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet(name = "NotificationServlet", urlPatterns = {"/notifications", "/notifications/*"})
public class NotificationServlet extends HttpServlet {

    private NotificationService notificationService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.notificationService = new NotificationService(); // Initialize the service
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Default action: list notifications for the current user
            listNotifications(request, response);
        } else if (pathInfo.equals("/poll")) {
            // New endpoint for polling unread count
            pollUnreadCount(request, response);
        } else {
            // Handle other GET requests if needed, e.g., view a single notification
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        // System.out.println("Marking "); // Debugging line
        if (pathInfo != null && pathInfo.equals("/mark-read")) {
            markNotificationRead(request, response);
        } else if (pathInfo != null && pathInfo.equals("/mark-all-read")) {
            // System.out.println("Marking all notifications as read"); // Debugging line
            markAllNotificationsRead(request, response);
        }
        else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // *** Mark all as read (using the logic from previous turn) ***
            notificationService.markAllAsRead(currentUser.getUserId()); // Use correct method name

            List<Notification> notifications = notificationService.getNotificationsForUser(currentUser.getUserId());

            // Convert LocalDateTime to Date for JSP formatting if needed
            // Make sure Notification class has `Date createdDateUtil` field + getter/setter
            for (Notification notification : notifications) {
                if (notification.getCreatedAt() != null) {
                    Date date = Date.from(notification.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
                    notification.setCreatedDateUtil(date);
                } else {
                    notification.setCreatedDateUtil(null); // Handle null case
                }
            }

            request.setAttribute("notifications", notifications);
            request.getRequestDispatcher("/WEB-INF/views/notifications/list.jsp").forward(request, response);
        } catch (EventManagementException e) {
            request.setAttribute("error", "Failed to retrieve notifications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }

    // Mark single notification as read
    private void markNotificationRead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // Get ID from request parameter, not path info
            int notificationId = Integer.parseInt(request.getParameter("notificationId"));
            // Optional: Verify notification belongs to currentUser before marking
            notificationService.markAsRead(notificationId); // Use correct service method
            response.setStatus(HttpServletResponse.SC_OK); // Send OK status for AJAX request
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid notification ID format.");
        } catch (EventManagementException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to mark notification as read.");
        }
    }


    // Mark all notifications as read
    private void markAllNotificationsRead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Use false to avoid creating new session
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // For an AJAX POST, redirect is less ideal, send error or OK status
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not logged in.");
            return;
        }

        try {
            // --- CORRECTED METHOD NAME ---
            notificationService.markAllAsRead(user.getUserId());
            // Send OK status for AJAX request instead of redirect
            response.setStatus(HttpServletResponse.SC_OK);
            // response.sendRedirect(request.getContextPath() + "/notifications"); // Old redirect logic
        } catch (EventManagementException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to mark all notifications as read.");
        }
    }

    // Poll for unread count
    private void pollUnreadCount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Use false
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        // System.out.println("Polling unread count for user: " + user); // Debugging line

        // If user is not logged in, return 0 count instead of redirecting
        if (user == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"unreadCount\": 0}");
            out.flush();
            return;
        }

        int unreadCount = 0;
        try {
            // --- CORRECTED METHOD NAME ---
            unreadCount = notificationService.getUnreadCount(user.getUserId());
        } catch (EventManagementException e) {
            System.err.println("Error fetching unread count for polling: " + e.getMessage());
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"unreadCount\": " + unreadCount + "}");
        out.flush();
    }
}