<?php
$conn = new mysqli('localhost', 'root', '', 'drugssearch');
if ($conn->connect_error) die("Conn failed: " . $conn->connect_error);
$res = $conn->query('SELECT drug_category, COUNT(*) as count FROM drugs GROUP BY drug_category');
if ($res) {
    while($row = $res->fetch_assoc()) {
        echo $row['drug_category'] . ': ' . $row['count'] . "\n";
    }
} else {
    echo "Error: " . $conn->error;
}
$conn->close();
?>
