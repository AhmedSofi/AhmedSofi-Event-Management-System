package com.ems.controllers;

import com.ems.exceptions.EventManagementException;
import com.ems.models.*;
import com.ems.services.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

@WebServlet(name = "UserServlet", urlPatterns = {"/register", "/login", "/logout", "/profile","/changePassword","/editProfile","/forgotPassword","/resetPassword"})
public class UserServlet extends HttpServlet {
    private UserService userService;
    private EventService eventService;
    private OrderService orderService;
    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
        this.eventService = new EventService();
        this.orderService = new OrderService();
        this.feedbackService = new FeedbackService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println(path);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // List of paths that should not be accessible when logged in
        List<String> restrictedPaths = Arrays.asList("/login", "/register","/forgotPassword","/resetPassword");

        if (user != null && restrictedPaths.contains(path)) {
            // User is already logged in, redirect to profile or home
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        switch (path) {
            case "/register":
                showRegistrationForm(request, response);
                break;
            case "/login":
                showLoginForm(request, response);
                break;
            case "/logout":
                logoutUser(request, response);
                break;
            case "/profile":
                showUserProfile(request, response);
                break;
            case "/forgotPassword":
                showForgotPasswordForm(request, response);
                break;
            case "/resetPassword":
                showResetPasswordForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println(path);
        switch (path) {
            case "/register":
                registerUser(request, response);
                break;
            case "/login":
                loginUser(request, response);
                break;
            case "/changePassword":
                changePassword(request, response);
                break;
            case "/editProfile":
                updateUser(request, response);
                break;
            case "/forgotPassword":
                forgotPassword(request, response);
                break;
            case "/resetPassword":
                resetPassword(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showRegistrationForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/users/login.jsp").forward(request, response);
    }



    private void showForgotPasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/users/forgotPassword.jsp").forward(request, response);
    }

    private void showResetPasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Invalid or expired token.");
            request.getRequestDispatcher("/WEB-INF/views/users/forgotPassword.jsp").forward(request, response);
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("/WEB-INF/views/users/resetPassword.jsp").forward(request, response);
    }
    private void showUserProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if(user.getRole() == User.UserRole.ORGANIZER) {
            List<Event> events = eventService.getEventsByOrganizer(user.getUserId());
            request.setAttribute("events", events);


        } else {
            Event event = eventService.getEventById(user.getUserId()); // Single Event

            List<Event> events = new ArrayList<>();
            events.add(event);
            request.setAttribute("events", events); // Pass as a list to the JSP
        }

        List<Order> orders = orderService.getOrdersByUser(user.getUserId());
        if (orders == null) {
            orders = new ArrayList<>();
        }
        request.setAttribute("orders", orders);

        List<Feedback> feedbacks = feedbackService.getFeedbackByUser(user.getUserId());
        if (feedbacks == null) {
            feedbacks = new ArrayList<>();
        }
        request.setAttribute("feedbacks", feedbacks);

        Date createdAtDate = Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        request.setAttribute("createdAtDate", createdAtDate);

        request.getRequestDispatcher("/WEB-INF/views/users/profile.jsp").forward(request, response);
    }
    private void resetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Check if token is missing
        if (token == null || token.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgotPassword");
            return;
        }

        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/users/resetPassword.jsp").forward(request, response);
            return;
        }

        try {
            // Reset password using token
            userService.resetPasswordWithToken(token, newPassword);
            response.sendRedirect(request.getContextPath() + "/login?message=Password has been reset successfully. Please login with your new password.");
        } catch (EventManagementException | SQLException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/users/resetPassword.jsp").forward(request, response);
        }
    }
    private void forgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        try {
            // Generate and store password reset token
            String token = userService.generateResetToken(email);

            // Create reset URL
            String resetUrl = request.getScheme() + "://" + request.getServerName() + ":" +
                    request.getServerPort() + request.getContextPath() +
                    "/resetPassword?token=" + token;

            // Email subject and body
            String subject = "Password Reset Request";
            String body = "Hello,\n\n" +
                    "You have requested to reset your password. Please click the link below to reset it:\n\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 15 minutes.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Regards,\nEvent Management System";

            // Send email with reset link
            new EmailService().sendEmail(email, subject, body);

            request.setAttribute("message", "A password reset link has been sent to your email address.");
        } catch (EventManagementException | SQLException e) {
            request.setAttribute("error", "Failed to process your request: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/users/forgotPassword.jsp").forward(request, response);
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String email = request.getParameter("email");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            User.UserRole role = User.UserRole.valueOf(request.getParameter("role"));

            // Check if password and confirm password match
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
                return;
            }
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                request.setAttribute("error", "Invalid email format.");
                request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
                return;
            }

            User user = userService.registerUser(username, password, email, firstName, lastName, role);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());


            if (role == User.UserRole.ORGANIZER) {
                response.sendRedirect(request.getContextPath() + "/events/my-events");
            } else {
                response.sendRedirect(request.getContextPath() + "/events");
            }
        } catch (EventManagementException e) {
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = userService.authenticateUser(username, password);

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());

            response.sendRedirect(request.getContextPath() + "/");
        } catch (EventManagementException e) {
            request.setAttribute("error", "Login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/users/login.jsp").forward(request, response);
        }
    }

    private void logoutUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();
//        response.sendRedirect(request.getContextPath() + "/login");
        request.getRequestDispatcher("/WEB-INF/views/users/login.jsp").forward(request, response);

    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmNewPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("passwordMismatchError", "New password and confirmation do not match.");
            request.setAttribute("openChangePasswordModal", true);
            showUserProfile(request, response);
            return;
        }

        try {
            userService.changePassword(user.getUserId(), currentPassword, newPassword);
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?message=Password changed successfully.");
        } catch (EventManagementException e) {
            request.setAttribute("changePasswordError", e.getMessage());
            request.setAttribute("openChangePasswordModal", true);
            showUserProfile(request, response);
        }
    }

    public void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        System.out.println("Try update");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        System.out.println("Try update");

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        try {

            System.out.println("Try update");
            user.setFirstName(firstName);
            user.setLastName(lastName);


            if(!user.getEmail().equals(email))
            {
                if(userService.getUserByEmail(email) != null)
                {

                    request.setAttribute("error", "Email already exists.");
                    showUserProfile(request, response);
                    return;
                }
                else
                {
                    user.setEmail(email);
                }

            }

            userService.updateUser(user);
            request.setAttribute("success", "Profile updated successfully.");
            showUserProfile(request, response);
        } catch (EventManagementException e) {
            request.setAttribute("error", "Failed to update profile: " + e.getMessage());
            showUserProfile(request, response);
        }
    }
}