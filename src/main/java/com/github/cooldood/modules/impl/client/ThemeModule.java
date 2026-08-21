package com.github.cooldood.modules.impl.client;

import com.github.cooldood.modules.*;
import com.github.cooldood.utils.render.FontUtil;

import java.awt.*;

@RegisterModule(
        name = "Theme",
        description = "Provides Theme functionality for the client.",
        category = Category.CLIENT,
        enabledByDefault = true
)
public class ThemeModule extends Module {

    public static boolean globalFont = false;
    public static int minecraftFontSize = 10;

    public static boolean shouldUseCustomFont() {
        return false;
    }

    @RegisterSubModule(name = "Color 1")
    public static Color customColour1 = new Color(0, 150, 255);

    @RegisterSubModule(name = "Color 2")
    public static Color customColour2 = new Color(0, 255, 150);

    public static Color[] getThemeColours() {
        return new Color[]{customColour1, customColour2};
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
