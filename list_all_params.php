<?php
$conn = new mysqli('localhost', 'root', '', 'drugssearch');
if ($conn->connect_error) die("Connection failed: " . $conn->connect_error);

$res = $conn->query("SELECT parameter_name, min_value, max_value, unit FROM lab_parameters");
while($row = $res->fetch_assoc()) {
    echo "{$row['parameter_name']}: {$row['min_value']} - {$row['max_value']} ({$row['unit']})\n";
}
?>
