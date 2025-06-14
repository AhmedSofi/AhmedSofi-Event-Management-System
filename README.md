# Event Management System

A Java Servlet-based web application for managing events, ticketing, and real-time attendee tracking. The system streamlines the process of creating, managing, and analyzing events for organizers, while providing a seamless experience for attendees.

## Features

### Event Management
- Create, update, and delete events.
- Add details like name, date, time, location, description, images, and videos.
- Customizable registration forms to collect attendee information.

### Ticketing & Registration
- Support for both free and paid tickets.
- Ticket types: General Admission, VIP, Early Bird, etc.
- Automated ticket purchasing, cancellations, and refunds.
- Dynamic pricing based on demand.
- Discount code management.

### Real-Time Attendee Management
- Live attendee check-ins and attendance tracking.
- Historical data storage for post-event analysis.
- Real-time updates using Observer design pattern.

### Venue Management
- Manage venue details, capacity, availability, and conflict resolution.
- Booking management for multiple venues and overlapping events.

### Feedback & Analysis
- Collect attendee feedback through ratings and comments.
- Generate post-event reports with aggregated feedback.

### Automated Notifications
- Email reminders, updates, and promotional messages.
- Notifications triggered by schedule changes or approaching events.

### Data Security & Compliance
- Secure password hashing.
- Follows best practices for data handling and user privacy.

## Technical Overview

### Tech Stack
- **Backend:** Java Servlets
- **Design Principles:** Object-Oriented Design, SOLID Principles
- **Design Patterns:**
  - Singleton: For services like database access, ticketing, and notifications.
  - Observer: For notifying attendees of schedule or event changes.

### Key Entities
- `Event`
- `User` (Organizer, Attendee)
- `Ticket`
- `Venue`
- `NotificationService`
- `Feedback`

## Installation

1. Setup your database and update the connection parameters in the project.

2. Build the project (WAR file) and deploy to Apache Tomcat.

## Running Using Docker

Make sure you have Docker installed and running
then type the following command from the application root directory
```
docker-compose down --volumes
docker-compose up --build
```
the application can be accessed from this URL [http://localhost:8080/EventManagementSystemV2/](http://localhost:8080/EventManagementSystemV2/)

## Running on Apache Tomcat

1. Download and extract [Apache Tomcat](https://tomcat.apache.org/download-90.cgi).

2. Place the generated `.war` file inside the `webapps/` directory of Tomcat.

3. Start Tomcat using the `startup.sh` (Linux/macOS) or `startup.bat` (Windows) script in the `bin/` folder.

4. Access your web application at `http://localhost:8080/your-app-name`.

## Usage

- Organizers can:
  - Create and manage events
  - Track attendees
  - Manage ticket sales
  - Analyze event feedback

- Attendees can:
  - Browse and register for events
  - Receive email notifications
  - Submit feedback post-event


