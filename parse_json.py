import json
import codecs

with open('C:/Users/Nikhil M/AndroidStudioProjects/DrugsSearch/ocr_output_full.json', 'rb') as f:
    raw = f.read()

text = raw.decode('utf-8', errors='replace')
if text.startswith('\ufeff'):
    text = text[1:]

try:
    data = json.loads(text)
    with open('debug_keys.txt', 'w', encoding='utf-8') as out:
        for k, v in data.get('parameters', {}).items():
            out.write(f"KEY: {repr(k)} VAL: {v.get('value')}\n")
    print("Successfully parsed and wrote keys.")
except Exception as e:
    print(f"Error parsing json: {e}")
