with open("src/main/java/com/github/scoliossis/modules/impl/render/Statistics.java", "r") as f:
    c = f.read()

import re

c = re.sub(r'private static void drawClayPanel\(.*?\{.*?\}', '''private static void drawClayPanel(float x, float y, float w, float h, float radius, Color bg) {
        com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawRound(x, y, w, h, radius, bg);
    }''', c, flags=re.DOTALL)

c = re.sub(r'private static void drawClayPanelWithAccent\(.*?\{.*?\}', '''private static void drawClayPanelWithAccent(float x, float y, float w, float h, float radius, Color bg) {
        Color[] theme = com.github.scoliossis.modules.impl.client.ThemeModule.getThemeColours();
        com.github.scoliossis.utils.tenacity.render.RoundedUtil.drawGradientRound(x, y, w, h, radius, theme[0], theme.length > 3 ? theme[3] : theme[0], theme.length > 1 ? theme[1] : theme[0], theme.length > 2 ? theme[2] : theme[0]);
    }''', c, flags=re.DOTALL)

with open("src/main/java/com/github/scoliossis/modules/impl/render/Statistics.java", "w") as f:
    f.write(c)
