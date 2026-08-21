import os
import glob

for fpath in glob.glob("src/main/java/com/github/scoliossis/modules/impl/**/*.java", recursive=True):
    with open(fpath, "r", encoding="utf-8") as f:
        c = f.read()

    # ArrayList
    c = c.replace("FontUtil.drawStringWithShadow(text, x, y + 2, 20, c1.getRGB());", "FontUtil.drawString(text, x, y + 2, 20, c1, true);")
    
    # HUD
    c = c.replace("FontUtil.drawStringWithShadow(CLIENT_NAME, 0, 0, 40, -1);", "FontUtil.drawString(CLIENT_NAME, 0, 0, 40, Color.WHITE, true);")
    c = c.replace("FontUtil.drawStringWithShadow(extraText, width, height / 2f - FontUtil.getFontHeight(20) / 2f, 20, Color.WHITE.getRGB());", "FontUtil.drawString(extraText, width, height / 2f - FontUtil.getFontHeight(20) / 2f, 20, Color.WHITE, true);")
    
    with open(fpath, "w", encoding="utf-8") as f:
        f.write(c)

print("FontUtil fixed")
