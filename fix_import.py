import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'r', 'utf-8') as f:
    code = f.read()

# Add import if missing
if 'import com.github.scoliossis.utils.tenacity.render.ColorUtil;' not in code:
    code = code.replace('import com.github.scoliossis.utils.tenacity.render.GradientUtil;', 'import com.github.scoliossis.utils.tenacity.render.GradientUtil;\nimport com.github.scoliossis.utils.tenacity.render.ColorUtil;')

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'w', 'utf-8') as f:
    f.write(code)

print("done")
