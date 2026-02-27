import sys
from pdf2image import convert_from_path
import cv2
import numpy as np
import traceback

POPPLER_PATH = r"C:\Users\Nikhil M\Downloads\Release-25.12.0-0\poppler-25.12.0\Library\bin"
pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772082033487_1772082033_699fd371f0589"

try:
    print(f"Converting {pdf_path}")
    pages = convert_from_path(pdf_path, dpi=300, poppler_path=POPPLER_PATH)
    print(f"Extracted {len(pages)} pages")
except Exception as e:
    print(f"Exception during convert_from_path:")
    traceback.print_exc()

