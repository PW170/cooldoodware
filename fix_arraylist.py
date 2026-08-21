import re
import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

code = code.replace('static { arraylistDraggable.x = 0.85; arraylistDraggable.y = 0.05; }', '')
code = code.replace('" A 7"', '"\xA77"')

code = code.replace('public static Draggable arraylistDraggable = new Draggable(', 'public static Draggable arraylistDraggable = new Draggable(')

# append static block at the very end of the class
code = code.replace('protected void onDisable() {}', 'protected void onDisable() {}\n    static { arraylistDraggable.x = 0.85; arraylistDraggable.y = 0.05; }')


with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
