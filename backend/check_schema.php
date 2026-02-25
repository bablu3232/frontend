<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include "db.php";

$tables = ['users', 'reports'];

foreach ($tables as $table) {
    try {
        $result = $conn->query("SHOW CREATE TABLE $table");
        if ($result) {
            $row = $result->fetch_assoc();
            echo "<h3>Schema for $table:</h3>";
            echo "<pre>" . htmlspecialchars($row['Create Table']) . "</pre>";
        } else {
            echo "<h3>Table $table does not exist or error: " . $conn->error . "</h3>";
        }
    } catch (Exception $e) {
        echo "Error: " . $e->getMessage();
    }
}
?>
