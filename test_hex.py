import json
import codecs

with open('C:/Users/Nikhil M/AndroidStudioProjects/DrugsSearch/ocr_output_full.json', 'r', encoding='utf-8') as f:
    text = f.read()

# Since we crashed on utf-8 decode before, let's just run ocr_extract and print immediately
import pytesseract
from pdf2image import convert_from_path
import cv2
import numpy as np
import sys

sys.path.append(r"c:\Users\Nikhil M\AndroidStudioProjects\DrugsSearch\backend")
import ocr_extract

pdf_path = r"c:\xampp\htdocs\drugssearch\uploads\temp_upload_file_1772088001118_Gallery_20260226_101920_1772088001_699feac1d5c03.pdf"
pages = convert_from_path(pdf_path, dpi=300, poppler_path=ocr_extract.POPPLER_PATH)
img = np.array(pages[0])
img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

raw_text = ocr_extract.multi_pass_ocr(img)

# Quick mock of the script's dict builder
# Just run the actual script using subprocess but piping to file
