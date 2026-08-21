import os

anim_file = "src/main/java/com/github/scoliossis/utils/tenacity/animations/Animation.java"
with open(anim_file, "r") as f:
    c = f.read()
c = c.replace("com.github.scoliossis.utils.tenacity.time.TimerUtil", "com.github.scoliossis.utils.time.TimerUtil")
with open(anim_file, "w") as f:
    f.write(c)

print("Fixed TimerUtil")
