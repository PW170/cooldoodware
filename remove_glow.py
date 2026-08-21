import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Remove simulated glow
old_bg = r'// Simulated Glow\s*Color c2 = ColorUtil\.interpolateColorsBackAndForth\(15, index \* 20, theme\[0\], theme\[theme\.length > 1 \? 1 : 0\], false\);\s*for \(int i = 5; i > 0; i--\) \{\s*com\.github\.scoliossis\.utils\.tenacity\.render\.RoundedUtil\.drawRound\(x - 2 - i, y - i, width \+ 4 \+ \(i\*2\), height \+ \(i\*2\), 3, new Color\(c2\.getRed\(\), c2\.getGreen\(\), c2\.getBlue\(\), 12\)\);\s*\}'
new_bg = ''
code = re.sub(old_bg, new_bg, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'r', 'utf-8') as f:
    code = f.read()

old_glow = r'// Simulated Glow\s*for \(int i = 7; i > 0; i--\) \{\s*Color glowC = ColorUtil\.interpolateColorsBackAndForth\(15, 0, theme\[0\], theme\[theme\.length > 1 \? 1 : 0\], false\);\s*com\.github\.scoliossis\.utils\.tenacity\.render\.RoundedUtil\.drawRound\(-i, -i, width \+ FontUtil\.getStringWidth\(extraText, extraSize\) \+ \(i\*2\), height \+ \(i\*2\), 4, new Color\(glowC\.getRed\(\), glowC\.getGreen\(\), glowC\.getBlue\(\), 12\)\);\s*\}'
new_glow = ''
code = re.sub(old_glow, new_glow, code)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/HUD.java', 'w', 'utf-8') as f:
    f.write(code)

print("done")
