with open("src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java", "r") as f:
    c = f.read()

c = c.replace("import com.github.scoliossis.events.impl.Render2DEvent;", "import com.github.scoliossis.events.impl.RenderTickEvent;")
c = c.replace("public static void onRender2D(Render2DEvent event) {", "public static void onRenderTick(RenderTickEvent event) {")

with open("src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java", "w") as f:
    f.write(c)
