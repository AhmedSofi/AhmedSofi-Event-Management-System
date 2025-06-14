package com.ems.controllers;

import com.ems.exceptions.EventManagementException;
import com.ems.models.Event;
import com.ems.models.Ticket;
import com.ems.services.EventService;
import com.ems.services.TicketService;
import com.ems.models.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(name = "TicketServlet", urlPatterns = { "/tickets", "/tickets/*", "/event-tickets" })
public class TicketServlet extends HttpServlet {
    private TicketService ticketService;
    private EventService eventService; // Added to fetch full Event details

    @Override
    public void init() throws ServletException {
        super.init();
        this.ticketService = TicketService.getInstance();
        // Create a new EventService instance to retrieve event details
        this.eventService = new EventService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listAllTickets(request, response); // Use listAllTickets instead of listTickets
        } else if (pathInfo.equals("/new")) {
            showCreateTicketForm(request, response);
        } else if (pathInfo.matches("/\\d+"))
        {

            viewTicket(request, response);
        }
        else if (pathInfo.matches("/\\d+/update"))
        {

            updateTicket(request, response);
        }
        else if (pathInfo.matches("/\\d+/delete")) {
            deleteTicket(request, response);
        }

        else if (pathInfo != null && pathInfo.matches("/\\d+/update-quantity")) {
            updateTicketQuantity(request, response);
        }


        else
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }



    private void updateTicketQuantity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int ticketId = Integer.parseInt(request.getPathInfo().substring(1).split("/")[0]);
        int quantityChange = Integer.parseInt(request.getParameter("quantityChange"));

        try {
            ticketService.updateTicketQuantity(ticketId, quantityChange);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }





    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            if (pathInfo.equals("/create")) {
                createTicket(request, response);
            } else if (pathInfo.matches("/\\d+/update")) {
                updateTicket(request, response);
            } else if (pathInfo.matches("/\\d+/delete")) {
                deleteTicket(request, response);
            } else if (pathInfo.matches("/\\d+/adjust-quantity")) {
                adjustTicketQuantity(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void adjustTicketQuantity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get ticket ID from URL
            int ticketId = Integer.parseInt(request.getPathInfo().split("/")[1]);

            // Get quantity change from request parameter
            int quantityChange = Integer.parseInt(request.getParameter("quantityChange"));

            // Get the ticket to check if user is the organizer
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return;
            }

            // Check if current user is the organizer
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !currentUser.getRole().equals(User.UserRole.ORGANIZER) ||
                    currentUser.getUserId() != ticket.getEvent().getOrganizer().getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to modify tickets");
                return;
            }

            // Check if decrease would make quantity negative
            if (quantityChange < 0 && Math.abs(quantityChange) > ticket.getQuantityAvailable()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot decrease below zero");
                return;
            }

            // Update ticket quantity
            ticketService.updateTicketQuantity(ticketId, quantityChange);

            // Return success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Quantity updated successfully");
        } catch (NumberFormatException | EventManagementException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to adjust ticket quantity: " + e.getMessage());
        }
    }

    // In TicketServlet.java
    private void listAllTickets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Ticket> tickets = ticketService.getAllTickets(); // Fetch all tickets
            request.setAttribute("tickets", tickets); // Set tickets attribute
            request.getRequestDispatcher("/WEB-INF/views/tickets/list.jsp").forward(request, response); // Forward to
            // JSP
        } catch (EventManagementException e) {
            handleError(request, response, "Failed to retrieve tickets: " + e.getMessage());
        }
    }
    // Modified: List tickets now fetches full event details from EventService.
    // private void listTickets(HttpServletRequest request, HttpServletResponse
    // response)
    // throws ServletException, IOException {
    // try {
    // // Expect eventId as a request parameter (ensure it's passed in the query
    // string)
    // List<Ticket> ticket = ticketService.getAllTickets();
    // // request.setAttribute("events", events);
    // //
    // request.getRequestDispatcher("/WEB-INF/views/events/list.jsp").forward(request,
    // response);
    // // Retrieve full event details
    // Event event = eventService.getEventById(eventId);
    // if (event == null) {
    // response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
    // return;
    // }
    // // Retrieve tickets for the event
    // List<Ticket> tickets = ticketService.getTicketsByEvent(eventId);
    // request.setAttribute("tickets", tickets);
    // request.setAttribute("event", event);
    // request.getRequestDispatcher("/WEB-INF/views/tickets/list.jsp").forward(request,
    // response);
    // } catch (EventManagementException | NumberFormatException e) {
    // handleError(request, response, "Failed to retrieve tickets: " +
    // e.getMessage());
    // }
    // }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int ticketId = Integer.parseInt(request.getPathInfo().substring(1));
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
            } else {
                request.setAttribute("ticket", ticket);
                request.getRequestDispatcher("/WEB-INF/views/tickets/view.jsp").forward(request, response);
            }

            // int eventId = Integer.parseInt(request.getPathInfo().substring(1));
            // Event event = eventService.getEventById(eventId);
            // List<Ticket> tickets = ticketService.getTicketsByEvent(event.getEventId());
            // request.setAttribute("tickets", tickets);
            // request.setAttribute("event", event);
            // request.getRequestDispatcher("/WEB-INF/views/events/view.jsp").forward(request, response);

        } catch (NumberFormatException | EventManagementException e) {
            handleError(request, response, "Failed to retrieve ticket: " + e.getMessage());
        }
    }

    private void showCreateTicketForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/tickets/create.jsp").forward(request, response);
    }

    private static int getLastNumber(String input) {
        String digits = input.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }


    private String generateTicketTypeWithNumber(List<Ticket>eventTickets, String ticketType,int eventId) {

        Map<String, Set<Integer>> map = new HashMap<>();
        for (Ticket ticket : eventTickets) {
            String type = ticket.getTicketType();
            int number = getLastNumber(type);
            String name = type.replaceAll("[0-9]", "").trim();
            name=name+ticket.getEvent().getEventId();
            map.computeIfAbsent(name, k -> new HashSet<>()).add(number);
        }

        Set<Integer> numbers = map.get((ticketType+eventId));
        if (numbers == null) {
            return ticketType + " 1";
        }
        int missingNumber = 1;
        while (numbers.contains(missingNumber)) {
            missingNumber++;
        }
        return ticketType + " " + missingNumber;
    }




    // Modified: Use a formatter to parse sale dates and redirect back to event view
    // (which shows tickets)
    private void createTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int eventId = Integer.parseInt(request.getParameter("eventId"));
            // Retrieve full event details
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }

            // Check if current user is the organizer of this event
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !currentUser.getRole().equals(User.UserRole.ORGANIZER) ||
                    currentUser.getUserId() != event.getOrganizer().getUserId()) {
                session.setAttribute("error", "You are not authorized to add tickets to this event");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            String ticketType = generateTicketTypeWithNumber(ticketService.getTicketsByEvent(eventId),request.getParameter("ticketType"),eventId);


            String priceStr = request.getParameter("price");
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0 || price > 9999.99) {
                    session.setAttribute("error", "Price must be between 0 and 9999.99.");
                    response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid price format.");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

// Validate quantityAvailable
            String quantityStr = request.getParameter("quantityAvailable");
            int quantityAvailable;
            try {
                quantityAvailable = Integer.parseInt(quantityStr);
                if (quantityAvailable < 0) {
                    session.setAttribute("error", "Quantity must be a positive number.");
                    response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid quantity format.");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            String description = request.getParameter("description");

            // Parse sale dates using a formatter matching the datetime-local input format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            String saleStartDateStr = request.getParameter("saleStartDate");
            String saleEndDateStr = request.getParameter("saleEndDate");

            LocalDateTime startDateTime = LocalDateTime.parse(request.getParameter("saleStartDate"));
            LocalDateTime endDateTime = LocalDateTime.parse(request.getParameter("saleEndDate"));

            if (endDateTime.isBefore(startDateTime)) {
                session.setAttribute("error", "End date cannot be earlier than start date");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            if (startDateTime.isAfter(event.getStartDateTime())) {
                session.setAttribute("error", "Ticket sale start date must be Before event start date");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            if (endDateTime.isAfter(event.getEndDateTime())) {
                session.setAttribute("error", "Ticket sale end date must be before event end date");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            LocalDateTime saleStartDate = null;
            LocalDateTime saleEndDate = null;
            if (saleStartDateStr != null && !saleStartDateStr.trim().isEmpty()) {
                saleStartDate = LocalDateTime.parse(saleStartDateStr, formatter);
            }
            if (saleEndDateStr != null && !saleEndDateStr.trim().isEmpty()) {
                saleEndDate = LocalDateTime.parse(saleEndDateStr, formatter);
            }



            Ticket ticket = ticketService.createTicket(event, ticketType, price, quantityAvailable, saleStartDate,
                    saleEndDate, description);
            // Redirect back to the event view (which lists tickets)
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (Exception e) {
            handleError(request, response, "Failed to create ticket: " + e.getMessage());
        }
    }

    private void updateTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Retrieve the ticket ID from the URL
            int ticketId = Integer.parseInt(request.getPathInfo().split("/")[1]);

            // Fetch the ticket to be updated
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return;
            }

            // Get the event associated with this ticket
            Event event = eventService.getEventById(ticket.getEvent().getEventId());
            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }


            // Check if current user is the organizer of this event
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null || !currentUser.getRole().equals(User.UserRole.ORGANIZER) ||
                    currentUser.getUserId() != event.getOrganizer().getUserId()) {
                session.setAttribute("error", "You are not authorized to update tickets for this event");
                response.sendRedirect(request.getContextPath() + "/events/" + event.getEventId());
                return;
            }

            // For GET requests, simply show the edit form
            if (request.getMethod().equals("GET")) {
                request.setAttribute("ticket", ticket);
                request.setAttribute("event", ticket.getEvent());
                request.getRequestDispatcher("/WEB-INF/views/tickets/edit.jsp").forward(request, response);
                return;
            }

            // For POST requests, update the ticket
//            String ticketType = request.getParameter("ticketType");
            String ticketType ;
            if(!request.getParameter("ticketType").equals(ticket.getTicketType().replaceAll("[0-9]", "").trim()))
            {
                 ticketType = generateTicketTypeWithNumber(ticketService.getTicketsByEvent(ticket.getEvent().getEventId()),request.getParameter("ticketType"),ticket.getEvent().getEventId());
            }
            else
            {
                ticketType = ticket.getTicketType();
            }



            String priceStr = request.getParameter("price");
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0 || price > 9999.99) {
                    session.setAttribute("error", "Price must be between 0 and 9999.99.");
                    response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid price format.");
                response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                return;
            }

// Validate quantityAvailable
            String quantityStr = request.getParameter("quantityAvailable");
            int quantityAvailable;
            try {
                quantityAvailable = Integer.parseInt(quantityStr);
                if (quantityAvailable < 0) {
                    session.setAttribute("error", "Quantity must be a positive number.");
                    response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid quantity format.");
                response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                return;
            }

            String description = request.getParameter("description");

            // Parse sale dates if provided
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            String saleStartDateStr = request.getParameter("saleStartDate");
            String saleEndDateStr = request.getParameter("saleEndDate");

            LocalDateTime startDateTime = LocalDateTime.parse(request.getParameter("saleStartDate"));
            LocalDateTime endDateTime = LocalDateTime.parse(request.getParameter("saleEndDate"));

            if (endDateTime.isBefore(startDateTime)) {

                session.setAttribute("error", "End date cannot be earlier than start date");                // Redirect back to the form page
                response.sendRedirect(request.getContextPath() + "/events/" + ticket.getEvent().getEventId());
                return;
            }
            int eventId = ticket.getEvent().getEventId();
            if (startDateTime.isAfter(event.getStartDateTime())) {
                session.setAttribute("error", "Ticket sale start date must be after event start date");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            if (endDateTime.isAfter(event.getEndDateTime())) {
                session.setAttribute("error", "Ticket sale end date must be before event end date");
                response.sendRedirect(request.getContextPath() + "/events/" + eventId);
                return;
            }

            if (saleStartDateStr != null && !saleStartDateStr.trim().isEmpty()) {
                ticket.setSaleStartDate(LocalDateTime.parse(saleStartDateStr, formatter));
            }

            if (saleEndDateStr != null && !saleEndDateStr.trim().isEmpty()) {
                ticket.setSaleEndDate(LocalDateTime.parse(saleEndDateStr, formatter));
            }

            // Update the ticket's properties
            ticket.setTicketType(ticketType);
            ticket.setPrice(price);
            ticket.setQuantityAvailable(quantityAvailable);
            ticket.setDescription(description);

            // Update the ticket in the database
            ticketService.updateTicket(ticket);

            // Add success message

            session.setAttribute("message", "Ticket updated successfully");

            // Redirect back to the event view

            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException | EventManagementException e) {
            handleError(request, response, "Failed to update ticket: " + e.getMessage());
        }
    }

    private void deleteTicket(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Retrieve the ticket ID from the URL
            int ticketId = Integer.parseInt(request.getPathInfo().split("/")[1]);

            // Fetch the ticket to get the event ID before deletion
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return;
            }

            // Store the event ID for redirection
            int eventId = ticket.getEvent().getEventId();

            // Delete the ticket
            ticketService.deleteTicket(ticketId);

            // Add success message
            HttpSession session = request.getSession();
            session.setAttribute("message", "Ticket deleted successfully");

            // Redirect back to the event view
            response.sendRedirect(request.getContextPath() + "/events/" + eventId);
        } catch (NumberFormatException | EventManagementException e) {
            handleError(request, response, "Failed to delete ticket: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
    }
}
