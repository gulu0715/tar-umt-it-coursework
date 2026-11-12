<?php
require '../config.php'; require '../auth.php'; require '../helpers.php'; require_admin();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('Method Not Allowed'); }
verify_csrf();
$id=(int)($_POST['id']??0); $uploadDir=__DIR__.'/../uploads';
$stmt=$conn->prepare('SELECT image_url FROM rooms WHERE id=?'); $stmt->bind_param('i',$id); $stmt->execute(); $room=$stmt->get_result()->fetch_assoc(); $stmt->close();
$stmt=$conn->prepare('DELETE FROM rooms WHERE id=? AND is_active=1'); $stmt->bind_param('i',$id);
if($stmt->execute() && $stmt->affected_rows>0){ if($room) delete_image_file($room['image_url'],$uploadDir); set_flash('success','Room deleted successfully.'); } else set_flash('error','Room could not be deleted. It may have bookings or may already be inactive.');
$stmt->close(); header('Location: rooms.php'); exit;
