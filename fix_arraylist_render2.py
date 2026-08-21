import codecs
import re

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'r', 'utf-8') as f:
    code = f.read()

# Fix the A 7 typo
code = code.replace('" A 7"', '" \u00a77"')

old_logic_pattern = r'float y = 0;\s*float maxWidth = 0;\s*Color\[\] theme = ThemeModule\.getThemeColours\(\);\s*int index = 0;\s*for \(Module m : activeModules\) \{\s*DecelerateAnimation anim = moduleAnimations\.computeIfAbsent\(m, k -> new DecelerateAnimation\(250, 1\)\);\s*anim\.setDirection\(m\.isEnabled\(\) && !m\.hide \? Direction\.FORWARDS : Direction\.BACKWARDS\);\s*float scale = \(float\) anim\.getOutput\(\)\.floatValue\(\);\s*if \(scale <= 0\.01f\) continue;\s*String text = m\.getAnnotation\(\)\.name\(\) \+ \(\!m\.arrayListExtraInfo\(\)\.isEmpty\(\) \? " \\u00a77" \+ m\.arrayListExtraInfo\(\) : ""\);\s*float width = FontUtil\.getStringWidth\(text, size\);\s*float height = FontUtil\.getFontHeight\(size\) \+ 4;\s*if \(width > maxWidth\) maxWidth = width;\s*float x = maxWidth - \(width \* scale\);'

new_logic = r'''float y = 0;
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
                
                float diff = targetMaxWidth - animatedMaxWidth;
                animatedMaxWidth += diff * 0.1f;
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

def replacer(match):
    return new_logic

code = re.sub(old_logic_pattern, replacer, code, flags=re.MULTILINE)

with codecs.open('src/main/java/com/github/scoliossis/modules/impl/client/ArrayListModule.java', 'w', 'utf-8') as f:
    f.write(code)
print("done")
