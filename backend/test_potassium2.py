import sys
import pytesseract
from pdf2image import convert_from_path
import cv2
import numpy as np

pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772088001118_Gallery_20260226_101920_1772088001_699feac1d5c03.pdf"
sys.argv = ['ocr_extract.py', pdf_path]

import ocr_extract

pages = convert_from_path(pdf_path, dpi=300, poppler_path=ocr_extract.POPPLER_PATH)
img = np.array(pages[0])
img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

text = ocr_extract.multi_pass_ocr(img)
for line in text.split('\n'):
    if 'otass' in line or 'assium' in line:
        print(repr(line))
