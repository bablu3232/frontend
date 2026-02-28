import os
import re

ui_dir = r"c:\Users\Nikhil M\AndroidStudioProjects\DrugsSearch\app\src\main\java\com\simats\drugssearch\ui"
major_screens = {
    "LoginScreen.kt",
    "RegisterScreen.kt",
    "DashboardScreen.kt",
    "ProfileScreen.kt",
    "VerifyEmailScreen.kt",
    "ForgotPasswordScreen.kt",
    "ResetPasswordScreen.kt"
}

# Regex to safely target the Footer Links block independently
pattern_links = re.compile(r"[ \t]*// Footer Links\s*Row\([\s\S]*?Contact Us[\s\S]*?\}\n", re.IGNORECASE)

# Regex to safely target the Copyright block independently (if it exists)
pattern_copyright = re.compile(r"[ \t]*// Copyright\s*Text\([\s\S]*?All rights reserved[\s\S]*?\)\n", re.IGNORECASE)

total_cleaned = 0

for filename in os.listdir(ui_dir):
    if filename.endswith(".kt") and filename not in major_screens:
        filepath = os.path.join(ui_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        orig_content = content
        
        # Replace targets
        content, c1 = pattern_links.subn("", content)
        content, c2 = pattern_copyright.subn("", content)
        
        if c1 > 0 or c2 > 0:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Cleaned {filename}")
            total_cleaned += 1

print(f"\nTask Complete. Cleaned footers from {total_cleaned} leftover files.")
