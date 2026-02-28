<?php
include "db.php";
echo "<h2>Database Drugs Listing</h2>";
$result = $conn->query("SELECT drug_name, drug_category, indication FROM drugs");
if ($result->num_rows > 0) {
    echo "<table border='1'><tr><th>Name</th><th>Category</th><th>Indication</th></tr>";
    while($row = $result->fetch_assoc()) {
        echo "<tr><td>".$row["drug_name"]."</td><td>".$row["drug_category"]."</td><td>".$row["indication"]."</td></tr>";
    }
    echo "</table>";
} else {
    echo "0 results";
}
$conn->close();
?>
