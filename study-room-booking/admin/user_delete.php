<?php
require '../config.php'; require '../auth.php'; require '../helpers.php'; require_admin();
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('Method Not Allowed'); }
verify_csrf(); $id=(int)($_POST['id']??0); $myId=(int)current_user_id();
if($id===$myId){set_flash('error','You cannot delete your own account.');}else{ $stmt=$conn->prepare('DELETE FROM users WHERE id=? AND is_admin=0'); $stmt->bind_param('i',$id); if($stmt->execute()&&$stmt->affected_rows>0)set_flash('success','User account deleted successfully.');else set_flash('error','The user could not be deleted.'); $stmt->close(); }
header('Location: users.php'); exit;
