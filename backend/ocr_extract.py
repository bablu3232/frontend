import pytesseract
import cv2
import sys
import re
import json
from pdf2image import convert_from_path

# Tesseract path (Windows)
pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

# Poppler path (UPDATE if different)
POPPLER_PATH = r"C:\Users\Nikhil M\Downloads\Release-25.12.0-0\poppler-25.12.0\Library\bin"

file_path = sys.argv[1]

# -------------------------------
# OCR FUNCTIONS
# -------------------------------
def extract_text_from_image(image_path):
    img = cv2.imread(image_path)
    if img is None:
        return ""
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    return pytesseract.image_to_string(gray)

def extract_text_from_pdf(pdf_path):
    pages = convert_from_path(
        pdf_path,
        dpi=300,
        poppler_path=POPPLER_PATH
    )
    text = ""
    for page in pages:
        text += pytesseract.image_to_string(page)
    return text

# -------------------------------
# CATEGORY & PARAMETER DEFINITIONS
# -------------------------------
CATEGORY_PARAMETERS = {
    "Blood Count": {
        "Hemoglobin": {
            "pattern": r"Hemoglobin:\s*([\d.]+)",
            "unit": "g/dL",
            "status": lambda x: "Low" if x < 12 else "Normal"
        }
    },
    "Lipid Profile": {
        "Total Cholesterol": {
            "pattern": r"Total Cholesterol:\s*(\d+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 200 else "Normal"
        },
        "LDL": {
            "pattern": r"LDL Cholesterol:\s*(\d+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 130 else "Normal"
        },
        "HDL": {
            "pattern": r"HDL Cholesterol:\s*(\d+)",
            "unit": "mg/dL",
            "status": lambda x: "Low" if x < 40 else "Normal"
        },
        "Triglycerides": {
            "pattern": r"Triglycerides:\s*(\d+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 150 else "Normal"
        }
    },
    "Kidney Function": {
        "Creatinine": {
            "pattern": r"Creatinine:\s*([\d.]+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 1.2 else "Normal"
        },
        "Urea": {
            "pattern": r"Urea:\s*([\d.]+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 40 else "Normal"
        }
    },
    "Liver Function": {
        "Bilirubin": {
            "pattern": r"Bilirubin:\s*([\d.]+)",
            "unit": "mg/dL",
            "status": lambda x: "High" if x > 1.2 else "Normal"
        }
    }
}

# -------------------------------
# OCR EXECUTION
# -------------------------------
if file_path.lower().endswith(".pdf"):
    raw_text = extract_text_from_pdf(file_path)
else:
    raw_text = extract_text_from_image(file_path)

# -------------------------------
# PARAMETER & CATEGORY DETECTION
# -------------------------------
detected_parameters = {}
category_scores = {}

for category, params in CATEGORY_PARAMETERS.items():
    category_scores[category] = 0

    for param_name, details in params.items():
        match = re.search(details["pattern"], raw_text, re.IGNORECASE)
        if match:
            value = float(match.group(1))
            detected_parameters[param_name] = {
                "value": value,
                "unit": details["unit"],
                "status": details["status"](value),
                "category": category
            }
            category_scores[category] += 1

# -------------------------------
# REPORT CATEGORY DECISION
# -------------------------------
matched_categories = [cat for cat, count in category_scores.items() if count > 0]

if len(matched_categories) == 0:
    report_category = "Unknown Report"
elif len(matched_categories) == 1:
    report_category = matched_categories[0]
else:
    report_category = "Mixed Report"

# -------------------------------
# FINAL JSON OUTPUT
# -------------------------------
output = {
    "report_category": report_category,
    "parameters": detected_parameters
}

print(json.dumps(output))
