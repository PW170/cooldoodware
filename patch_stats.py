with open("src/main/java/com/github/scoliossis/modules/impl/render/Statistics.java", "r") as f:
    c = f.read()

c = c.replace("private static void drawClayPanel(float x, float y, float w, float h, float radius, Color bg) {", "private static void drawClayPanel(float x, float y, float w, float h, float radius, Color bg) {\n        com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(x, y, w, h, radius, bg);\n        return;\n")

c = c.replace("private static void drawClayPanelWithAccent(float x, float y, float w, float h, float radius, Color bg) {", "private static void drawClayPanelWithAccent(float x, float y, float w, float h, float radius, Color bg) {\n        Color[] theme = com.github.scoliossis.modules.impl.client.ThemeModule.getThemeColours();\n        com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawGradientRound(x, y, w, h, radius, theme[0], theme.length > 3 ? theme[3] : theme[0], theme.length > 1 ? theme[1] : theme[0], theme.length > 2 ? theme[2] : theme[0]);\n        return;\n")

with open("src/main/java/com/github/scoliossis/modules/impl/render/Statistics.java", "w") as f:
    f.write(c)
