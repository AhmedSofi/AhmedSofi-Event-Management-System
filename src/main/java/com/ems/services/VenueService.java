package com.ems.services;

import com.ems.dao.VenueDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Venue;
import java.sql.Connection;
import com.ems.config.DatabaseConnection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class VenueService {
    private final VenueDAO venueDao;

    public VenueService() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        this.venueDao = new VenueDAO(connection);
    }

    public VenueService(VenueDAO venueDao) {
        this.venueDao = venueDao;
    }
    
    public boolean updateVenue(Venue venue) throws EventManagementException {
        try {
            return venueDao.updateVenue(venue);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update venue", e);
        }
    }

    public boolean deleteVenue(int venueId) throws EventManagementException {
        try {
            return venueDao.deleteVenue(venueId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to delete venue", e);
        }
    }

    public List<Venue> getAllVenues() throws EventManagementException {
        try {
            return venueDao.getAllVenues();
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve venues", e);
        }
    }

    public Venue getVenueById(int venueId) throws EventManagementException {
        try {
            return venueDao.getVenueById(venueId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve venue with ID: " + venueId, e);
        }
    }
    public boolean createVenue(Venue venue) throws EventManagementException {
        try {
            return venueDao.createVenue(venue);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to create venue", e);
        }
    }

    public boolean isVenueAvailable(int venueId,int eventId, LocalDateTime start, LocalDateTime end) throws EventManagementException {
        try {
            return venueDao.isVenueAvailable(venueId,eventId, start, end);
        } catch (SQLException e) {
            throw new EventManagementException("Error checking venue availability", e);
        }
    }

}