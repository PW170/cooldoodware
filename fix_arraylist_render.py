import codecs

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Add smooth max width variable
code = code.replace(
    'private static final HashMap<Module, DecelerateAnimation> moduleAnimations = new HashMap<>();',
    '''private static final HashMap<Module, DecelerateAnimation> moduleAnimations = new HashMap<>();
    private static float animatedMaxWidth = 0;'''
)

# Replace max width logic inside render
old_logic = '''                float y = 0;
                float maxWidth = 0;

                Color[] theme = ThemeModule.getThemeColours();

                int index = 0;
                for (Module m : activeModules) {
                    DecelerateAnimation anim = moduleAnimations.computeIfAbsent(m, k -> new DecelerateAnimation(250, 1));
                    anim.setDirection(m.isEnabled() && !m.hide ? Direction.FORWARDS : Direction.BACKWARDS);
                    
                    float scale = (float) anim.getOutput().floatValue();
                    if (scale <= 0.01f) continue;

                    String text = m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " \u00a77" + m.arrayListExtraInfo() : "");
                    float width = FontUtil.getStringWidth(text, size);
                    float height = FontUtil.getFontHeight(size) + 4;
                    
                    if (width > maxWidth) maxWidth = width;

                    float x = maxWidth - (width * scale);'''

new_logic = '''                float y = 0;
                float targetMaxWidth = 0;

                for (Module m : activeModules) {
                    DecelerateAnimation anim = moduleAnimations.computeIfAbsent(m, k -> new DecelerateAnimation(250, 1));
                    anim.setDirection(m.isEnabled() && !m.hide ? Direction.FORWARDS : Direction.BACKWARDS);
                    float scale = (float) anim.getOutput().floatValue();
                    if (scale > 0.01f) {
                        String text = m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " \u00a77" + m.arrayListExtraInfo() : "");
                        float width = FontUtil.getStringWidth(text, size);
                        if (width > targetMaxWidth) targetMaxWidth = width;
                    }
                }
                
                // Animate max width so the arraylist bounding box smoothly expands/shrinks instead of instantly snapping
                float diff = targetMaxWidth - animatedMaxWidth;
                animatedMaxWidth += diff * 0.1f; // Smooth approach
                // clamp very close values
                if (Math.abs(diff) < 0.1f) animatedMaxWidth = targetMaxWidth;
                
                float maxWidth = animatedMaxWidth;

                Color[] theme = ThemeModule.getThemeColours();

                int index = 0;
                for (Module m : activeModules) {
                    DecelerateAnimation anim = moduleAnimations.get(m);
                    float scale = (float) anim.getOutput().floatValue();
                    if (scale <= 0.01f) continue;

                    String text = m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " \u00a77" + m.arrayListExtraInfo() : "");
                    float width = FontUtil.getStringWidth(text, size);
                    float height = FontUtil.getFontHeight(size) + 4;
                    
                    float x = maxWidth - (width * scale);'''

code = code.replace(old_logic, new_logic)

# Replace the static block
code = code.replace(
    'static { arraylistDraggable.x = 0.85; arraylistDraggable.y = 0.05; }',
    'static { arraylistDraggable.anchor = Draggable.Anchor.RIGHT; arraylistDraggable.x = 0.99; arraylistDraggable.y = 0.05; }'
)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
