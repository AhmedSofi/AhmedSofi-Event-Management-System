package com.ems.services;

import com.ems.config.DatabaseConnection;
import com.ems.dao.FeedbackDAO;
import com.ems.exceptions.EventManagementException;
import com.ems.models.Event;
import com.ems.models.Feedback;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private final FeedbackDAO feedbackDao;

    public FeedbackService() {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        this.feedbackDao = new FeedbackDAO(conn);
    }

    public void submitFeedback(Feedback feedback) throws EventManagementException {
        try {
            feedbackDao.saveFeedback(feedback);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to submit feedback", e);
        }
    }

    public List<Feedback> getFeedbackByUser(int userId) throws EventManagementException {
        try {
            return feedbackDao.getFeedbackByUser(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve feedback", e);
        }

    }
    public double getAverageRatingByEvent(int eventId) throws EventManagementException {
        try {
            return feedbackDao.getAverageRating(eventId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to get average rating", e);
        }
    }
    public boolean isFeedbackSubmitted(int eventId, int userId) throws EventManagementException {
        try {
            return feedbackDao.isFeedbackExists(eventId, userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to check feedback submission", e);
        }
    }

    public void updateFeedback(Feedback feedback) throws EventManagementException {
        try {
            feedbackDao.updateFeedback(feedback);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to update feedback", e);
        }
    }
    public void deleteFeedback(int eventId,int userId) throws EventManagementException {
        try {
            feedbackDao.deleteFeedback(eventId, userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to delete feedback", e);
        }
    }

    public List<Feedback> getFeedbackByEvent(int eventId) throws EventManagementException {
        try {
            return feedbackDao.getFeedbackByEvent(eventId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to retrieve feedback", e);
        }
    }

    public List<Event> getEventsAttendedByUser(int userId) throws EventManagementException {
        try {
            return feedbackDao.getEventsAttendedByUser(userId);
        } catch (SQLException e) {
            throw new EventManagementException("Failed to get attended events", e);
        }
    }}
