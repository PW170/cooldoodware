import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# gap
code = code.replace('float height = FontUtil.getFontHeight(size) + 4;', 'float height = FontUtil.getFontHeight(size) + 2;')

# bold + remove sidebar
old_draw = r'// Side bar\s*Color c1 = ColorUtil\.interpolateColorsBackAndForth\(15, index \* 20, theme\[0\], theme\[theme\.length > 1 \? 1 : 0\], false\);\s*RenderUtil\.drawRect\(x \+ width, y, 2, height, c1\);\s*FontUtil\.drawString\(text, x, y \+ 2, size, c1, true\);'

new_draw = r'''Color c1 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    
                    // Double strike to make it bold
                    FontUtil.drawString(text, x + 0.5f, y + 1, size, c1, true);
                    FontUtil.drawString(text, x, y + 1, size, c1, true);'''

def replacer(match):
    return new_draw

code = re.sub(old_draw, replacer, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
