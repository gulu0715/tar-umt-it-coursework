USE study_room_booking_db;

-- Convert the four rooms created by the first modification instead of duplicating them.
UPDATE rooms SET room_name='Room 101', location='Library, Level 1', capacity=6, image_url='/uploads/sample-room1.jpg' WHERE room_name='Study Room A';
UPDATE rooms SET room_name='Room 102', location='Library, Level 1', capacity=6, image_url='/uploads/sample-room2.jpg' WHERE room_name='Study Room B';
UPDATE rooms SET room_name='Room 103', location='Library, Level 1', capacity=6, image_url='/uploads/sample-room3.jpg' WHERE room_name='Study Room C';
UPDATE rooms SET room_name='Room 104', location='Library, Level 1', capacity=6, image_url='/uploads/sample-room1.jpg' WHERE room_name='Silent Study Pod';

-- Add the remaining rooms: six rooms on each of three levels.
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 105', 'Library, Level 1', 6, '/uploads/sample-room3.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 105');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 106', 'Library, Level 1', 6, '/uploads/sample-room1.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 106');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 201', 'Library, Level 2', 6, '/uploads/sample-room2.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 201');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 202', 'Library, Level 2', 6, '/uploads/sample-room3.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 202');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 203', 'Library, Level 2', 6, '/uploads/sample-room1.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 203');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 204', 'Library, Level 2', 6, '/uploads/sample-room2.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 204');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 205', 'Library, Level 2', 6, '/uploads/sample-room3.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 205');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 206', 'Library, Level 2', 6, '/uploads/sample-room1.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 206');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 301', 'Library, Level 3', 6, '/uploads/sample-room2.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 301');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 302', 'Library, Level 3', 6, '/uploads/sample-room3.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 302');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 303', 'Library, Level 3', 6, '/uploads/sample-room1.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 303');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 304', 'Library, Level 3', 6, '/uploads/sample-room2.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 304');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 305', 'Library, Level 3', 6, '/uploads/sample-room3.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 305');
INSERT INTO rooms (room_name, location, capacity, image_url) SELECT 'Room 306', 'Library, Level 3', 6, '/uploads/sample-room1.jpg' WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name='Room 306');
