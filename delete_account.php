<?php
header("Content-Type: application/json");
error_reporting(0);
ini_set('display_errors', 0);
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['user_id'])) {
    http_response_code(400);
    echo json_encode(["message" => "User ID required"]);
    exit;
}

$userId = intval($data['user_id']);

// Start transaction to ensure all or nothing is deleted
$conn->begin_transaction();

try {
    // 1. Delete parameters associated with the user's reports
    $deleteParamsSql = "DELETE FROM report_parameters WHERE report_id IN (SELECT id FROM reports WHERE user_id = ?)";
    $stmt1 = $conn->prepare($deleteParamsSql);
    $stmt1->bind_param("i", $userId);
    $stmt1->execute();

    // 2. Delete the user's reports
    $deleteReportsSql = "DELETE FROM reports WHERE user_id = ?";
    $stmt2 = $conn->prepare($deleteReportsSql);
    $stmt2->bind_param("i", $userId);
    $stmt2->execute();

    // 3. Delete the user account
    $deleteUserSql = "DELETE FROM users WHERE id = ?";
    $stmt3 = $conn->prepare($deleteUserSql);
    $stmt3->bind_param("i", $userId);
    $stmt3->execute();

    // Commit transaction
    $conn->commit();
    echo json_encode(["message" => "Account and all associated data deleted successfully"]);
} catch (Exception $e) {
    // Rollback on error
    $conn->rollback();
    http_response_code(500);
    echo json_encode(["message" => "Error deleting account: " . $e->getMessage()]);
}

$conn->close();
?>
