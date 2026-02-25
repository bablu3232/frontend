<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include "db.php";

// Create drugs table
$sql = "CREATE TABLE IF NOT EXISTS drugs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `condition` VARCHAR(255) NOT NULL,
    dosages TEXT NOT NULL,
    description TEXT,
    side_effects TEXT,
    warnings TEXT,
    storage TEXT
)";

if ($conn->query($sql) === TRUE) {
    echo "Table 'drugs' created successfully.<br>";
} else {
    die("Error creating table: " . $conn->error);
}

// Initial Data
$drugs = [
    ["Metformin", "Diabetes", ["500mg", "850mg", "1000mg"]],
    ["Lisinopril", "Hypertension", ["2.5mg", "5mg", "10mg", "20mg"]],
    ["Atorvastatin", "Cholesterol", ["10mg", "20mg", "40mg", "80mg"]],
    ["Amlodipine", "Hypertension", ["2.5mg", "5mg", "10mg"]],
    ["Aspirin", "Pain Relief", ["81mg", "325mg", "500mg"]],
    ["Omeprazole", "Gastric", ["10mg", "20mg", "40mg"]],
    ["Insulin", "Diabetes", ["Various doses"]],
    ["Glipizide", "Diabetes", ["5mg", "10mg"]],
    ["Metoprolol", "Hypertension", ["25mg", "50mg", "100mg"]],
    ["Ibuprofen", "Pain Relief", ["200mg", "400mg", "600mg"]],
    ["Acetaminophen", "Pain Relief", ["325mg", "500mg", "650mg"]],
    ["Naproxen", "Pain Relief", ["220mg", "500mg"]],
    ["Simvastatin", "Cholesterol", ["10mg", "20mg", "40mg"]],
    ["Losartan", "Hypertension", ["25mg", "50mg", "100mg"]],
    ["Gabapentin", "Neurology", ["100mg", "300mg", "400mg"]],
    ["Amoxicillin", "Antibiotics", ["250mg", "500mg"]],
    ["Azithromycin", "Antibiotics", ["250mg", "500mg"]],
    ["Ciprofloxacin", "Antibiotics", ["250mg", "500mg", "750mg"]],
    ["Prednisone", "Anti-inflammatory", ["5mg", "10mg", "20mg"]],
    ["Cetirizine", "Allergy", ["5mg", "10mg"]],
    ["Loratadine", "Allergy", ["10mg"]],
    ["Vitamin D", "Supplements", ["1000 IU", "2000 IU", "5000 IU"]],
    ["Vitamin B12", "Supplements", ["500mcg", "1000mcg"]]
];

$stmt = $conn->prepare("INSERT INTO drugs (name, `condition`, dosages, description, side_effects, warnings, storage) VALUES (?, ?, ?, ?, ?, ?, ?)");

$desc = "Consult your healthcare provider.";
$side = "Follow your healthcare provider's instructions.";
$warn = "Consult your healthcare provider.";
$store = "Consult your healthcare provider.";

foreach ($drugs as $drug) {
    $name = $drug[0];
    $cond = $drug[1];
    $dose = json_encode($drug[2]);
    
    // Check if exists to avoid duplicates on refresh
    $check = $conn->query("SELECT id FROM drugs WHERE name = '$name'");
    if ($check->num_rows == 0) {
        $stmt->bind_param("sssssss", $name, $cond, $dose, $desc, $side, $warn, $store);
        $stmt->execute();
        echo "Inserted $name<br>";
    } else {
        echo "Skipped $name (already exists)<br>";
    }
}

echo "Drugs data population complete.";
$conn->close();
?>
