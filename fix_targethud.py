with open("src/main/java/com/github/scoliossis/modules/impl/render/TargetHUD.java", "r") as f:
    c = f.read()

c = c.replace("public class TargetHUD extends Module {", "public class TargetHUD extends Module {\n    public static final com.github.scoliossis.utils.tenacity.animations.ContinualAnimation healthAnimation = new com.github.scoliossis.utils.tenacity.animations.ContinualAnimation();")

with open("src/main/java/com/github/scoliossis/modules/impl/render/TargetHUD.java", "w") as f:
    f.write(c)
