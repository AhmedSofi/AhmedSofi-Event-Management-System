package com.ems.services;

import com.ems.dao.EventDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Event;
import com.ems.models.User;
import com.ems.models.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    private EventService eventService;
    private EventDAO eventDAO;

    @BeforeEach
    void setUp() {
        eventDAO = mock(EventDAO.class);
        eventService = new EventService(eventDAO);
    }

    @Test
    @DisplayName("Create event with valid inputs should succeed")
    void createEvent_ValidInput_Success() throws SQLException {
        // Arrange
        String title = "Test Event";
        String description = "Test Description";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1).plusHours(2);
        User organizer = new User(1, "organizer", "password", "org@example.com", "John", "Doe", User.UserRole.ORGANIZER);
        Venue venue = new Venue(1, "Test Venue", "123 Test St", 100);

        Event inputEvent = new Event(0, title, description, startDateTime, endDateTime, venue, organizer, null);
        Event createdEvent = new Event(1, title, description, startDateTime, endDateTime, venue, organizer, null);

        when(eventDAO.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        Event result = eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getEventId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(startDateTime, result.getStartDateTime());
        assertEquals(endDateTime, result.getEndDateTime());
        assertEquals(venue, result.getVenue());
        assertEquals(organizer, result.getOrganizer());
        verify(eventDAO).createEvent(any(Event.class));
    }

    @Test
    @DisplayName("Create event with database error should throw exception")
    void createEvent_DatabaseError_ThrowsException() throws SQLException {
        // Arrange
        String title = "Test Event";
        String description = "Test Description";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1).plusHours(2);
        User organizer = new User(1, "organizer", "password", "org@example.com", "John", "Doe", User.UserRole.ORGANIZER);
        Venue venue = new Venue(1, "Test Venue", "123 Test St", 100);

        when(eventDAO.createEvent(any(Event.class))).thenThrow(new SQLException("Database connection error"));

        // Act & Assert
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue)
        );

        assertEquals("Failed to create event", exception.getMessage());
        assertTrue(exception.getCause() instanceof SQLException);
    }

    @Test
    @DisplayName("Create event with null title should fail")
    void createEvent_NullTitle_ThrowsException() throws SQLException {
        // Arrange
        String title = null;
        String description = "Test Description";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1).plusHours(2);
        User organizer = new User(1, "organizer", "password", "org@example.com", "John", "Doe", User.UserRole.ORGANIZER);
        Venue venue = new Venue(1, "Test Venue", "123 Test St", 100);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue)
        );

        verify(eventDAO, never()).createEvent(any(Event.class));
    }

    @Test
    @DisplayName("Create event with null organizer should fail")
    void createEvent_NullOrganizer_ThrowsException() throws SQLException {
        // Arrange
        String title = "Test Event";
        String description = "Test Description";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1).plusHours(2);
        User organizer = null;
        Venue venue = new Venue(1, "Test Venue", "123 Test St", 100);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue)
        );

        verify(eventDAO, never()).createEvent(any(Event.class));
    }

    @Test
    @DisplayName("Create event with invalid date range should fail")
    void createEvent_InvalidDateRange_ThrowsException() throws SQLException {
        // Arrange
        String title = "Test Event";
        String description = "Test Description";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);  // End before start
        LocalDateTime endDateTime = LocalDateTime.now().plusDays(1);
        User organizer = new User(1, "organizer", "password", "org@example.com", "John", "Doe", User.UserRole.ORGANIZER);
        Venue venue = new Venue(1, "Test Venue", "123 Test St", 100);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue)
        );

        verify(eventDAO, never()).createEvent(any(Event.class));
    }
}