<?php
require '../config.php'; require '../auth.php'; require '../helpers.php'; require_admin();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('Method Not Allowed'); }
verify_csrf(); $id=(int)($_POST['id']??0); $myId=(int)current_user_id();
if($id===$myId){set_flash('error','You cannot change your own admin status.');}else{ $stmt=$conn->prepare('UPDATE users SET is_admin = NOT is_admin WHERE id=?'); $stmt->bind_param('i',$id); if($stmt->execute()&&$stmt->affected_rows>0)set_flash('success','User role updated successfully.'); else set_flash('error','User role could not be updated.'); $stmt->close(); }
header('Location: users.php'); exit;
