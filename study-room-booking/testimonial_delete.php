<?php
require 'config.php'; require 'auth.php'; require 'helpers.php'; require_login();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('Method Not Allowed'); }
verify_csrf(); $id=(int)($_POST['id']??0); $uid=current_user_id();
$stmt=$conn->prepare('DELETE FROM testimonials WHERE id=? AND user_id=?'); $stmt->bind_param('ii',$id,$uid); if($stmt->execute()&&$stmt->affected_rows>0)set_flash('success','Comment deleted successfully.'); else set_flash('error','Comment could not be deleted.'); $stmt->close(); header('Location: testimonials.php'); exit;
