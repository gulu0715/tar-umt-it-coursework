<?php
require '../config.php';
require '../auth.php';
require '../helpers.php';
require_admin();

$id = (int)($_GET['id'] ?? $_POST['id'] ?? 0);
$error = '';
$uploadDir = __DIR__ . '/../uploads';

$stmt = $conn->prepare('SELECT * FROM rooms WHERE id = ?');
$stmt->bind_param('i', $id);
$stmt->execute();
$room = $stmt->get_result()->fetch_assoc();
$stmt->close();

if (!$room) {
    die('Room not found.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verify_csrf();
    $room_name = trim($_POST['room_name']);
    $location  = trim($_POST['location']);
    $capacity  = (int)($_POST['capacity'] ?? 0);
    $floor = (int)($_POST['floor'] ?? 0);
    $facilities = trim($_POST['facilities'] ?? '');
    verify_csrf();

    [$newImageUrl, $uploadError] = handle_image_upload($_FILES['image'] ?? null, $uploadDir, 'room');
    $image_url = $newImageUrl ?? $room['image_url'];

    if ($room_name === '' || mb_strlen($room_name) > 100 || $location === '' || mb_strlen($location) > 100 || $capacity < 1 || $floor < 1 || $floor > 3) {
        $error = 'Room name, floor and location are required; capacity must be at least 1 and floor must be 1-3.';
        $room = array_merge($room, compact('room_name', 'floor', 'location', 'capacity', 'facilities', 'image_url'));
    } elseif ($uploadError) {
        $error = $uploadError;
    } else {
        if ($newImageUrl) {
            delete_image_file($room['image_url'], $uploadDir);
        }

        $stmt = $conn->prepare('UPDATE rooms SET room_name=?, floor=?, location=?, capacity=?, facilities=?, image_url=? WHERE id=?');
        $stmt->bind_param('sisissi', $room_name, $floor, $location, $capacity, $facilities, $image_url, $id);
        if ($stmt->execute()) {
            set_flash('success', 'Study room updated successfully.');
        } else {
            $error = ($conn->errno === 1062) ? 'A room with this name already exists.' : 'Could not update the study room.';
        }
        $stmt->close();
        if ($error === '') { header('Location: rooms.php'); exit; }
        exit;
    }
}

$pageTitle = 'Edit Room';
require 'partials/header.php';
?>
<div class="form-card">
<h1>Edit Room</h1>
<?php if ($error): ?><p class="alert alert-error"><?= htmlspecialchars($error) ?></p><?php endif; ?>
<form method="post" enctype="multipart/form-data">
<?= csrf_field() ?>
<input type="hidden" name="id" value="<?= (int)$room['id'] ?>">
<label>Room Name <input type="text" name="room_name" value="<?= htmlspecialchars($room['room_name']) ?>" required></label>
<label>Floor <select name="floor" required><?php for($f=1;$f<=3;$f++): ?><option value="<?= $f ?>" <?= (int)$room['floor'] === $f ? 'selected' : '' ?>>Level <?= $f ?></option><?php endfor; ?></select></label>
<label>Location <input type="text" name="location" value="<?= htmlspecialchars($room['location']) ?>" required></label>
<label>Capacity <input type="number" name="capacity" min="1" max="99" value="<?= (int)$room['capacity'] ?>" required></label>
<label>Facilities <textarea name="facilities" rows="3" maxlength="500"><?= htmlspecialchars($room['facilities'] ?? '') ?></textarea></label>
<label>Current Photo
<img class="table-thumb" style="width:96px;height:96px;" src="<?= htmlspecialchars(entity_image_url($room)) ?>" alt="<?= htmlspecialchars($room['room_name']) ?>">
</label>
<label>Replace Photo <input type="file" name="image" accept="image/jpeg,image/png,image/gif,image/webp"></label>
<button type="submit">Update Room</button>
</form>
<p><a class="btn btn-secondary btn-small" href="rooms.php">Back to rooms</a></p>
</div>
<?php require 'partials/footer.php'; ?>
