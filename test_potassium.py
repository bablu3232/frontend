import sys
import pytesseract
from pdf2image import convert_from_path
import cv2
import numpy as np
import re

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
POPPLER_PATH = r"C:\Users\Nikhil M\Downloads\Release-25.12.0-0\poppler-25.12.0\Library\bin"
pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772088001118_Gallery_20260226_101920_1772088001_699feac1d5c03.pdf"

def multi_pass(img):
    img = cv2.resize(img, None, fx=3, fy=3, interpolation=cv2.INTER_CUBIC)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.bilateralFilter(gray, 9, 75, 75)
    kernel = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])
    gray = cv2.filter2D(gray, -1, kernel)
    _, gray = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
    return pytesseract.image_to_string(gray, config='--psm 4')

try:
    pages = convert_from_path(pdf_path, dpi=300, poppler_path=POPPLER_PATH)
    img = np.array(pages[0])
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
    text = multi_pass(img)
    for line in text.split('\n'):
        if 'Potass' in line or 'assium' in line:
            print(repr(line))
except Exception as e:
    print(e)

