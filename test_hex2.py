import subprocess
import json

result = subprocess.run(['C:/Users/Nikhil M/AppData/Local/Programs/Python/Python310/python.exe', 'c:/Users/Nikhil M/AndroidStudioProjects/DrugsSearch/backend/ocr_extract.py', 'c:/xampp/htdocs/drugssearch/uploads/temp_upload_file_1772088001118_Gallery_20260226_101920_1772088001_699feac1d5c03.pdf'], capture_output=True, text=True)

try:
    data = json.loads(result.stdout)
    params = data.get('parameters', {})
    for k in params.keys():
        hex_str = ' '.join([hex(ord(c)) for c in k])
        print(f"'{k}' -> {hex_str}")
except Exception as e:
    print("Error parsing:")
    print(e)
    print("STDOUT:", result.stdout[:500])
