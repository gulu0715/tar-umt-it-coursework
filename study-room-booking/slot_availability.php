<?php
require 'config.php'; require 'auth.php'; require 'helpers.php';
header('Content-Type: application/json; charset=utf-8');
$roomId=(int)($_GET['room_id']??0); $date=trim($_GET['booking_date']??''); $exclude=(int)($_GET['exclude_booking_id']??0);
if($roomId<1 || !valid_date_string($date)){ http_response_code(400); echo json_encode(['error'=>'Invalid room or date.']); exit; }
$stmt=$conn->prepare('SELECT id FROM rooms WHERE id=? AND is_active=1'); $stmt->bind_param('i',$roomId); $stmt->execute(); $room=$stmt->get_result()->fetch_assoc(); $stmt->close();
if(!$room){ http_response_code(404); echo json_encode(['error'=>'Room not found.']); exit; }
$sql='SELECT time_slot FROM bookings WHERE room_id=? AND booking_date=?'; if($exclude>0)$sql.=' AND id<>?';
$stmt=$conn->prepare($sql); if($exclude>0)$stmt->bind_param('isi',$roomId,$date,$exclude);else$stmt->bind_param('is',$roomId,$date);$stmt->execute();$rows=$stmt->get_result()->fetch_all(MYSQLI_ASSOC);$stmt->close();
$valid=array_fill_keys(campus_time_slots(),true);$full=[];foreach($rows as $r){if(isset($valid[$r['time_slot']]))$full[]=$r['time_slot'];}
echo json_encode(['full_slots'=>array_values(array_unique($full)),'time_slots'=>campus_time_slots()]);
