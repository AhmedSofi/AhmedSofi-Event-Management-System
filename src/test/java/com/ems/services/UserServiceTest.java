package com.ems.services;

import com.ems.dao.UserDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    @DisplayName("Register user with valid inputs should succeed")
    void registerUser_ValidInput_Success() throws SQLException {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        // Mock behavior
        when(userDAO.getUserByUsername(username)).thenReturn(null);
        when(userDAO.getUserByEmail(email)).thenReturn(null);

        User createdUser = new User(1, username, "hashedPassword", email, firstName, lastName, role);
        when(userDAO.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        User result = userService.registerUser(username, password, email, firstName, lastName, role);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(role, result.getRole());
        verify(userDAO).createUser(any(User.class));
    }

    @Test
    @DisplayName("Register with existing username should throw exception")
    void registerUser_UsernameExists_ThrowsException() throws SQLException {
        // Arrange
        String username = "existingUser";
        String password = "password123";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        User existingUser = new User(1, username, "hashedPassword", "another@email.com", "Existing", "User", role);
        when(userDAO.getUserByUsername(username)).thenReturn(existingUser);

        // Act & Assert
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userDAO, never()).createUser(any());
    }

    @Test
    @DisplayName("Register with existing email should throw exception")
    void registerUser_EmailExists_ThrowsException() throws SQLException {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String email = "existing@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        when(userDAO.getUserByUsername(username)).thenReturn(null);
        User existingUser = new User(1, "anotherUser", "hashedPassword", email, "Existing", "User", role);
        when(userDAO.getUserByEmail(email)).thenReturn(existingUser);

        // Act & Assert
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(userDAO, never()).createUser(any());
    }

    @Test
    @DisplayName("Register with empty username should throw exception")
    void registerUser_EmptyUsername_ThrowsException() throws SQLException {
        // Arrange
        String username = "";
        String password = "password123";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Username cannot be empty", exception.getMessage());
        verify(userDAO, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Register with empty password should throw exception")
    void registerUser_EmptyPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Password cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Register with empty email should throw exception")
    void registerUser_EmptyEmail_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Database error during registration should throw exception")
    void registerUser_DatabaseError_ThrowsException() throws SQLException {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";
        User.UserRole role = User.UserRole.ATTENDEE;

        when(userDAO.getUserByUsername(username)).thenReturn(null);
        when(userDAO.getUserByEmail(email)).thenReturn(null);
        when(userDAO.createUser(any())).thenThrow(new SQLException("Database connection error"));

        // Act & Assert
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> userService.registerUser(username, password, email, firstName, lastName, role)
        );

        assertEquals("Failed to register user", exception.getMessage());
        assertTrue(exception.getCause() instanceof SQLException);
    }
}