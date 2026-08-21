package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RenderTickEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.github.scoliossis.utils.tenacity.animations.impl.DecelerateAnimation;
import com.github.scoliossis.utils.tenacity.animations.Direction;
import com.github.scoliossis.utils.tenacity.render.ColorUtil;
import net.minecraft.client.gui.ScaledResolution;
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

    @RegisterSubModule(name = "Font Size", min = 10, max = 40, increment = 2)
    public static double fontSize = 20;

    private static final HashMap<Module, DecelerateAnimation> moduleAnimations = new HashMap<>();

    @SubscribeEvent
    public static void onRenderTick(RenderTickEvent event) {
        if (!ModuleManager.isEnabled(ArrayListModule.class)) return;

        int size = (int) fontSize;

        List<Module> activeModules = new ArrayList<>(ModuleManager.getModules());
        activeModules.sort(Comparator.comparingDouble(m -> -FontUtil.getStringWidth(m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " " + m.arrayListExtraInfo() : ""), size)));

        ScaledResolution sr = new ScaledResolution(C.mc);
        float y = 5;

        Color[] theme = ThemeModule.getThemeColours();

        int index = 0;
        for (Module m : activeModules) {
            DecelerateAnimation anim = moduleAnimations.computeIfAbsent(m, k -> new DecelerateAnimation(250, 1));
            anim.setDirection(m.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            
            float scale = (float) anim.getOutput().floatValue();
            if (scale <= 0.01f) continue;

            String text = m.getAnnotation().name() + (!m.arrayListExtraInfo().isEmpty() ? " §7" + m.arrayListExtraInfo() : "");
            float width = FontUtil.getStringWidth(text, size);
            float height = FontUtil.getFontHeight(size) + 4;
            float x = sr.getScaledWidth() - (width * scale) - 5;

            // Tenacity style background
            RenderUtil.drawRect(x - 2, y, width + 4, height, new Color(0, 0, 0, 120));
            
            // Side bar
            Color c1 = ColorUtil.interpolateColorsBackAndForth(15, index * 20, theme[0], theme[theme.length > 1 ? 1 : 0], false);
            RenderUtil.drawRect(x + width, y, 2, height, c1);
            
            FontUtil.drawString(text, x, y + 2, size, c1, true);

            y += height * scale;
            index++;
        }
    }

    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
}
