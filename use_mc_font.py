import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/utils/render/FontUtil.java', 'r', 'utf-8') as f:
    code = f.read()

# Replace drawStringFade
old_drawFade = r'public static float drawStringFade\(String string, float x, float y, int size, Color\[\] colour, double fadeSpeed, double fadeSpread, boolean dropShadow\) \{[\s\S]*?return totalWidth / scaleFactor;\s*\}'
new_drawFade = '''public static float drawStringFade(String string, float x, float y, int size, Color[] colour, double fadeSpeed, double fadeSpread, boolean dropShadow) {
        if (string.isEmpty()) return 0;
        return drawMinecraftString(string, x, y, size, colour[0], dropShadow);
    }'''

code = re.sub(old_drawFade, new_drawFade, code)

# Replace getStringWidth
old_strWidth = r'public static int getStringWidth\(String string, int fontSize\) \{[\s\S]*?return \(int\) \(width \* \(originalSize / \(double\) generatedSize\)\);\s*\}'
new_strWidth = '''public static int getStringWidth(String string, int fontSize) {
        return getMinecraftStringWidth(string, fontSize);
    }'''

code = re.sub(old_strWidth, new_strWidth, code)

# Replace getCharWidth
old_charWidth = r'public static int getCharWidth\(char c, int fontSize\) \{[\s\S]*?return \(int\) \(fontTexture\.charBounds\.get\(c\)\.width \* \(originalSize / \(double\) generatedSize\)\);\s*\}'
new_charWidth = '''public static int getCharWidth(char c, int fontSize) {
        return getMinecraftCharWidth(c, fontSize);
    }'''

code = re.sub(old_charWidth, new_charWidth, code)

# Replace getFontHeight
old_fontHeight = r'public static int getFontHeight\(int fontSize\) \{[\s\S]*?return \(int\) \(fontTexture\.height \* \(originalSize / \(double\) generatedSize\)\);\s*\}'
new_fontHeight = '''public static int getFontHeight(int fontSize) {
        return (int) (C.mc.fontRendererObj.FONT_HEIGHT * 0.1 * fontSize);
    }'''

code = re.sub(old_fontHeight, new_fontHeight, code)

with codecs.open('src/main/java/com/github/scoliossis/utils/render/FontUtil.java', 'w', 'utf-8') as f:
    f.write(code)

print("done")
