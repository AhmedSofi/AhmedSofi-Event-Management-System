package com.ems.controllers;

import com.ems.exceptions.EventManagementException;
import com.ems.models.Event;
import com.ems.models.Feedback;
import com.ems.models.User;
import com.ems.services.EventService;
import com.ems.services.FeedbackService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/feedback/*", "/feedback/my"})
public class FeedbackServlet extends HttpServlet {
    private FeedbackService feedbackService;
    private EventService eventService;

    @Override
    public void init() {
        this.feedbackService = new FeedbackService();
        this.eventService = new EventService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        System.out.println("🟢 FeedbackServlet - path: " + path);


        try {

        if("/feedback/my".equals(path)){
            List<Feedback> feedbacks = feedbackService.getFeedbackByUser(user.getUserId());
            request.setAttribute("feedbacks", feedbacks);
            request.getRequestDispatcher("/WEB-INF/views/feedback/list.jsp").forward(request, response);

        }
           else if (path == null || path.equals("/feedback") || path.equals("/submit")) {
               System.out.println("this is submit");
                List<Event> attendedEvents = feedbackService.getEventsAttendedByUser(user.getUserId());
                request.setAttribute("events", attendedEvents);
                request.getRequestDispatcher("/WEB-INF/views/feedback/submit.jsp").forward(request, response);


            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND); // ✅ only called if nothing matches
            }
        } catch (EventManagementException e) {
            request.setAttribute("error", "Error loading feedback: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/errors/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        HttpSession session = request.getSession();

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int eventId = Integer.parseInt(request.getParameter("eventId"));

            String  ratingStr = request.getParameter("rating");
            if(ratingStr == null || ratingStr.isEmpty()) {
                if(feedbackService.isFeedbackSubmitted(eventId, user.getUserId()))
                {
                    feedbackService.deleteFeedback(eventId, user.getUserId());
                }
                response.sendRedirect(request.getContextPath() + "/feedback/my");
                return;
            }
            int rating = Integer.parseInt(ratingStr);
            String comments = request.getParameter("comments");

            Event event = new Event();
            event.setEventId(eventId);

            Feedback feedback = new Feedback();
            feedback.setAttendee(user);
            feedback.setEvent(event);
            feedback.setRating(rating);
            feedback.setComments(comments);

            Event event1 = eventService.getEventById(eventId);
            if (event1 == null || !LocalDateTime.now().isAfter(event1.getEndDateTime())) {
                session.setAttribute("error", "You can only submit feedback for events that have ended.");
                response.sendRedirect(request.getContextPath() + "/feedback/my");
                return;
            }



            if(feedbackService.isFeedbackSubmitted(eventId, user.getUserId()))
            {
                feedbackService.updateFeedback(feedback);
            }
            else
            {
                feedbackService.submitFeedback(feedback);
            }


            response.sendRedirect(request.getContextPath() + "/feedback/my");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to submit feedback: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }
}
