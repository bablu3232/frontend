<?php
header("Content-Type: application/json");
error_reporting(0);
ini_set('display_errors', 0);
include "db.php";

$query = $_GET['query'] ?? '';

if (empty($query)) {
    echo json_encode([]);
    exit;
}

$searchTerm = "%" . $query . "%";
$stmt = $conn->prepare("SELECT * FROM drugs WHERE name LIKE ?");
$stmt->bind_param("s", $searchTerm);
$stmt->execute();
$result = $stmt->get_result();

$drugs = [];

while ($row = $result->fetch_assoc()) {
    $drugs[] = [
        'name' => $row['name'],
        'condition' => $row['condition'],
        'dosages' => json_decode($row['dosages']),
        'description' => $row['description'],
        'side_effects' => $row['side_effects'],
        'warnings' => $row['warnings'],
        'storage' => $row['storage']
    ];
}

echo json_encode($drugs);

$conn->close();
?>
