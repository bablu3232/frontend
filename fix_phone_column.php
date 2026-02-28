<?php
include 'db.php';

try {
    // Check current column type (optional, but good for verification)
    $result = $conn->query("SHOW COLUMNS FROM users LIKE 'phone'");
    $row = $result->fetch_assoc();
    echo "Current Type: " . $row['Type'] . "<br>";

    // Alter the table
    $sql = "ALTER TABLE users MODIFY phone VARCHAR(20)";
    
    if ($conn->query($sql) === TRUE) {
        echo "Column 'phone' modified successfully to VARCHAR(20).<br>";
    } else {
        echo "Error modifying column: " . $conn->error . "<br>";
    }

    // Verify change
    $result = $conn->query("SHOW COLUMNS FROM users LIKE 'phone'");
    $row = $result->fetch_assoc();
    echo "New Type: " . $row['Type'] . "<br>";

} catch (Exception $e) {
    echo "Exception: " . $e->getMessage();
}

$conn->close();
?>
