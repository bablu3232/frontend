import json
with open('ocr_output_full.json', 'r', encoding='utf-16le') as f:
    data = json.load(f)
    print("Keys in parameters:")
    for k in data['parameters'].keys():
        print(f"'{k}'")
