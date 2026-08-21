import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Fix bold double strike shadow overlapping glitch
old_draw = r'FontUtil\.drawString\(text, x \+ 0\.5f, y \+ 1, size, c1, true\);\s*FontUtil\.drawString\(text, x, y \+ 1, size, c1, true\);'

new_draw = r'''FontUtil.drawString(text, x + 0.5f, y + 1, size, c1, true);
                    FontUtil.drawString(text, x, y + 1, size, c1, false);'''

def replacer(match):
    return new_draw

code = re.sub(old_draw, replacer, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
