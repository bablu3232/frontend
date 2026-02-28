<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include 'db.php';

echo "Connected.<br>";

// Add category
$sql = "ALTER TABLE reports ADD COLUMN category VARCHAR(255) NULL";
if ($conn->query($sql) === TRUE) {
    echo "Added category successfully.<br>";
} else {
    echo "Error adding category: " . $conn->error . "<br>";
}

// Add report_date
$sql = "ALTER TABLE reports ADD COLUMN report_date DATE NULL";
if ($conn->query($sql) === TRUE) {
    echo "Added report_date successfully.<br>";
} else {
    echo "Error adding report_date: " . $conn->error . "<br>";
}

// Check final schema again
echo "<h2>Final Schema:</h2>";
$result = $conn->query("DESCRIBE reports");
if ($result) {
    while ($row = $result->fetch_assoc()) {
        echo $row['Field'] . " - " . $row['Type'] . "<br>";
    }
}
?>
