import os

with open("src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java", "r") as f:
    c = f.read()

c = c.replace("m.getName()", "m.getAnnotation().name()")
c = c.replace("m.getSuffix() != null", "!m.arrayListExtraInfo().isEmpty()")
c = c.replace("m.getSuffix()", "m.arrayListExtraInfo()")
c = c.replace("anim.getOutput()", "anim.getOutput().floatValue()")

with open("src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java", "w") as f:
    f.write(c)

print("ArrayList fixed")
