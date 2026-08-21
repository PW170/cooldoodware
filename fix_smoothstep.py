import os

fpath = "src/main/java/com/github/scoliossis/utils/tenacity/animations/impl/SmoothStepAnimation.java"
with open(fpath, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("dev.tenacity.utils.", "com.github.scoliossis.utils.tenacity.")
c = c.replace("package dev.tenacity.", "package com.github.scoliossis.")
c = c.replace("import dev.tenacity.", "import com.github.scoliossis.")

with open(fpath, "w", encoding="utf-8") as f:
    f.write(c)

print("Fixed SmoothStepAnimation")
