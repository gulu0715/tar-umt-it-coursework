<?php
require '../config.php'; require '../auth.php'; require '../helpers.php'; require_admin();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('Method Not Allowed'); }
verify_csrf(); $id=(int)($_POST['id']??0);
$stmt=$conn->prepare('DELETE FROM testimonials WHERE id=?'); $stmt->bind_param('i',$id); if($stmt->execute()&&$stmt->affected_rows>0)set_flash('success','Comment deleted successfully.'); else set_flash('error','Comment could not be deleted.'); $stmt->close(); header('Location: testimonials.php'); exit;
