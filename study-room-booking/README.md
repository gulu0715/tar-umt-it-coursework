# Study Room Booking System

PHP + MySQL web application for booking campus study rooms. This version is intentionally scoped to study-room operations and is designed to run locally with XAMPP and later on Amazon EC2 + RDS behind an Application Load Balancer.

## Core features

- User registration and secure login
- Profile and password management
- 18 study rooms: 3 levels × 6 rooms
- Room search by name/location
- Floor, capacity and facilities information
- Date-based schedule matrix with hourly availability
- Book, edit and cancel your own room reservations
- Database-level duplicate booking protection
- CSRF protection on state-changing requests
- Database-backed sessions for ALB/Auto Scaling deployments
- Admin room, booking and user management
- Contact messages and room testimonials
- `healthz.php` for an AWS ALB target-group health check
- Optional S3 storage for shared room images in multi-EC2 deployments

## Booking rules

- Only active rooms can be booked.
- The booking date must be today or a future date.
- Only the fixed hourly slots defined in `helpers.php` are accepted by the server.
- A room can only have one booking for a given date and time slot. The database enforces this with `UNIQUE (room_id, booking_date, time_slot)`.
- A slot that has already started today cannot be booked.
- Users can edit/cancel their own bookings; admins can cancel any booking.

## Local setup (XAMPP)

1. Put this folder under `xampp/htdocs/StudyRoomBooking/`.
2. Start Apache and MySQL in XAMPP.
3. Open phpMyAdmin and import `schema.sql` for a new installation.
4. For an existing Modification 2 database, run `database_upgrade_v3.sql`.
5. Copy `.env.example` to `.env` and set the local DB values if required.
6. Open `http://localhost/StudyRoomBooking/`.
7. Register a normal user first. An administrator can then create another admin from the Admin panel.

## AWS target

Internet → Application Load Balancer → EC2 Auto Scaling Group → RDS MySQL

Room images can use S3 so multiple EC2 instances share the same uploads. Sessions are stored in RDS so a user session remains available when the ALB routes requests to different EC2 instances. Use `/healthz.php` for the ALB health check.

## AWS Management & Monitoring

For this coursework, **AWS Systems Manager (SSM) is not required** and is intentionally not part of this application architecture. The application deployment can be administered through the mechanisms required by the assignment, while monitoring can be handled separately with the AWS services selected for the project.

## Security notes

- Never commit `.env`.
- Set `SESSION_SECURE_COOKIE=true` when serving the application through HTTPS.
- Do not use a shared RDS master password in application code. Use a dedicated database user with only the permissions the application needs.
- Prefer an EC2 IAM role for S3 access where the assignment environment allows it.
