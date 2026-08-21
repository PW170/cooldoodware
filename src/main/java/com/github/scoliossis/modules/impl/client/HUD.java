package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.Main;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.github.scoliossis.utils.render.draggable.Draggable;
import net.minecraft.client.Minecraft;

import java.awt.*;

@RegisterModule(
        name = "HUD",
        description = "Heads up display for client information.",
        category = Category.CLIENT
)
public class HUD extends Module {

    @RegisterSubModule(name = "Font Size", min = 10, max = 40)
    public static int fontSize = 30;
    @RegisterSubModule(name = "Fade Speed", min = 0.1, max = 10)
    public static double watermarkFadeSpeed = 5f;
    @RegisterSubModule(name = "Fade Spread", min = 0.1, max = 10)
    public static double watermarkFadeSpread = 5f;

    private static final String CLIENT_NAME = Main.MOD_NAME.split(" ")[0];

    // Claymorphic palette — matches ClickGUI exactly
    private static final Color COL_PANEL_BG      = new Color(26, 26, 34, 245);
    private static final Color COL_SHADOW         = new Color(0, 0, 0, 80);
    private static final Color COL_RIM_HIGHLIGHT  = new Color(255, 255, 255, 22);

    public static Draggable coolwareWatermark = new Draggable(
            "CoolWareWatermark",
            () -> {
                float padH = 10f;
                float padV = 6f;

                float textW = FontUtil.getStringWidth(CLIENT_NAME, fontSize);
                float textH = FontUtil.getFontHeight(fontSize);

                float cardW = textW + padH * 2;
                float cardH = textH + padV * 2;

                // Drop shadow (offset 2px down-right like ClickGUI panels)
                RenderUtil.drawRoundedRect(2, 2, cardW, cardH, 8f, COL_SHADOW);
                // Main claymorphic body
                RenderUtil.drawRoundedRect(0, 0, cardW, cardH, 8f, COL_PANEL_BG);
                // Rim highlight — top inner edge catches light
                RenderUtil.drawRoundedRect(1, 1, cardW - 2, 2, 8f, COL_RIM_HIGHLIGHT);
                // Theme-coloured accent strip along the top (same as ClickGUI category headers)
                Color[] accentColors = ThemeModule.getThemeColours();
                Color accent = RenderUtil.getColorsFade(0, accentColors, 4f);
                RenderUtil.drawRoundedRect(0, 0, cardW, 2, 8f,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 160));

                // Client name with theme gradient fade
                FontUtil.drawStringFade(CLIENT_NAME, padH, padV, fontSize,
                        ThemeModule.getThemeColours(), watermarkFadeSpeed, watermarkFadeSpread, true);

                return new double[]{cardW, cardH};
            },
            e -> ModuleManager.isEnabled(HUD.class),
            e -> true
    );

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
