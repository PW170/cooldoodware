import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Make gap a bit less
code = code.replace('float height = FontUtil.getFontHeight(size) + 4;', 'float height = FontUtil.getFontHeight(size) + 1;')

# Remove line on right, and make text bold (double strike)
old_draw = '''                    // Side bar
                    Color c1 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    RenderUtil.drawRect(x + width, y, 2, height, c1);
                    
                    FontUtil.drawString(text, x, y + 2, size, c1, true);'''

new_draw = '''                    Color c1 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    
                    // Double strike to make it bold
                    FontUtil.drawString(text, x + 0.5f, y + 1, size, c1, true);
                    FontUtil.drawString(text, x, y + 1, size, c1, true);'''

code = code.replace(old_draw, new_draw)

# And because gap is less (y + 1 instead of y + 2 for centered text)
with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
