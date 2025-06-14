package com.ems.services;

import com.ems.config.DatabaseConnection;
import com.ems.dao.UserDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.ResetToken;
import com.ems.models.User;
import com.ems.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class for user management operations.
 * Handles authentication, registration, and user data management.
 */
public class UserService {
    private UserDAO userDAO;
    private Connection connection;
    private Map<String, ResetToken> resetTokens = new HashMap<>();





    public UserService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.userDAO = new UserDAO(connection);

    }

    public UserService(UserDAO userDAO) {
        this.userDAO =userDAO;
    }



    // Register a new user
    public User registerUser(String username, String password, String email,
                             String firstName, String lastName, User.UserRole role) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Check if username or email already exists
        try {
            if (userDAO.getUserByUsername(username) != null) {
                throw new EventManagementException("Username already exists");
            }
            if (userDAO.getUserByEmail(email) != null) {
                throw new EventManagementException("Email already registered");
            }
        } catch (SQLException e) {
            throw new EventManagementException("Failed to check user existence", e);
        }

        // Hash password
        String passwordHash = PasswordHasher.hashPassword(password);

        // Create user
        User user = new User(0, username, passwordHash, email, firstName, lastName, role);

        try {
            return userDAO.createUser(user);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to register user", e);
        }
    }

    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        userDAO.updatePassword(userId, hashedPassword);
    }

    // Authenticate a user
    public User authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        try {
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                throw new EventManagementException("Invalid username or password");
            }

            if (PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                return user;
            } else {
                throw new EventManagementException("Invalid username or password");
            }
        } catch (SQLException e) {
            throw new EventManagementException("Failed to authenticate user", e);
        }
    }

    public void resetPasswordWithToken(String token, String newPassword) throws SQLException {
        ResetToken resetToken = resetTokens.get(token);

       try {
           if (resetToken == null || resetToken.isExpired()) {
               throw new EventManagementException("Reset token is invalid or expired.");
           }

           if (resetToken.isBlocked()) {
               throw new EventManagementException("Too many failed attempts. This token is now blocked.");
           }

           int userId = resetToken.getUserId();
           User user = getUserById(userId);
           if (user == null) {
               throw new EventManagementException("User not found.");
           }

           String newPasswordHash = PasswordHasher.hashPassword(newPassword);
           userDAO.updatePassword(userId, newPasswordHash);
           resetTokens.remove(token);
       }
         catch (SQLException e) {
             resetToken.incrementAttempts();
              throw new EventManagementException("Failed to reset password", e);
         }
    }

    public String generateResetToken(String email) throws SQLException {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new EventManagementException("User with this email not found.");
        }

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (resetTokens.containsKey(token));
        // Set expiration time to 15 minutes from now
        long expiration = System.currentTimeMillis() + (15 * 60 * 1000); // 15 minutes

        resetTokens.put(token, new ResetToken(token, user.getUserId(), expiration));

        return token;
    }


    // Get user by ID
    public User getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve user", e);
        }
    }
    public User getUserByUsername(String username) {
        try {
            return userDAO.getUserByUsername(username);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve user", e);
        }
    }

    // Update user information
    public User updateUser(User user) {
        try {
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update user", e);
        }
    }

    public User getUserByEmail(String email) {
        try {
            return userDAO.getUserByEmail(email);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve user by email", e);
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve users", e);
        }
    }

    // Change user password
    public void changePassword(int userId, String currentPassword, String newPassword) {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                throw new EventManagementException("User not found");
            }

            if (!PasswordHasher.verifyPassword(currentPassword, user.getPasswordHash())) {
                throw new EventManagementException("Current password is incorrect");
            }

            String newPasswordHash = PasswordHasher.hashPassword(newPassword);
            userDAO.updatePassword(userId, newPasswordHash);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to change password", e);
        }
    }
}
