<?php
require_once __DIR__ . '/../../helpers.php';
$flashSuccess = get_flash('success');
$flashError = get_flash('error');
$currentPage = basename($_SERVER['PHP_SELF']);
$resourcePages = ['rooms.php', 'room_create.php', 'room_edit.php'];
$activityPages = ['schedule.php', 'bookings.php'];
?>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script>
(function () {
    var saved = localStorage.getItem('theme');
    if (saved === 'dark' || saved === 'light') {
        document.documentElement.setAttribute('data-theme', saved);
    }
})();
</script>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><?= htmlspecialchars($pageTitle ?? 'Admin') ?></title>
<link rel="icon" type="image/png" href="../assets/favicon.png">
<link rel="stylesheet" href="../style.css?v=<?= @filemtime(__DIR__ . '/../../style.css') ?>">
</head>
<body>
<nav class="navbar">
<a class="brand" href="rooms.php"><img src="../assets/tarumt-logo.png" alt="TAR UMT" class="brand-logo">Admin &middot; Study Room Booking System</a>
<div class="nav-links">
<div class="nav-dropdown" data-nav-dropdown>
<button type="button" class="nav-dropdown-trigger <?= in_array($currentPage, $resourcePages) ? 'active' : '' ?>" aria-haspopup="true" aria-expanded="false">Resources &#9662;</button>
<div class="nav-dropdown-menu">
<a href="rooms.php" class="<?= $currentPage === 'rooms.php' ? 'active' : '' ?>">Rooms</a>
</div>
</div>
<div class="nav-dropdown" data-nav-dropdown>
<button type="button" class="nav-dropdown-trigger <?= in_array($currentPage, $activityPages) ? 'active' : '' ?>" aria-haspopup="true" aria-expanded="false">Activity &#9662;</button>
<div class="nav-dropdown-menu">
<a href="schedule.php" class="<?= $currentPage === 'schedule.php' ? 'active' : '' ?>">Schedule</a>
<a href="bookings.php" class="<?= $currentPage === 'bookings.php' ? 'active' : '' ?>">Room Bookings</a>
</div>
</div>
<a href="users.php" class="<?= $currentPage === 'users.php' ? 'active' : '' ?>">Users</a>
<a href="../logout.php">Logout</a>
<button id="theme-toggle" class="theme-toggle" type="button" aria-label="Toggle dark mode">&#9728;</button>
</div>
</nav>
<main class="container">
<?php if ($flashSuccess): ?><p class="alert alert-success" role="status"><?= htmlspecialchars($flashSuccess) ?></p><?php endif; ?>
<?php if ($flashError): ?><p class="alert alert-error" role="alert"><?= htmlspecialchars($flashError) ?></p><?php endif; ?>
