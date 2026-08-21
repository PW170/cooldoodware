with open("src/main/java/com/github/scoliossis/modules/impl/render/ChestESP.java", "r") as f:
    c = f.read()

c = c.replace("ColorUtil.resetColor()", "com.github.scoliossis.utils.tenacity.render.TRenderUtil.resetColor()")

with open("src/main/java/com/github/scoliossis/modules/impl/render/ChestESP.java", "w") as f:
    f.write(c)
