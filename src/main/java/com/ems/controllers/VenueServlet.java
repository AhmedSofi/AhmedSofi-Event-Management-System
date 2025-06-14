package com.ems.controllers;

import com.ems.models.User;
import com.ems.models.Venue;
import com.ems.services.VenueService;
import com.ems.exceptions.EventManagementException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "VenueServlet", urlPatterns = {"/venues", "/venues/add", "/venues/edit", "/venues/delete"})

public class VenueServlet extends HttpServlet {
    private VenueService venueService;

    @Override
    public void init() {
        this.venueService = new VenueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        if ("/venues/add".equals(path)) {
            if (user == null || user.getRole() != User.UserRole.ORGANIZER) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.getRequestDispatcher("/WEB-INF/views/venues/add.jsp").forward(request, response);
        } else if ("/venues".equals(path)) {
            try {
                request.setAttribute("venues", venueService.getAllVenues());
                request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
            } catch (EventManagementException e) {
                e.printStackTrace();
                request.setAttribute("error", "Could not load venues: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } else if ("/venues/delete".equals(path)) {
            int venueId = Integer.parseInt(request.getParameter("id"));
            venueService.deleteVenue(venueId);
            response.sendRedirect(request.getContextPath() + "/venues");
        } else  if ("/venues/edit".equals(path)) {
            int venueId = Integer.parseInt(request.getParameter("id"));
            Venue venue = venueService.getVenueById(venueId);
            request.setAttribute("venue", venue);
            request.getRequestDispatcher("/WEB-INF/views/venues/edit.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");

        if (user == null || user.getRole() != User.UserRole.ORGANIZER) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath(); // Determines if it's add or edit

        try {
            String venueIdStr = request.getParameter("venueId"); // match input name in form
            int venueId = (venueIdStr != null && !venueIdStr.isEmpty()) ? Integer.parseInt(venueIdStr) : 0;

            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String contactPerson = request.getParameter("contactPerson");
            String contactPhone = request.getParameter("contactPhone");
            String contactEmail = request.getParameter("contactEmail");
            String capacityStr = request.getParameter("capacity");

                // Validate all fields
                if (name == null || name.isEmpty() || address == null || address.isEmpty()) {
                    request.setAttribute("error", "Invalid input. Name and address must not be empty.");
                    request.setAttribute("venues", venueService.getAllVenues());
                    request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
                    return;
                }

                int capacity = Integer.parseInt(capacityStr);
                if (capacity < 1 || capacity > 100000) {
                    request.setAttribute("error", "Invalid capacity. Must be between 1 and 100,000.");
                    request.setAttribute("venues", venueService.getAllVenues());
                    request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
                    return;
                }

                if (contactPerson == null || contactPerson.isEmpty()) {
                    request.setAttribute("error", "must be not empty.");
                    request.setAttribute("venues", venueService.getAllVenues());
                    request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
                    return;
                }

                if (!contactPhone.matches("^[+]?[0-9]{10,15}$")) {
                    request.setAttribute("error", "Invalid phone format.");
                    request.setAttribute("venues", venueService.getAllVenues());
                    request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
                    return;
                }

                if (!contactEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    request.setAttribute("error", "Invalid email format.");
                    request.setAttribute("venues", venueService.getAllVenues());
                    request.getRequestDispatcher("/WEB-INF/views/venues/list.jsp").forward(request, response);
                    return;
                }


                Venue venue = new Venue(
                    venueId,
                    name,
                    address,
                    capacity
            );
            venue.setContactPerson(contactPerson);
            venue.setContactPhone(contactPhone);
            venue.setContactEmail(contactEmail);

            if ("/venues/edit".equals(path)) {
                venueService.updateVenue(venue);
            } else {
                venueService.createVenue(venue);
            }

            response.sendRedirect(request.getContextPath() + "/venues");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to process venue: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errors/500.jsp").forward(request, response);
        }
    }
}
