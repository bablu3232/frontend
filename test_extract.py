import sys
import importlib.util
import json

spec = importlib.util.spec_from_file_location("ocr_extract", "c:/Users/Nikhil M/AndroidStudioProjects/DrugsSearch/backend/ocr_extract.py")
ocr_extract = importlib.util.module_from_spec(spec)

# We have to patch argv before executing because it uses sys.argv[1]
sys.argv = ['ocr_extract.py', 'c:/xampp/htdocs/drugssearch/uploads/temp_upload_file_1772082033487_1772082033_699fd371f0589']

try:
    spec.loader.exec_module(ocr_extract)
except SystemExit:
    pass

