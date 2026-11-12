<?php
require 'config.php'; require 'auth.php'; require 'helpers.php';
$pageTitle='About Us'; $pageDescription='About the Study Room Booking System.'; require 'partials/header.php';
?>
<div class="page-header"><h1>About Study Room Booking System</h1><p>A simple campus platform for finding and reserving study rooms.</p></div>
<section><div class="card-grid">
<div class="card"><h3>18 Study Rooms</h3><p>Three levels with six rooms on each level.</p></div>
<div class="card"><h3>Hourly Booking</h3><p>Choose an available one-hour slot for focused study, group discussion or assignment work.</p></div>
<div class="card"><h3>Central Schedule</h3><p>Check room availability by date and time before making a reservation.</p></div>
</div></section>
<section><h2>How the System Works</h2><div class="card-grid">
<div class="card"><h3>1. Find a Room</h3><p>Compare floor, capacity and facilities.</p></div>
<div class="card"><h3>2. Check Availability</h3><p>Use the schedule matrix to identify a free hourly slot.</p></div>
<div class="card"><h3>3. Reserve and Manage</h3><p>Book the room, then edit or cancel your own upcoming reservation from My Bookings.</p></div>
</div></section>
<?php require 'partials/footer.php'; ?>
