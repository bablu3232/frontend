import sys
import pytesseract
from pdf2image import convert_from_path
import cv2
import numpy as np
import traceback

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
POPPLER_PATH = r"C:\Users\Nikhil M\Downloads\Release-25.12.0-0\poppler-25.12.0\Library\bin"
pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772088001118_Gallery_20260226_101920_1772088001_699feac1d5c03.pdf"

try:
    pages = convert_from_path(pdf_path, dpi=300, poppler_path=POPPLER_PATH)
    img = np.array(pages[0])
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

    from c:/Users/Nikhil M/AndroidStudioProjects/DrugsSearch/backend/ocr_extract import multi_pass_ocr
except Exception:
    pass

sys.path.append(r"c:\Users\Nikhil M\AndroidStudioProjects\DrugsSearch\backend")
import ocr_extract

text = ocr_extract.multi_pass_ocr(img)
for line in text.split('\n'):
    if 'otass' in line or 'assium' in line:
        print(repr(line))

