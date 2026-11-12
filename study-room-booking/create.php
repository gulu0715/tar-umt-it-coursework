<?php
require 'config.php';
require 'auth.php';
require 'helpers.php';
require_login();

$error = '';
$selectedRoom = (int)($_GET['room_id'] ?? $_POST['room_id'] ?? 0);
$selectedDate = $_GET['booking_date'] ?? $_POST['booking_date'] ?? date('Y-m-d');
$selectedSlot = $_GET['time_slot'] ?? $_POST['time_slot'] ?? '';
$purpose = trim($_POST['purpose'] ?? '');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $selectedRoom = (int)($_POST['room_id'] ?? 0);
    $selectedDate = trim($_POST['booking_date'] ?? '');
    $selectedSlot = trim($_POST['time_slot'] ?? '');
    $purpose = trim($_POST['purpose'] ?? '');

    if ($selectedRoom < 1 || $selectedDate === '' || $selectedSlot === '' || $purpose === '') {
        $error = 'All booking fields are required.';
    } elseif (!valid_booking_date($selectedDate)) {
        $error = 'Please choose a valid booking date that is today or later.';
    } elseif (!valid_time_slot($selectedSlot)) {
        $error = 'Please choose a valid hourly time slot.';
    } elseif (is_slot_in_past($selectedDate, $selectedSlot)) {
        $error = 'That time slot has already passed today. Please choose a later time slot.';
    } elseif (mb_strlen($purpose) > 150) {
        $error = 'Purpose must be 150 characters or fewer.';
    } else {
        $stmt = $conn->prepare('SELECT id FROM rooms WHERE id = ? AND is_active = 1');
        $stmt->bind_param('i', $selectedRoom);
        $stmt->execute();
        $roomExists = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        if (!$roomExists) {
            $error = 'The selected study room is unavailable.';
        } else {
            $uid = current_user_id();
            $stmt = $conn->prepare('INSERT INTO bookings (user_id, room_id, booking_date, time_slot, purpose) VALUES (?, ?, ?, ?, ?)');
            $stmt->bind_param('iisss', $uid, $selectedRoom, $selectedDate, $selectedSlot, $purpose);
            if ($stmt->execute()) {
                $stmt->close();
                set_flash('success', 'Booking successful. Your study room has been reserved.');
                header('Location: index.php');
                exit;
            }
            $isDuplicate = $conn->errno === 1062;
            $error = $isDuplicate
                ? 'That room has just been booked for this date and time slot. Please choose another slot.'
                : 'Could not save the booking. Please try again.';
            $stmt->close();
        }
    }
}

$rooms = $conn->query('SELECT id, room_name, floor, location, capacity FROM rooms WHERE is_active = 1 ORDER BY floor, room_name');

$pageTitle = 'Book a Study Room';
require 'partials/header.php';
?>
<div class="form-card">
<h1>Book a Study Room</h1>
<p>Select a room, date and available hourly time slot.</p>
<?php if ($error): ?><p class="alert alert-error" role="alert"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post">
<?= csrf_field() ?>
<label>Room
<select name="room_id" id="room-select" required>
<option value="">-- Select a room --</option>
<?php $currentFloor = 0; while ($r = $rooms->fetch_assoc()): ?>
<?php if ((int)$r['floor'] !== $currentFloor): $currentFloor = (int)$r['floor']; ?><option value="" disabled>— Level <?= $currentFloor ?> —</option><?php endif; ?>
<option value="<?= (int)$r['id'] ?>" <?= (int)$r['id'] === $selectedRoom ? 'selected' : '' ?>><?= htmlspecialchars($r['room_name']) ?> (<?= (int)$r['capacity'] ?> people)</option>
<?php endwhile; ?>
</select>
</label>
<label>Booking Date <input type="date" name="booking_date" id="booking-date" value="<?= htmlspecialchars($selectedDate) ?>" min="<?= date('Y-m-d') ?>" required></label>
<label>Time Slot
<select name="time_slot" id="time-slot-select" required>
<option value="">-- Select a time slot --</option>
<?php foreach (campus_time_slots() as $slot): ?>
<option value="<?= htmlspecialchars($slot) ?>" <?= $selectedSlot === $slot ? 'selected' : '' ?>><?= htmlspecialchars($slot) ?></option>
<?php endforeach; ?>
</select>
</label>
<p class="form-hint" id="slot-availability-hint"></p>
<label>Purpose <input type="text" name="purpose" maxlength="150" placeholder="e.g. Individual study, group discussion, assignment" value="<?= htmlspecialchars($purpose) ?>" required></label>
<button type="submit">Confirm Booking</button>
</form>
<script>
(function () {
    var roomSelect = document.getElementById('room-select');
    var dateInput = document.getElementById('booking-date');
    var slotSelect = document.getElementById('time-slot-select');
    var hint = document.getElementById('slot-availability-hint');
    var today = '<?= date('Y-m-d') ?>';
    var nowMinutes = <?= (int)date('H') * 60 + (int)date('i') ?>;
    function slotStartMinutes(value) { var parts = value.split(' - ')[0].split(':'); return parseInt(parts[0],10)*60 + parseInt(parts[1],10); }
    function resetOptions() { Array.prototype.forEach.call(slotSelect.options, function (opt) { if (opt.dataset.originalText) opt.textContent = opt.dataset.originalText; opt.disabled = false; }); hint.textContent = ''; }
    function markDisabled(opt, label) { opt.dataset.originalText = opt.dataset.originalText || opt.textContent; opt.textContent = opt.dataset.originalText + ' (' + label + ')'; opt.disabled = true; if (slotSelect.value === opt.value) slotSelect.value = ''; }
    function refreshSlots() {
        var roomId = roomSelect.value, date = dateInput.value; resetOptions();
        if (date === today) Array.prototype.forEach.call(slotSelect.options, function(opt){ if(opt.value && slotStartMinutes(opt.value) < nowMinutes) markDisabled(opt,'Past'); });
        if (!roomId || !date) return;
        fetch('slot_availability.php?room_id=' + encodeURIComponent(roomId) + '&booking_date=' + encodeURIComponent(date))
            .then(function(res){ if(!res.ok) throw new Error('availability'); return res.json(); })
            .then(function(data){ var fullSlots=data.full_slots||[]; Array.prototype.forEach.call(slotSelect.options,function(opt){ if(fullSlots.indexOf(opt.value)!==-1 && !opt.disabled) markDisabled(opt,'Booked'); }); hint.textContent='Greyed-out slots are already booked or have already passed today.'; })
            .catch(function(){ hint.textContent='Live availability is temporarily unavailable. Final booking validation is still enforced on the server.'; });
    }
    roomSelect.addEventListener('change', refreshSlots); dateInput.addEventListener('change', refreshSlots); refreshSlots();
})();
</script>
<p><a class="btn btn-secondary btn-small" href="index.php">Back to home</a></p>
</div>
<?php require 'partials/footer.php'; ?>
