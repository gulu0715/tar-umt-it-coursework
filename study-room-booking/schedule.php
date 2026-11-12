<?php
require 'config.php';
require 'auth.php';
require 'helpers.php';

$date = $_GET['date'] ?? date('Y-m-d');
if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $date)) {
    $date = date('Y-m-d');
}

$rooms = $conn->query("SELECT * FROM rooms WHERE is_active = 1 ORDER BY floor, room_name")->fetch_all(MYSQLI_ASSOC);

$stmt = $conn->prepare('SELECT room_id, time_slot, purpose FROM bookings WHERE booking_date = ?');
$stmt->bind_param('s', $date);
$stmt->execute();
$bookings = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
$stmt->close();

$bookingsByRoomSlot = [];
foreach ($bookings as $b) {
    $bookingsByRoomSlot[(int)$b['room_id']][$b['time_slot']] = $b;
}

$timeSlots = campus_time_slots();

$pageTitle = 'Room Schedule';
$pageDescription = 'View study room availability by level, room, date and time slot.';
require 'partials/header.php';
?>
<div class="page-header">
<h1>Study Room Schedule</h1>
<p>Choose a date to view all active study rooms. Each row is a room and each column is an hourly time slot.</p>
</div>

<form method="get" class="filter-bar schedule-filter">
<label>Date <input type="date" name="date" value="<?= htmlspecialchars($date) ?>" min="<?= date('Y-m-d') ?>"></label>
<button type="submit">View Schedule</button>
</form>

<div class="schedule-legend">
<span class="schedule-key available">Available</span>
<span class="schedule-key booked">Booked</span>
<span class="schedule-key past">Past</span>
</div>

<div class="schedule-table-wrap">
<table class="schedule-table">
<thead>
<tr>
<th>Floor</th>
<th>Room</th>
<th>Capacity</th>
<?php foreach ($timeSlots as $slot): ?><th><?= htmlspecialchars($slot) ?></th><?php endforeach; ?>
</tr>
</thead>
<tbody>
<?php foreach ($rooms as $r): ?>
<?php
    $floor = 'Level ' . (int)$r['floor'];
    $roomBookings = $bookingsByRoomSlot[(int)$r['id']] ?? [];
?>
<tr>
<td><?= htmlspecialchars($floor) ?></td>
<td><strong><?= htmlspecialchars($r['room_name']) ?></strong></td>
<td><?= (int)$r['capacity'] ?></td>
<?php foreach ($timeSlots as $slot): ?>
<?php
    $booked = isset($roomBookings[$slot]);
    $past = $date === date('Y-m-d') && is_slot_in_past($date, $slot);
    $statusClass = $booked ? 'booked' : ($past ? 'past' : 'available');
?>
<td class="schedule-cell <?= $statusClass ?>">
<?php if ($booked): ?>
<span class="schedule-status">Booked</span>
<?php elseif ($past): ?>
<span class="schedule-status">Past</span>
<?php elseif (current_user_id()): ?>
<a href="create.php?room_id=<?= (int)$r['id'] ?>&booking_date=<?= urlencode($date) ?>&time_slot=<?= urlencode($slot) ?>" class="schedule-status schedule-link">Available</a>
<?php else: ?>
<a href="login.php" class="schedule-status schedule-link">Available</a>
<?php endif; ?>
</td>
<?php endforeach; ?>
</tr>
<?php endforeach; ?>
</tbody>
</table>
</div>

<?php require 'partials/footer.php'; ?>
