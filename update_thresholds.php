<?php
$conn = new mysqli('localhost', 'root', '', 'drugssearch');
if ($conn->connect_error) die("Connection failed: " . $conn->connect_error);

// Update WBC to absolute counts
$sql1 = "UPDATE lab_parameters SET min_value = 4000, max_value = 11000, unit = '/µL' WHERE parameter_name = 'WBC'";
if ($conn->query($sql1)) echo "WBC updated\n";

// Update Platelets to 10^5 scale (common for 2.25 style values)
$sql2 = "UPDATE lab_parameters SET min_value = 1.5, max_value = 4.5, unit = '10^5/µL' WHERE parameter_name = 'Platelets' OR parameter_name = 'PLT'";
if ($conn->query($sql2)) echo "Platelets updated\n";

$conn->close();
?>
