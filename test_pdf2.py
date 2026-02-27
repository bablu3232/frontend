import sys
import pytesseract
from pdf2image import convert_from_path
import cv2
import numpy as np
import traceback

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
POPPLER_PATH = r"C:\Users\Nikhil M\Downloads\Release-25.12.0-0\poppler-25.12.0\Library\bin"
pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772082033487_1772082033_699fd371f0589"

def preprocess_enhanced(img):
    img = cv2.resize(img, None, fx=3, fy=3, interpolation=cv2.INTER_CUBIC)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.bilateralFilter(gray, 9, 75, 75)
    kernel = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])
    gray = cv2.filter2D(gray, -1, kernel)
    _, gray = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
    return gray

def multi_pass_ocr(img):
    try:
        processed_a = preprocess_enhanced(img)
        text_a = pytesseract.image_to_string(processed_a, config='--psm 4')
        
        img_b = cv2.resize(img, None, fx=2, fy=2, interpolation=cv2.INTER_CUBIC)
        gray_b = cv2.cvtColor(img_b, cv2.COLOR_BGR2GRAY)
        text_b = pytesseract.image_to_string(gray_b, config='--psm 6')
        
        kernel = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])
        gray_c = cv2.filter2D(gray_b.copy(), -1, kernel)
        _, gray_c = cv2.threshold(gray_c, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
        text_c = pytesseract.image_to_string(gray_c, config='--psm 3')
        
        return text_a + "\n" + text_b + "\n" + text_c
    except Exception as e:
        print("Error in ocr:")
        traceback.print_exc()
        return ""

try:
    print(f"Converting {pdf_path}")
    pages = convert_from_path(pdf_path, dpi=300, poppler_path=POPPLER_PATH)
    print(f"Extracted {len(pages)} pages")
    for i, page in enumerate(pages):
        img = np.array(page)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        text = multi_pass_ocr(img)
        print(f"Text for page {i}:")
        print(text[:200]) # First 200 chars
        print("===")
except Exception as e:
    print(f"Exception during convert_from_path:")
    traceback.print_exc()

