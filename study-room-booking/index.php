<?php
require 'config.php';
require 'auth.php';
require 'helpers.php';

$search = trim($_GET['q'] ?? '');

if ($search !== '') {
    $stmt = $conn->prepare('SELECT * FROM rooms WHERE is_active = 1 AND (room_name LIKE ? OR location LIKE ?) ORDER BY floor, room_name');
    $likeSearch = '%' . $search . '%';
    $stmt->bind_param('ss', $likeSearch, $likeSearch);
    $stmt->execute();
    $rooms = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
} else {
    $rooms = $conn->query('SELECT * FROM rooms WHERE is_active = 1 ORDER BY floor, room_name')->fetch_all(MYSQLI_ASSOC);
}

$myBookings = [];
if ($uid = current_user_id()) {
    $stmt = $conn->prepare('
        SELECT b.id, r.room_name, r.location, b.booking_date, b.time_slot, b.purpose
        FROM bookings b
        JOIN rooms r ON r.id = b.room_id
        WHERE b.user_id = ?
        ORDER BY b.booking_date, b.time_slot
    ');
    $stmt->bind_param('i', $uid);
    $stmt->execute();
    $myBookings = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
}

$pageTitle = 'Study Room Booking System';
$pageDescription = 'Find and book campus study rooms online.';
require 'partials/header.php';
?>
<section class="hero">
<h1>Study Room Booking System</h1>
<p>Find a suitable study room, check its schedule, and reserve an available time slot.</p>
<?php if (current_user_id()): ?>
<a class="btn" href="create.php">Book a Study Room</a>
<?php else: ?>
<a class="btn" href="login.php">Login to Book</a>
<a class="btn btn-secondary" href="register.php">Create Account</a>
<?php endif; ?>
</section>

<section>
<h2>Study Rooms</h2>
<form method="get" class="filter-bar" id="room-filter-form">
<label>Search <input type="text" name="q" id="room-search" placeholder="Room name or location..." value="<?= htmlspecialchars($search) ?>" autocomplete="off"></label>
<button type="submit">Search</button>
<?php if ($search !== ''): ?><a class="btn btn-secondary" href="index.php">Clear</a><?php endif; ?>
</form>
<?php if (empty($rooms)): ?>
<div class="empty-state">
<div class="empty-state-icon">&#128269;</div>
<p>No study rooms match your search.</p>
<a class="btn btn-small btn-secondary" href="index.php">Clear filters</a>
</div>
<?php else: ?>
<div class="card-grid">
<?php foreach ($rooms as $r): ?>
<div class="card">
<img class="card-thumb" src="<?= htmlspecialchars(entity_image_url($r)) ?>" alt="<?= htmlspecialchars($r['room_name']) ?>" loading="lazy">
<h3><?= htmlspecialchars($r['room_name']) ?></h3>
<p>&#128205; <?= htmlspecialchars($r['location']) ?></p>
<p>Level <?= (int)$r['floor'] ?> &middot; Capacity: <?= (int)$r['capacity'] ?> people</p>
<p><strong>Facilities:</strong> <?= htmlspecialchars($r['facilities'] ?? '') ?></p>
<?php if (current_user_id()): ?>
<a class="btn" href="create.php?room_id=<?= (int)$r['id'] ?>">Book Now</a>
<?php else: ?>
<a class="btn" href="login.php">Login to Book</a>
<?php endif; ?>
<a class="btn btn-secondary btn-small" href="schedule.php?date=<?= date('Y-m-d') ?>">View Schedule</a>
</div>
<?php endforeach; ?>
</div>
<?php endif; ?>
</section>

<section id="my-bookings">
<h2>My Bookings</h2>
<?php if (!current_user_id()): ?>
<p><a href="login.php">Login</a> or <a href="register.php">register</a> to view and manage your bookings.</p>
<?php elseif (empty($myBookings)): ?>
<div class="empty-state">
<div class="empty-state-icon">&#128197;</div>
<p>You have no study room bookings yet.</p>
<a class="btn btn-small" href="create.php">Book a Study Room</a>
</div>
<?php else: ?>
<table>
<tr><th>Room</th><th>Date</th><th>Time Slot</th><th>Purpose</th><th>Actions</th></tr>
<?php foreach ($myBookings as $b): ?>
<tr>
<td><?= htmlspecialchars($b['room_name']) ?><br><small><?= htmlspecialchars($b['location']) ?></small></td>
<td><?= htmlspecialchars($b['booking_date']) ?></td>
<td><?= htmlspecialchars($b['time_slot']) ?></td>
<td><?= htmlspecialchars($b['purpose']) ?></td>
<td>
<a class="btn btn-secondary btn-small" href="edit.php?id=<?= (int)$b['id'] ?>">Edit</a>
<form action="delete.php" method="post" style="display:inline" onsubmit="return confirm('Cancel this booking?');">
<?= csrf_field() ?>
<input type="hidden" name="id" value="<?= (int)$b['id'] ?>">
<button type="submit" class="btn-small btn-danger">Cancel</button>
</form>
</td>
</tr>
<?php endforeach; ?>
</table>
<?php endif; ?>
</section>

<section>
<h2>How It Works</h2>
<div class="card-grid">
<div class="card"><h3>1. Choose a Room</h3><p>Compare room locations and capacities.</p></div>
<div class="card"><h3>2. Check the Schedule</h3><p>Choose a date and see occupied time slots.</p></div>
<div class="card"><h3>3. Make a Booking</h3><p>Reserve one available hourly slot for your study activity.</p></div>
</div>
</section>
<?php require 'partials/footer.php'; ?>
