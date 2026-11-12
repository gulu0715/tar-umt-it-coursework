CREATE DATABASE IF NOT EXISTS study_room_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE study_room_booking_db;

CREATE TABLE users (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  id_number VARCHAR(30) NULL,
  faculty VARCHAR(150) NULL,
  date_of_birth DATE NULL,
  is_admin TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Demo admin for local coursework only: admin@example.com / Admin@12345
-- Change or remove this account before real deployment.
INSERT INTO users (name, email, password_hash, is_admin) VALUES
('Admin', 'admin@example.com', '$2y$12$MWyqq4Cmeb/H789CWIRUTO56d0HNMx8lMjsIOQL1k918Yki/icbyG', 1);


CREATE TABLE rooms (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  room_name VARCHAR(100) NOT NULL UNIQUE,
  floor TINYINT UNSIGNED NOT NULL,
  location VARCHAR(100) NOT NULL,
  capacity SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  facilities TEXT NULL,
  image_url VARCHAR(500) NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_rooms_floor_active (floor, is_active)
) ENGINE=InnoDB;

INSERT INTO rooms (room_name, floor, location, capacity, facilities, image_url) VALUES
('Room 101',1,'Library, Level 1',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room1.jpg'),
('Room 102',1,'Library, Level 1',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room2.jpg'),
('Room 103',1,'Library, Level 1',6,'Power Sockets, Wi-Fi, Air Conditioning','/uploads/sample-room3.jpg'),
('Room 104',1,'Library, Level 1',6,'Whiteboard, Projector, Power Sockets','/uploads/sample-room1.jpg'),
('Room 105',1,'Library, Level 1',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room2.jpg'),
('Room 106',1,'Library, Level 1',6,'Power Sockets, Wi-Fi, Air Conditioning','/uploads/sample-room3.jpg'),
('Room 201',2,'Library, Level 2',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room1.jpg'),
('Room 202',2,'Library, Level 2',6,'Whiteboard, Projector, Power Sockets','/uploads/sample-room2.jpg'),
('Room 203',2,'Library, Level 2',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room3.jpg'),
('Room 204',2,'Library, Level 2',6,'Power Sockets, Wi-Fi, Air Conditioning','/uploads/sample-room1.jpg'),
('Room 205',2,'Library, Level 2',6,'Whiteboard, Projector, Wi-Fi','/uploads/sample-room2.jpg'),
('Room 206',2,'Library, Level 2',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room3.jpg'),
('Room 301',3,'Library, Level 3',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room1.jpg'),
('Room 302',3,'Library, Level 3',6,'Whiteboard, Projector, Power Sockets','/uploads/sample-room2.jpg'),
('Room 303',3,'Library, Level 3',6,'Power Sockets, Wi-Fi, Air Conditioning','/uploads/sample-room3.jpg'),
('Room 304',3,'Library, Level 3',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room1.jpg'),
('Room 305',3,'Library, Level 3',6,'Whiteboard, Projector, Wi-Fi','/uploads/sample-room2.jpg'),
('Room 306',3,'Library, Level 3',6,'Whiteboard, Power Sockets, Wi-Fi','/uploads/sample-room3.jpg');

CREATE TABLE bookings (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  room_id INT UNSIGNED NOT NULL,
  booking_date DATE NOT NULL,
  time_slot VARCHAR(20) NOT NULL,
  purpose VARCHAR(150) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
  UNIQUE KEY unique_slot (room_id, booking_date, time_slot),
  INDEX idx_bookings_date_room (booking_date, room_id),
  INDEX idx_bookings_user_date (user_id, booking_date)
) ENGINE=InnoDB;

CREATE TABLE testimonials (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  room_id INT UNSIGNED NOT NULL,
  comment TEXT NOT NULL,
  rating TINYINT UNSIGNED NOT NULL DEFAULT 5,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
  INDEX idx_testimonials_created (created_at)
) ENGINE=InnoDB;

CREATE TABLE contact_messages (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  subject VARCHAR(150) NOT NULL,
  message TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_contact_created (created_at)
) ENGINE=InnoDB;

CREATE TABLE sessions (
  id VARCHAR(128) PRIMARY KEY,
  data MEDIUMTEXT NOT NULL,
  last_activity INT UNSIGNED NOT NULL,
  INDEX idx_last_activity (last_activity)
) ENGINE=InnoDB;
