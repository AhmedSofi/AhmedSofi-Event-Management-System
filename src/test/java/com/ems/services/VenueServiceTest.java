package com.ems.services;

import com.ems.dao.VenueDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VenueServiceTest {

    private VenueService venueService;
    private VenueDAO venueDAO;

    @BeforeEach
    void setUp() {
        venueDAO = mock(VenueDAO.class);
        venueService = new VenueService(venueDAO);
    }

    @Test
    @DisplayName("Adding a new venue with valid input should succeed")
    void createVenue_ValidInput_Success() throws SQLException {
        // Setup
        String venueName = "Banquet Hall";
        String address = "King Faisal Street, Riyadh";
        int capacity = 500;

        Venue venue = new Venue(1, venueName, address, capacity);
        when(venueDAO.createVenue(any(Venue.class))).thenReturn(true);

        // Execution
        boolean result = venueService.createVenue(venue);

        // Verification
        assertTrue(result);
        verify(venueDAO).createVenue(any(Venue.class));
    }

    @Test
    @DisplayName("Database error during venue creation should throw exception")
    void createVenue_DatabaseError_ThrowsException() throws SQLException {
        // Setup
        Venue venue = new Venue(1, "Banquet Hall", "King Faisal Street, Riyadh", 500);
        when(venueDAO.createVenue(any(Venue.class))).thenThrow(new SQLException("Database connection error"));

        // Execution & Verification
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> venueService.createVenue(venue)
        );

        assertEquals("Failed to create venue", exception.getMessage());
        assertTrue(exception.getCause() instanceof SQLException);
    }

    @Test
    @DisplayName("Retrieving all venues should succeed")
    void getAllVenues_Success() throws SQLException {
        // Setup
        List<Venue> venues = Arrays.asList(
                new Venue(1, "Banquet Hall", "King Faisal Street, Riyadh", 500),
                new Venue(2, "Conference Center", "Olaya Street, Riyadh", 1000)
        );
        when(venueDAO.getAllVenues()).thenReturn(venues);

        // Execution
        List<Venue> result = venueService.getAllVenues();

        // Verification
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Banquet Hall", result.get(0).getName());
        assertEquals("Conference Center", result.get(1).getName());
        verify(venueDAO).getAllVenues();
    }

    @Test
    @DisplayName("Retrieving a venue by valid ID should succeed")
    void getVenueById_ValidId_Success() throws SQLException {
        // Setup
        int venueId = 5;
        Venue venue = new Venue(venueId, "Banquet Hall", "King Faisal Street, Riyadh", 500);
        when(venueDAO.getVenueById(venueId)).thenReturn(venue);

        // Execution
        Venue result = venueService.getVenueById(venueId);

        // Verification
        assertNotNull(result);
        assertEquals(venueId, result.getVenueId());
        assertEquals("Banquet Hall", result.getName());
        verify(venueDAO).getVenueById(venueId);
    }

    @Test
    @DisplayName("Retrieving a venue by non-existent ID should return null")
    void getVenueById_NonExistentId_ReturnsNull() throws SQLException {
        // Setup
        int venueId = 999;
        when(venueDAO.getVenueById(venueId)).thenReturn(null);

        // Execution & Verification
        assertNull(venueService.getVenueById(venueId));
        verify(venueDAO).getVenueById(venueId);
    }

    @Test
    @DisplayName("Updating a venue with valid input should succeed")
    void updateVenue_ValidInput_Success() throws SQLException {
        // Setup
        int venueId = 1;
        String venueName = "Updated Banquet Hall";
        String address = "King Faisal Street, Riyadh";
        int capacity = 600;

        Venue venue = new Venue(venueId, venueName, address, capacity);
        when(venueDAO.updateVenue(any(Venue.class))).thenReturn(true);

        // Execution
        boolean result = venueService.updateVenue(venue);

        // Verification
        assertTrue(result);
        verify(venueDAO).updateVenue(any(Venue.class));
    }

    @Test
    @DisplayName("Deleting an existing venue should succeed")
    void deleteVenue_ExistingVenue_Success() throws SQLException {
        // Setup
        int venueId = 1;
        when(venueDAO.deleteVenue(venueId)).thenReturn(true);

        // Execution
        boolean result = venueService.deleteVenue(venueId);

        // Verification
        assertTrue(result);
        verify(venueDAO).deleteVenue(venueId);
    }

    @Test
    @DisplayName("Database error during venue deletion should throw exception")
    void deleteVenue_DatabaseError_ThrowsException() throws SQLException {
        // Setup
        int venueId = 1;
        when(venueDAO.deleteVenue(venueId)).thenThrow(new SQLException("Database connection error"));

        // Execution & Verification
        EventManagementException exception = assertThrows(
                EventManagementException.class,
                () -> venueService.deleteVenue(venueId)
        );

        assertEquals("Failed to delete venue", exception.getMessage());
        assertTrue(exception.getCause() instanceof SQLException);
    }
}
