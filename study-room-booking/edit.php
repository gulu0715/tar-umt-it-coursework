<?php
require 'config.php';
require 'auth.php';
require 'helpers.php';
require_login();

$id = (int)($_GET['id'] ?? $_POST['id'] ?? 0);
$uid = current_user_id();

$stmt = $conn->prepare('SELECT b.*, r.room_name FROM bookings b JOIN rooms r ON r.id = b.room_id WHERE b.id = ? AND b.user_id = ?');
$stmt->bind_param('ii', $id, $uid);
$stmt->execute();
$booking = $stmt->get_result()->fetch_assoc();
$stmt->close();
if (!$booking) { http_response_code(404); die('Booking not found or you do not have permission to edit it.'); }

$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $room_id = (int)($_POST['room_id'] ?? 0);
    $booking_date = trim($_POST['booking_date'] ?? '');
    $time_slot = trim($_POST['time_slot'] ?? '');
    $purpose = trim($_POST['purpose'] ?? '');
    $booking = array_merge($booking, compact('room_id','booking_date','time_slot','purpose'));

    if ($room_id < 1 || $booking_date === '' || $time_slot === '' || $purpose === '') $error = 'All booking fields are required.';
    elseif (!valid_booking_date($booking_date)) $error = 'Please choose a valid booking date that is today or later.';
    elseif (!valid_time_slot($time_slot)) $error = 'Please choose a valid hourly time slot.';
    elseif (is_slot_in_past($booking_date, $time_slot)) $error = 'That time slot has already passed today. Please choose a later time slot.';
    elseif (mb_strlen($purpose) > 150) $error = 'Purpose must be 150 characters or fewer.';
    else {
        $stmt = $conn->prepare('SELECT id FROM rooms WHERE id = ? AND is_active = 1');
        $stmt->bind_param('i', $room_id); $stmt->execute(); $roomExists = $stmt->get_result()->fetch_assoc(); $stmt->close();
        if (!$roomExists) $error = 'The selected study room is unavailable.';
        else {
            $stmt = $conn->prepare('UPDATE bookings SET room_id=?, booking_date=?, time_slot=?, purpose=? WHERE id=? AND user_id=?');
            $stmt->bind_param('isssii', $room_id, $booking_date, $time_slot, $purpose, $id, $uid);
            if ($stmt->execute()) { $stmt->close(); set_flash('success','Booking updated successfully.'); header('Location: index.php'); exit; }
            $error = ($conn->errno === 1062) ? 'That room has just been booked for this date and time slot. Please choose another.' : 'Could not save the booking. Please try again.';
            $stmt->close();
        }
    }
}
$rooms = $conn->query('SELECT id, room_name, floor, capacity FROM rooms WHERE is_active = 1 OR id = ' . (int)$booking['room_id'] . ' ORDER BY floor, room_name');
$pageTitle='Edit Booking'; require 'partials/header.php';
?>
<div class="form-card"><h1>Edit Study Room Booking</h1>
<?php if ($error): ?><p class="alert alert-error" role="alert"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post">
<?= csrf_field() ?>
<input type="hidden" name="id" value="<?= (int)$booking['id'] ?>">
<label>Room<select name="room_id" id="room-select" required>
<?php $currentFloor=0; while($r=$rooms->fetch_assoc()): ?>
<?php if((int)$r['floor']!==$currentFloor): $currentFloor=(int)$r['floor']; ?><option value="" disabled>— Level <?= $currentFloor ?> —</option><?php endif; ?>
<option value="<?= (int)$r['id'] ?>" <?= (int)$r['id']===(int)$booking['room_id']?'selected':'' ?>><?= htmlspecialchars($r['room_name']) ?> (<?= (int)$r['capacity'] ?> people)</option>
<?php endwhile; ?></select></label>
<label>Booking Date <input type="date" name="booking_date" id="booking-date" value="<?= htmlspecialchars($booking['booking_date']) ?>" min="<?= date('Y-m-d') ?>" required></label>
<label>Time Slot<select name="time_slot" id="time-slot-select" required><option value="">-- Select a time slot --</option>
<?php foreach(campus_time_slots() as $slot): ?><option value="<?= htmlspecialchars($slot) ?>" <?= $booking['time_slot']===$slot?'selected':'' ?>><?= htmlspecialchars($slot) ?></option><?php endforeach; ?></select></label>
<p class="form-hint" id="slot-availability-hint"></p>
<label>Purpose <input type="text" name="purpose" maxlength="150" value="<?= htmlspecialchars($booking['purpose']) ?>" required></label>
<button type="submit">Update Booking</button></form>
<script>
(function(){var roomSelect=document.getElementById('room-select'),dateInput=document.getElementById('booking-date'),slotSelect=document.getElementById('time-slot-select'),hint=document.getElementById('slot-availability-hint'),excludeBookingId=<?= (int)$booking['id'] ?>,today='<?= date('Y-m-d') ?>',nowMinutes=<?= (int)date('H')*60+(int)date('i') ?>;function start(v){var p=v.split(' - ')[0].split(':');return parseInt(p[0],10)*60+parseInt(p[1],10)}function reset(){Array.prototype.forEach.call(slotSelect.options,function(o){if(o.dataset.originalText)o.textContent=o.dataset.originalText;o.disabled=false});hint.textContent=''}function disable(o,l){o.dataset.originalText=o.dataset.originalText||o.textContent;o.textContent=o.dataset.originalText+' ('+l+')';o.disabled=true;if(slotSelect.value===o.value)slotSelect.value=''}function refresh(){var rid=roomSelect.value,d=dateInput.value;reset();if(d===today)Array.prototype.forEach.call(slotSelect.options,function(o){if(o.value&&start(o.value)<nowMinutes)disable(o,'Past')});if(!rid||!d)return;fetch('slot_availability.php?room_id='+encodeURIComponent(rid)+'&booking_date='+encodeURIComponent(d)+'&exclude_booking_id='+excludeBookingId).then(function(r){if(!r.ok)throw new Error();return r.json()}).then(function(data){var full=data.full_slots||[];Array.prototype.forEach.call(slotSelect.options,function(o){if(full.indexOf(o.value)!==-1&&!o.disabled)disable(o,'Booked')});hint.textContent='Greyed-out slots are already booked or have already passed today.'}).catch(function(){hint.textContent='Live availability is temporarily unavailable. Final validation is enforced on the server.'})}roomSelect.addEventListener('change',refresh);dateInput.addEventListener('change',refresh);refresh()})();
</script>
<p><a class="btn btn-secondary btn-small" href="index.php">Back to home</a></p></div>
<?php require 'partials/footer.php'; ?>
