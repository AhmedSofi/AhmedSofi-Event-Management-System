package com.ems.controllers;

import com.ems.exceptions.EventManagementException;
import com.ems.models.*;
import com.ems.observers.InAppNotificationObserver;
import com.ems.services.*;
import com.ems.utils.FileUtils;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(name = "EventServlet", urlPatterns = {"/events", "/events/*","/my-events","/speakers/*", "/agenda/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 10 * 1024 * 1024,   // 5 MB
        maxRequestSize = 15 * 1024 * 1024 // 10 MB
)

public class EventServlet extends HttpServlet {
    private EventService eventService;
    private UserService userService;
    private InAppNotificationObserver inAppNotificationObserver;
    private TicketService ticketService;
    private VenueService VenueService;
    private OrderService orderService;
    private FeedbackService feedbackService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.eventService = new EventService();
        this.userService = new UserService();
        this.ticketService = new TicketService();
        this.VenueService = new VenueService();
        this.orderService = new OrderService();
        this.feedbackService = new FeedbackService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo+" in EventServlet doGet");
        if (pathInfo == null || pathInfo.equals("/")) {
            // List all events
            listEvents(request, response);
        } else if (pathInfo.equals("/new")) {
            // Show new event form
            showNewEventForm(request, response);
        } else if (pathInfo.matches("/\\d+")) {
            // View single event (ID in path)
            viewEvent(request, response);
        } else if (pathInfo.matches("/\\d+/edit")) {
            // Show edit form for event
            showEditEventForm(request, response);
        } else if (pathInfo.equals("/my-events")) {
            // List events organized by current user
            listMyEvents(request, response);
        } else if (pathInfo.matches("/\\d+/cancel")) {
            // Cancel event
            cancelEvent(request, response);
        }
        else if(pathInfo.matches("/\\d+/speakers/delete/\\d+"))
        {
            removeSpeakerFromEvent(request, response);
        }
        else if(pathInfo.matches("/\\d+/agenda/delete/\\d+"))
        {
            // Delete agenda items from event
            deleteAgendaItem(request, response);
        }
        else
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo == null || pathInfo.equals("/")) {
            // Create new event
            createEvent(request, response);
        } else if (pathInfo.matches("/\\d+")) {
            // Update existing event
            updateEvent(request, response);
        }
        else if(pathInfo.matches("/\\d+/speakers"))
        {
            // Add speakers to event
            addSpeakerToEvent(request, response);
        }
        else if (pathInfo.matches("/\\d+/speakers/edit/\\d+")) {
            // Edit speaker in event
            updateSpeakerInEvent(request, response);
        }
        else if(pathInfo.matches("/\\d+/agenda"))
        {
            // Add agenda items to event
            addAgendaItem(request, response);
        }

        else if(pathInfo.matches("/\\d+/agenda/edit/\\d+"))
        {
            // Edit agenda items in event
            updateAgendaItem(request, response);
        }
       else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // Process adding a speaker to an event
    private void addSpeakerToEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int eventId = Integer.parseInt(request.getPathInfo().substring(1).split("/")[0]);

            // Create speaker from request parameters
            Speaker speaker = new Speaker();
            speaker.setName(request.getParameter("name"));
            speaker.setBio(request.getParameter("bio"));

            // Add speaker to event
            eventService.addSpeakerToEvent(speaker, eventId);

            // Redirect to event details
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateSpeakerInEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().substring(1).split("/"); // ["eventId", "speakers", "edit", "speakerId"]
            int eventId = Integer.parseInt(pathParts[0]);
            int speakerId = Integer.parseInt(pathParts[3]);

            // Read new data from form
            String name = request.getParameter("name");
            String bio = request.getParameter("bio");

            // Create and update speaker
            Speaker speaker = new Speaker();
            speaker.setSpeakerId(speakerId);
            speaker.setName(name);
            speaker.setBio(bio);

            eventService.updateSpeaker(speaker);

            // Redirect back to speaker list for the event
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event or speaker ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    // Remove a speaker from an event
    private void removeSpeakerFromEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().substring(1).split("/");
            int eventId = Integer.parseInt(pathParts[0]);
            int speakerId = Integer.parseInt(pathParts[3]);

            eventService.removeSpeakerFromEvent(eventId, speakerId);

            // Redirect back to event speakers page
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event or speaker ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Process adding an agenda item
    private void addAgendaItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int eventId = Integer.parseInt(request.getPathInfo().substring(1).split("/")[0]);

            // Create agenda item from request parameters
            AgendaItem agendaItem = new AgendaItem();
            agendaItem.setEventId(eventId);
            agendaItem.setTitle(request.getParameter("title"));
            agendaItem.setDescription(request.getParameter("description"));
            // Add agenda item
            eventService.addAgendaItem(agendaItem);

            // Redirect to event agenda
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Process updating an agenda item
    private void updateAgendaItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().substring(1).split("/");
            int eventId = Integer.parseInt(pathParts[0]);
            int agendaItemId = Integer.parseInt(pathParts[3]);

            // Create agenda item from request parameters
            AgendaItem agendaItem = new AgendaItem();
            agendaItem.setAgendaItemId(agendaItemId);
            agendaItem.setEventId(eventId);
            agendaItem.setTitle(request.getParameter("title"));
            agendaItem.setDescription(request.getParameter("description"));

            // Update agenda item
            eventService.updateAgendaItem(agendaItem);

            // Redirect to event agenda
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event or agenda item ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Delete an agenda item
    private void deleteAgendaItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String[] pathParts = request.getPathInfo().substring(1).split("/");
            int eventId = Integer.parseInt(pathParts[0]);
            int agendaItemId = Integer.parseInt(pathParts[3]);

            // Delete agenda item
            eventService.deleteAgendaItem(agendaItemId);

            // Redirect to event agenda
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid event or agenda item ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }




    private void listEvents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            List<Event> upcomingEvents = eventService.getAllNotCancelledEvents();
            List<Event> canceledEvents = eventService.getCanceledEvents();
            List<Event> pastEvents = eventService.getPastEvents(); // Add this line

            request.setAttribute("upcomingEvents", upcomingEvents);
            request.setAttribute("canceledEvents", canceledEvents);
            request.setAttribute("pastEvents", pastEvents); // Add this line
            request.getRequestDispatcher("/WEB-INF/views/events/list.jsp").forward(request, response);
        } catch (EventManagementException e) {
            request.setAttribute("error", "Failed to retrieve events: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }

    private void listMyEvents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {

            List<Event> events = eventService.getEventsByOrganizer(user.getUserId());
            request.setAttribute("events", events);

            request.getRequestDispatcher("/WEB-INF/views/events/my-events.jsp").forward(request, response);
        } catch (EventManagementException e) {
            request.setAttribute("error", "Failed to retrieve your events: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }

    private void showNewEventForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != User.UserRole.ORGANIZER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            List<Venue> venues = VenueService.getAllVenues(); // Get all venues
            request.setAttribute("venues", venues); // Set attribute for JSP
        } catch (EventManagementException e) {
            request.setAttribute("error", "Could not load venues: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
    }

    private void showEditEventForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int eventId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            Event event = eventService.getEventById(eventId);

            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Check if current user is the organizer
            if (event.getOrganizer().getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<Venue>venues =VenueService.getAllVenues();

            request.setAttribute("event", event);
            request.setAttribute("venues", venues);
            request.getRequestDispatcher("/WEB-INF/views/events/edit.jsp").forward(request, response);
        } catch (NumberFormatException | EventManagementException e) {
            request.setAttribute("error", "Invalid event ID: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }

    private void viewEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            int eventId = Integer.parseInt(request.getPathInfo().substring(1));
            Event event = eventService.getEventById(eventId);

            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Date startDate = Date.from(event.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(event.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant());

            int ticketsSold=0;
            int totalCapacity=0;
            List<Ticket>tickets = ticketService.getTicketsByEvent(eventId);
            if(tickets.isEmpty())
            {
                ticketsSold=orderService.getSoldQuantityByEventID(eventId);
                totalCapacity=ticketsSold;
            }
            else
            {
                for(Ticket ticket : tickets){
                    ticketsSold += orderService.getTotalQuantityByTicketId(ticket.getTicketId());
                    totalCapacity += ticket.getQuantityAvailable()+orderService.getTotalQuantityByTicketId(ticket.getTicketId());
                }
            }


           List<Ticket>isAvailableTickets=new ArrayList<>();
           for (Ticket ticket : tickets) {
               if (ticket.getQuantityAvailable() > 0) {
                   isAvailableTickets.add(ticket);
               }
               else
               {
                   ticketService.deleteTicket(ticket.getTicketId());
               }
           }

            List<Speaker> speakers = eventService.getEventSpeakers(eventId);
            if (speakers != null) {
                request.setAttribute("speakers", speakers);
            }
            List<AgendaItem> agendaItems = eventService.getEventAgenda(eventId);
            if (agendaItems != null) {
                request.setAttribute("agendaItems", agendaItems);
            }




            request.setAttribute("event", event);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("tickets", isAvailableTickets);
            request.setAttribute("event", event);
            request.setAttribute("ticketsSold", ticketsSold);
            request.setAttribute("totalCapacity", totalCapacity);
            request.setAttribute("averageRating", feedbackService.getAverageRatingByEvent(eventId));
            request.setAttribute("feedbackCount", feedbackService.getFeedbackByEvent(eventId).size());
            request.getRequestDispatcher("/WEB-INF/views/events/view.jsp").forward(request, response);
        } catch (NumberFormatException | EventManagementException e) {
            request.setAttribute("error", "Invalid event ID: " + e.getMessage());
            System.out.println(e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }

    private void createEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        int venueId = Integer.parseInt(request.getParameter("venueId"));
        LocalDateTime startDateTime = LocalDateTime.parse(request.getParameter("startDateTime"));
        LocalDateTime endDateTime = LocalDateTime.parse(request.getParameter("endDateTime"));
        // Validate start date against today's date
        LocalDateTime today = LocalDateTime.now();
        if (startDateTime.isBefore(today)) {
            request.setAttribute("error", "Start date cannot be earlier than today's date");
            // Redirect back to the form page
            request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
            return;
        }
        if (endDateTime.isBefore(startDateTime)) {
            request.setAttribute("error", "End date cannot be earlier than start date");
            // Redirect back to the form page
            request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
            return;
        }


        // Check venue availability before creating the event
        if (!eventService.isVenueAvailable(venueId,-1, startDateTime, endDateTime)) {
            request.setAttribute("error", "The selected venue is already booked during this time.");
            List<Venue> venues = VenueService.getAllVenues(); // for repopulating the form
            request.setAttribute("venues", venues);
            request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
            return;
        }


        if (user == null || user.getRole() != User.UserRole.ORGANIZER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }



// Handle file upload
        String imageUrl = null;
        Part filePart = request.getPart("eventImage");
        if (filePart != null && filePart.getSize() > 0) {
            imageUrl = FileUtils.saveEventImage(filePart, request.getServletContext().getRealPath(""));
        }



        try {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            startDateTime = LocalDateTime.parse(
                    request.getParameter("startDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
            endDateTime = LocalDateTime.parse(
                    request.getParameter("endDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
            // Get current user as organizer
            User organizer = (User) request.getSession().getAttribute("user");
            Venue venue = new Venue(venueId, null, null, 0); // only ID is required
            Event event = eventService.createEvent(title, description, startDateTime, endDateTime, organizer, venue);
            // Set image URL if uploaded
            if (imageUrl != null) {
                event.setImageUrl(imageUrl);
                eventService.updateEvent(event);
            }

            response.sendRedirect(request.getContextPath() + "/events/" + event.getEventId());
        } catch (Exception e) {
            request.setAttribute("error", "Failed to create event: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void updateEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int eventId = Integer.parseInt(request.getPathInfo().substring(1));
            Event event = eventService.getEventById(eventId);

            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (event.getStatus() == Event.EventStatus.CANCELLED) {
                session.setAttribute("error", "Event cancelled.");
                response.sendRedirect(request.getContextPath() + "/events/" + event.getEventId());
                return;
            }

            // Check if current user is the organizer
            if (event.getOrganizer().getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Update event details
            event.setTitle(request.getParameter("title"));
            event.setDescription(request.getParameter("description"));
            event.setStartDateTime(LocalDateTime.parse(
                    request.getParameter("startDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ));
            event.setEndDateTime(LocalDateTime.parse(
                    request.getParameter("endDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ));

            if (LocalDateTime.parse(
                    request.getParameter("endDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ).isBefore(LocalDateTime.parse(
                    request.getParameter("startDateTime"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ))) {
                request.setAttribute("error", "End date cannot be earlier than start date");
                // Redirect back to the form page
                request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
                return;
            }




            // Check venue availability before updating the event
            if(request.getParameter("venueId").equals(""))
            {
                request.setAttribute("error", "Please select a venue");
                // Redirect back to the form page
                request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
                return;
            }
            Venue venue =VenueService.getVenueById(Integer.parseInt(request.getParameter("venueId"))) ;
            event.setVenue(venue);


            int venueId = Integer.parseInt(request.getParameter("venueId"));
            LocalDateTime startDateTime = LocalDateTime.parse(request.getParameter("startDateTime"));
            LocalDateTime endDateTime = LocalDateTime.parse(request.getParameter("endDateTime"));

            if (!eventService.isVenueAvailable(venueId,eventId,startDateTime, endDateTime)) {
                request.setAttribute("error", "The selected venue is already booked during this time.");
                List<Venue> venues = VenueService.getAllVenues(); // for repopulating the form
                request.setAttribute("venues", venues);
                request.getRequestDispatcher("/WEB-INF/views/events/new.jsp").forward(request, response);
                return;
            }


            // Handle image
            boolean removeImage = "on".equals(request.getParameter("removeImage"));
            if (removeImage) {
                // Delete the old image file
                if (event.getImageUrl() != null) {
                    FileUtils.deleteEventImage(event.getImageUrl(), request.getServletContext().getRealPath(""));
                    event.setImageUrl(null);
                }
            }

            // Check for new image upload
            Part filePart = request.getPart("eventImage");
            if (filePart != null && filePart.getSize() > 0) {
                // Delete old image if exists
                if (event.getImageUrl() != null) {
                    FileUtils.deleteEventImage(event.getImageUrl(), request.getServletContext().getRealPath(""));
                }

                // Save new image
                String imageUrl = FileUtils.saveEventImage(filePart, request.getServletContext().getRealPath(""));
                event.setImageUrl(imageUrl);
            }

            if(request.getParameter("status").equals(""))
            {
                request.setAttribute("error", "Please select a status");
                return;
            }
            else
            {
                event.setStatus(Event.EventStatus.valueOf(request.getParameter("status").toUpperCase()));
            }

            eventService.updateEvent(event);
            response.sendRedirect(request.getContextPath() + "/events/" + event.getEventId());
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update event: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void cancelEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int eventId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            Event event = eventService.getEventById(eventId);

            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Check if current user is the organizer
            if (event.getOrganizer().getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            eventService.cancelEvent(eventId);
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to cancel event: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
