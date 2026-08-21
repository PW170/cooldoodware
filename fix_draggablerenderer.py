import codecs

with codecs.open('src/main/java/com/github/scoliossis/utils/render/draggable/DraggableRenderer.java', 'r', 'utf-8') as f:
    code = f.read()

# Modify rendering translation
old_render = '''                int renderX = (int) (draggable.x * C.res().getScaledWidth());
                int renderY = (int) (draggable.y * C.res().getScaledHeight());

                GL11.glPushMatrix();
                GL11.glTranslated(renderX, renderY, 0);'''

new_render = '''                int renderX = (int) (draggable.x * C.res().getScaledWidth());
                int renderY = (int) (draggable.y * C.res().getScaledHeight());
                
                if (draggable.anchor == Draggable.Anchor.RIGHT) {
                    renderX -= draggable.width;
                }

                GL11.glPushMatrix();
                GL11.glTranslated(renderX, renderY, 0);'''

# Modify drag logic
old_drag = '''                        draggable.x = (ScreenUtil.getMouseX() - draggingCoords.width) / C.res().getScaledWidth();
                        draggable.y = (ScreenUtil.getMouseY() - draggingCoords.height) / C.res().getScaledHeight();'''

new_drag = '''                        double newX = ScreenUtil.getMouseX() - draggingCoords.width;
                        if (draggable.anchor == Draggable.Anchor.RIGHT) newX += size[0];
                        draggable.x = newX / C.res().getScaledWidth();
                        
                        draggable.y = (ScreenUtil.getMouseY() - draggingCoords.height) / C.res().getScaledHeight();'''

# Add size recording
old_size = '''                GL11.glPopMatrix();

                if (canDrag()) {'''
new_size = '''                GL11.glPopMatrix();
                draggable.width = size[0];
                draggable.height = size[1];

                if (canDrag()) {'''

code = code.replace(old_render, new_render)
code = code.replace(old_drag, new_drag)
code = code.replace(old_size, new_size)

with codecs.open('src/main/java/com/github/scoliossis/utils/render/draggable/DraggableRenderer.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
