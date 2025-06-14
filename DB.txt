CREATE DATABASE IF NOT EXISTS event_management_system;
USE event_management_system;
-- Users table
CREATE TABLE users (
user_id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
first_name VARCHAR(50) NOT NULL,
last_name VARCHAR(50) NOT NULL,
role ENUM('ORGANIZER', 'ATTENDEE', 'VENUE_MANAGER') NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP
);
-- Venues table
CREATE TABLE venues (
venue_id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
address TEXT NOT NULL,
capacity INT NOT NULL,
contact_person VARCHAR(100),
contact_phone VARCHAR(20),
contact_email VARCHAR(100),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP
);
-- Events table
CREATE TABLE events (
event_id INT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(100) NOT NULL,
description TEXT,
start_datetime DATETIME NOT NULL,
end_datetime DATETIME NOT NULL,
venue_id INT,
organizer_id INT NOT NULL,
image_url VARCHAR(255),
status ENUM('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED') NOT NULL
DEFAULT 'DRAFT',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP,
FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE SET NULL,
FOREIGN KEY (organizer_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Tickets table
CREATE TABLE tickets (
ticket_id INT AUTO_INCREMENT PRIMARY KEY,
event_id INT NOT NULL,
ticket_type VARCHAR(50) NOT NULL,
price DECIMAL(10, 2) NOT NULL,
quantity_available INT NOT NULL,
sale_start_date DATETIME,
sale_end_date DATETIME,
description TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP,
FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);
-- Discount codes table
CREATE TABLE discount_codes (
code_id INT AUTO_INCREMENT PRIMARY KEY,
code VARCHAR(50) NOT NULL UNIQUE,
discount_type ENUM('PERCENTAGE', 'FIXED') NOT NULL,
discount_value DECIMAL(10, 2) NOT NULL,
event_id INT,
valid_from DATETIME,
valid_until DATETIME,
max_uses INT,
current_uses INT DEFAULT 0,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP,
FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);
-- Orders table
CREATE TABLE orders (
order_id INT AUTO_INCREMENT PRIMARY KEY,
attendee_id INT NOT NULL,
order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
total_amount DECIMAL(10, 2) NOT NULL,
status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT
'PENDING',
payment_method VARCHAR(50),
transaction_id VARCHAR(100),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
CURRENT_TIMESTAMP,
FOREIGN KEY (attendee_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Order items table
CREATE TABLE order_items (
item_id INT AUTO_INCREMENT PRIMARY KEY,
order_id INT NOT NULL,
ticket_id INT NOT NULL,
quantity INT NOT NULL,
unit_price DECIMAL(10, 2) NOT NULL,
discount_applied DECIMAL(10, 2) DEFAULT 0,
final_price DECIMAL(10, 2) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE
);
-- Attendee check-ins table
CREATE TABLE attendee_checkins (
checkin_id INT AUTO_INCREMENT PRIMARY KEY,
order_item_id INT NOT NULL,
checkin_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
checked_in_by INT,
notes TEXT,
FOREIGN KEY (order_item_id) REFERENCES order_items(item_id) ON DELETE
CASCADE,
FOREIGN KEY (checked_in_by) REFERENCES users(user_id) ON DELETE SET NULL
);
-- Feedback table
CREATE TABLE feedback (
feedback_id INT AUTO_INCREMENT PRIMARY KEY,
event_id INT NOT NULL,
attendee_id INT NOT NULL,
rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
comments TEXT,
submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
FOREIGN KEY (attendee_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Notifications table
CREATE TABLE notifications (
notification_id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT NOT NULL,
title VARCHAR(100) NOT NULL,
message TEXT NOT NULL,
is_read BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Event observers table (for Observer pattern implementation)
CREATE TABLE event_observers (
observer_id INT AUTO_INCREMENT PRIMARY KEY,
event_id INT NOT NULL,
user_id INT NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
UNIQUE KEY unique_observer (event_id, user_id)
);CREATE TABLE sold_tickets (
    sold_ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    event_id INT NOT NULL,
    order_item_id INT NOT NULL, 
    order_id INT NOT NULL, 
    quantity INT NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL, 
    sold_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);