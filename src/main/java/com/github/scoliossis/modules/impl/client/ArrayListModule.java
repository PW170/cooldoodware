package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.github.scoliossis.utils.render.draggable.Draggable;
import com.github.scoliossis.utils.tenacity.animations.impl.DecelerateAnimation;
import com.github.scoliossis.utils.tenacity.animations.Direction;
import com.github.scoliossis.utils.tenacity.render.ColorUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RegisterModule(
        name = "Array List",
        description = "Shows enabled modules.",
        category = Category.CLIENT,
        enabledByDefault = true
)
public class ArrayListModule extends Module {

    @RegisterSubModule(name = "Font Size", min = 5, max = 30, increment = 2)
    public static double fontSize = 20;

    private static final HashMap<Module, DecelerateAnimation> moduleAnimations = new HashMap<>();
    private static float animatedMaxWidth = 0;

    

    public static Draggable arraylistDraggable = new Draggable(
            "ArrayListWidget2",
            () -> {
                int size = (int) fontSize;

                List<Module> activeModules = new ArrayList<>(ModuleManager.getModules());
                activeModules.sort(Comparator.comparingDouble(m -> -FontUtil.getStringWidth(m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " " + m.arrayListExtraInfo() : ""), size)));

                float y = 0;
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
                    float height = FontUtil.getFontHeight(size) + 1;
                    
                    float x = maxWidth - (width * scale);

                    // Tenacity style background
                    RenderUtil.drawRect(x - 2, y, width + 4, height, new Color(0, 0, 0, 120));
                    
                    Color c1 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
                    
                    // Double strike to make it bold
                    FontUtil.drawString(text, x + 0.5f, y + 1, size, c1, true);
                    FontUtil.drawString(text, x, y + 1, size, c1, false);

                    y += height * scale;
                    index++;
                }

                return new double[]{maxWidth + 4, y};
            },
            e -> ModuleManager.isEnabled(ArrayListModule.class),
            e -> true
    );

    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    static { arraylistDraggable.anchor = Draggable.Anchor.RIGHT; arraylistDraggable.x = 0.99; arraylistDraggable.y = 0.05; }
}
