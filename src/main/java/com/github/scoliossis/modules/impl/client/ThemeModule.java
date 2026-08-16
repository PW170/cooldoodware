package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ClientTickEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;

import java.awt.*;

// Internal helper — no longer a user-facing module (hidden = true)
@RegisterModule(
        name = "Theme",
        description = "Internal font/color helper",
        category = Category.CLIENT,
        enabledByDefault = true
)
public class ThemeModule extends Module {

    public static boolean globalFont = false;
    public static int minecraftFontSize = 10;

    // Gray colour — used by anything that previously referenced theme colours
    private static final Color[] GRAY_COLOURS = {
            new Color(150, 150, 150),
            new Color(120, 120, 120)
    };

    public static Color customColour1 = new Color(150, 150, 150);
    public static Color customColour2 = new Color(120, 120, 120);

    public static boolean shouldUseCustomFont() {
        return false;
    }

    @SubscribeEvent
    public static void onClientTickEvent(ClientTickEvent event) {
        FontUtil.setCurrentFont(FontUtil.Fonts.DM_Sans_Bold);
    }

    public static Color[] getThemeColours() {
        return GRAY_COLOURS;
    }

    @Override
    protected void onEnable() {
        FontUtil.setCurrentFont(FontUtil.Fonts.DM_Sans_Bold);
    }

    @Override
    protected void onDisable() {
        // keep always enabled
        ModuleManager.getModule(ThemeModule.class).setEnabled(true);
    }
}
