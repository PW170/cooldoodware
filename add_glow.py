import codecs
import re

# --- ArrayList Glow ---
with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Replace drawing the background
old_bg = r'// Tenacity style background\s*RenderUtil\.drawRect\(x - 2, y, width \+ 4, height, new Color\(0, 0, 0, 120\)\);'
new_bg = '''// Tenacity style background
                    // Simulated Glow
                    Color c2 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    for (int i = 6; i > 0; i--) {
                        com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(x - 2 - i, y - i, width + 4 + (i*2), height + (i*2), 3, new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 15));
                    }
                    RenderUtil.drawRect(x - 2, y, width + 4, height, new Color(0, 0, 0, 120));'''

code = re.sub(old_bg, new_bg, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)

# --- Watermark Glow ---
with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'r', 'utf-8') as f:
    code = f.read()

old_watermark = r'GradientUtil\.applyGradientHorizontal\(0, 0, width, height, 1, color1, color2, \(\) -> \{\s*FontUtil\.drawString\(CLIENT_NAME, 0, 0, size, Color\.WHITE, true\);\s*\}\);'

new_watermark = '''// Simulated Glow
                for (int i = 8; i > 0; i--) {
                    Color glowC = ColorUtil.interpolateColorsBackAndForth(15, 0, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(-i, -i, width + FontUtil.getStringWidth(extraText, extraSize) + (i*2), height + (i*2), 4, new Color(glowC.getRed(), glowC.getGreen(), glowC.getBlue(), 12));
                }

                GradientUtil.applyGradientHorizontal(0, 0, width, height, 1, color1, color2, () -> {
                    FontUtil.drawString(CLIENT_NAME, 0, 0, size, Color.WHITE, true);
                });'''

code = re.sub(old_watermark, new_watermark, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'w', 'utf-8') as f:
    f.write(code)

print("done")
