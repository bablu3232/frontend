<?php
$conn = new mysqli('localhost', 'root', '', 'drugssearch');
if ($conn->connect_error) die("Connection failed: " . $conn->connect_error);

$res = $conn->query("SELECT parameter_name, min_value, max_value, unit FROM lab_parameters WHERE parameter_name IN ('WBC', 'Platelets')");
while($row = $res->fetch_assoc()) {
    print_r($row);
}
?>
