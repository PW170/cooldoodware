import os
import glob

def fix_file(fpath):
    with open(fpath, "r", encoding="utf-8") as f:
        content = f.read()
    
    content = content.replace("dev.tenacity.utils.", "com.github.scoliossis.utils.tenacity.")
    content = content.replace("package dev.tenacity.", "package com.github.scoliossis.")
    content = content.replace("import dev.tenacity.", "import com.github.scoliossis.")
            
    with open(fpath, "w", encoding="utf-8") as f:
        f.write(content)

for f in glob.glob("src/main/java/com/github/scoliossis/utils/tenacity/time/*.java"):
    fix_file(f)

# Also fix the import in Animation.java back to tenacity's TimerUtil
anim = "src/main/java/com/github/scoliossis/utils/tenacity/animations/Animation.java"
with open(anim, "r") as f:
    c = f.read()
c = c.replace("com.github.scoliossis.utils.time.TimerUtil", "com.github.scoliossis.utils.tenacity.time.TimerUtil")
with open(anim, "w") as f:
    f.write(c)

print("Fixed Time")
