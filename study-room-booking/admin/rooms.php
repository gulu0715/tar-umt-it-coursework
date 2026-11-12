<?php
require '../config.php';
require '../auth.php';
require '../helpers.php';
require_admin();

$rooms = $conn->query('SELECT * FROM rooms ORDER BY floor, room_name');

$pageTitle = 'Manage Rooms';
require 'partials/header.php';
?>
<h1>Rooms</h1>
<p><a class="btn btn-small" href="room_create.php">+ Add Room</a></p>
<table>
<tr><th>Photo</th><th>Room</th><th>Floor</th><th>Location</th><th>Capacity</th><th>Facilities</th><th>Status</th><th>Actions</th></tr>
<?php while ($r = $rooms->fetch_assoc()): ?>
<tr>
<td><img class="table-thumb" src="<?= htmlspecialchars(entity_image_url($r)) ?>" alt="<?= htmlspecialchars($r['room_name']) ?>" loading="lazy"></td>
<td><?= htmlspecialchars($r['room_name']) ?></td>
<td>Level <?= (int)$r['floor'] ?></td>
<td><?= htmlspecialchars($r['location']) ?></td>
<td><?= (int)$r['capacity'] ?></td>
<td><?= htmlspecialchars($r['facilities'] ?? '') ?></td>
<td><?= $r['is_active'] ? '<span class="badge badge-neutral">Active</span>' : '<span class="badge badge-critical">Inactive</span>' ?></td>
<td>
<a class="btn btn-secondary btn-small" href="room_edit.php?id=<?= (int)$r['id'] ?>">Edit</a>
<form action="room_delete.php" method="post" style="display:inline" onsubmit="return confirm('Delete this room? Any existing bookings for it must be removed first.');">
<?= csrf_field() ?>
<input type="hidden" name="id" value="<?= (int)$r['id'] ?>">
<button type="submit" class="btn-small btn-danger">Delete</button>
</form>
</td>
</tr>
<?php endwhile; ?>
</table>
<?php require 'partials/footer.php'; ?>
