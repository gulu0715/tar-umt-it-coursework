-- Study Room Booking System - Modification 3 migration
-- Run this AFTER database_upgrade_v2.sql on an existing v2 database.
USE study_room_booking_db;

-- 1) Drop the existing foreign keys before changing integer signedness.
ALTER TABLE bookings DROP FOREIGN KEY bookings_ibfk_1;
ALTER TABLE bookings DROP FOREIGN KEY bookings_ibfk_2;
ALTER TABLE testimonials DROP FOREIGN KEY testimonials_ibfk_1;
ALTER TABLE testimonials DROP FOREIGN KEY testimonials_ibfk_2;

-- 2) Add explicit room metadata used by the new UI and admin panel.
ALTER TABLE rooms ADD COLUMN floor TINYINT UNSIGNED NOT NULL DEFAULT 1 AFTER room_name;
ALTER TABLE rooms ADD COLUMN facilities TEXT NULL AFTER capacity;
ALTER TABLE rooms ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER image_url;
ALTER TABLE rooms MODIFY id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE rooms ADD UNIQUE KEY uq_rooms_name (room_name);

UPDATE rooms SET floor = CASE
  WHEN room_name LIKE 'Room 1%%' THEN 1
  WHEN room_name LIKE 'Room 2%%' THEN 2
  WHEN room_name LIKE 'Room 3%%' THEN 3
  ELSE 1 END;
UPDATE rooms SET facilities = 'Whiteboard, Power Sockets, Wi-Fi' WHERE facilities IS NULL;

-- 3) Remove library-only tables.
DROP TABLE IF EXISTS equipment_loans;
DROP TABLE IF EXISTS book_loans;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS books;

-- 4) Align key columns for strict FK compatibility.
ALTER TABLE users MODIFY id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE bookings MODIFY id INT UNSIGNED NOT NULL AUTO_INCREMENT, MODIFY user_id INT UNSIGNED NOT NULL, MODIFY room_id INT UNSIGNED NOT NULL;
ALTER TABLE testimonials MODIFY id INT UNSIGNED NOT NULL AUTO_INCREMENT, MODIFY user_id INT UNSIGNED NOT NULL, MODIFY room_id INT UNSIGNED NOT NULL;
ALTER TABLE sessions MODIFY last_activity INT UNSIGNED NOT NULL;

-- 5) Recreate the foreign keys with safe delete behaviour.
ALTER TABLE bookings ADD CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE bookings ADD CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT;
ALTER TABLE testimonials ADD CONSTRAINT fk_testimonials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE testimonials ADD CONSTRAINT fk_testimonials_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT;

-- 6) Useful lookup indexes.
CREATE INDEX idx_bookings_date_room ON bookings (booking_date, room_id);
CREATE INDEX idx_bookings_user_date ON bookings (user_id, booking_date);
CREATE INDEX idx_rooms_floor_active ON rooms (floor, is_active);
CREATE INDEX idx_testimonials_created ON testimonials (created_at);
CREATE INDEX idx_contact_created ON contact_messages (created_at);
