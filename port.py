import os
import shutil
import re

TENACITY_DIR = "e:/999/Tenacity-master/Tenacity-master/src/main/java/dev/tenacity"
COOLWARE_DIR = "e:/999/coolware/src/main/java/com/github/scoliossis"

def copy_and_patch(src_rel, dst_rel):
    src = os.path.join(TENACITY_DIR, src_rel)
    dst = os.path.join(COOLWARE_DIR, dst_rel)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    
    with open(src, "r", encoding="utf-8") as f:
        content = f.read()
        
    # Replace packages
    content = content.replace("dev.tenacity.utils.", "com.github.scoliossis.utils.tenacity.")
    content = content.replace("package dev.tenacity.", "package com.github.scoliossis.")
    content = content.replace("import dev.tenacity.", "import com.github.scoliossis.")
    
    if "RenderUtil.java" in src_rel:
        content = content.replace("class RenderUtil", "class TRenderUtil")
        content = content.replace(" RenderUtil ", " TRenderUtil ")
        content = content.replace(" RenderUtil.", " TRenderUtil.")
    else:
        content = content.replace(" RenderUtil.", " TRenderUtil.")
        content = content.replace("import com.github.scoliossis.utils.tenacity.render.RenderUtil;", "import com.github.scoliossis.utils.tenacity.render.TRenderUtil;")
    
    with open(dst, "w", encoding="utf-8") as f:
        f.write(content)

utils_to_copy = [
    "utils/render/RoundedUtil.java",
    "utils/render/ColorUtil.java",
    "utils/render/GradientUtil.java",
    "utils/render/ShaderUtil.java",
    "utils/render/StencilUtil.java",
    "utils/render/RenderUtil.java",
    "utils/objects/GradientColorWheel.java",
    "utils/objects/Dragging.java",
    "utils/animations/Animation.java",
    "utils/animations/ContinualAnimation.java",
    "utils/animations/Direction.java",
    "utils/animations/impl/DecelerateAnimation.java",
    "utils/misc/MathUtils.java",
]

for u in utils_to_copy:
    dst = u.replace("utils/", "utils/tenacity/")
    if "RenderUtil.java" in u:
        dst = dst.replace("RenderUtil.java", "TRenderUtil.java")
    copy_and_patch(u, dst)
    
print("Copied utils.")
