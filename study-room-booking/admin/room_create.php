<?php
require '../config.php';
require '../auth.php';
require '../helpers.php';
require_admin();

$error = '';
$uploadDir = __DIR__ . '/../uploads';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $room_name = trim($_POST['room_name']);
    $location  = trim($_POST['location']);
    $capacity  = (int)($_POST['capacity'] ?? 0);
    $floor     = (int)($_POST['floor'] ?? 0);
    $facilities = trim($_POST['facilities'] ?? '');
    verify_csrf();

    [$image_url, $uploadError] = handle_image_upload($_FILES['image'] ?? null, $uploadDir, 'room');

    if ($room_name === '' || mb_strlen($room_name) > 100 || $location === '' || mb_strlen($location) > 100 || $capacity < 1 || $floor < 1 || $floor > 3) {
        $error = 'Room name, floor and location are required; capacity must be at least 1 and floor must be 1-3.';
    } elseif ($uploadError) {
        $error = $uploadError;
    } else {
        $stmt = $conn->prepare('INSERT INTO rooms (room_name, floor, location, capacity, facilities, image_url) VALUES (?, ?, ?, ?, ?, ?)');
        $stmt->bind_param('sisiss', $room_name, $floor, $location, $capacity, $facilities, $image_url);
        if ($stmt->execute()) {
            set_flash('success', 'Study room added successfully.');
        } else {
            $error = ($conn->errno === 1062) ? 'A room with this name already exists.' : 'Could not add the study room.';
        }
        $stmt->close();
        if ($error === '') { header('Location: rooms.php'); exit; }
        exit;
    }
}

$pageTitle = 'Add Room';
require 'partials/header.php';
?>
<div class="form-card">
<h1>Add Room</h1>
<?php if ($error): ?><p class="alert alert-error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post" enctype="multipart/form-data">
<?= csrf_field() ?>
<label>Room Name <input type="text" name="room_name" required></label>
<label>Floor <select name="floor" required><option value="">-- Select Level --</option><option value="1">Level 1</option><option value="2">Level 2</option><option value="3">Level 3</option></select></label>
<label>Location <input type="text" name="location" required></label>
<label>Capacity <input type="number" name="capacity" min="1" max="99" required></label>
<label>Facilities <textarea name="facilities" rows="3" maxlength="500" placeholder="e.g. Whiteboard, Projector, Power Sockets"><?= htmlspecialchars($_POST['facilities'] ?? '') ?></textarea></label>
<label>Photo <input type="file" name="image" accept="image/jpeg,image/png,image/gif,image/webp"></label>
<button type="submit">Add Room</button>
</form>
<p><a class="btn btn-secondary btn-small" href="rooms.php">Back to rooms</a></p>
</div>
<?php require 'partials/footer.php'; ?>
