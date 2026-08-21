package com.github.cooldood.modules.impl.client;

import com.github.cooldood.modules.*;
import com.github.cooldood.utils.client.C;
import com.github.cooldood.utils.render.FontUtil;
import com.github.cooldood.utils.render.draggable.Draggable;
import com.github.cooldood.utils.tenacity.render.GradientUtil;
import com.github.cooldood.utils.tenacity.render.ColorUtil;
import java.awt.Color;
import org.lwjgl.opengl.GL11;

@RegisterModule(
        name = "HUD",
        description = "Heads up display for client information.",
        category = Category.CLIENT
)
public class HUD extends Module {

    
    public static String CLIENT_NAME = "Coolware";

    @RegisterSubModule(name = "Watermark Size", min = 5, max = 50, increment = 2)
    public static double watermarkSize = 30;

    private static long lastPingTime = 0;
    private static int cachedPing = 0;

    private static int getPing() {
        if (C.mc.isSingleplayer()) return 0;
        long now = System.currentTimeMillis();
        if (now - lastPingTime > 10000) {
            lastPingTime = now;
            if (C.mc.getNetHandler() != null && C.p() != null) {
                net.minecraft.client.network.NetworkPlayerInfo playerInfo = C.mc.getNetHandler().getPlayerInfo(C.p().getUniqueID());
                if (playerInfo != null) {
                    cachedPing = playerInfo.getResponseTime();
                }
            }
        }
        return cachedPing;
    }


    public static Draggable coolwareWatermark = new Draggable(
            "CoolWareWatermark",
            () -> {
                Color[] theme = ThemeModule.getThemeColours();
                Color color1 = theme[0];
                Color color2 = theme.length > 1 ? theme[1] : theme[0];

                int size = (int) watermarkSize;

                float width = FontUtil.getStringWidth(CLIENT_NAME, size);
                float height = FontUtil.getFontHeight(size);

                int extraSize = (int) (size / 2.0);
                  String extraText = " | " + C.mc.getDebugFPS() + "fps | " + getPing() + "ms";

                  

                  GradientUtil.applyGradientHorizontal(0, 0, width, height, 1, color1, color2, () -> {
                      FontUtil.drawString(CLIENT_NAME, 0, 0, size, Color.WHITE, !com.github.cooldood.utils.render.draggable.DraggableRenderer.isBloom);
                  });
                FontUtil.drawString(extraText, width, height / 2f - FontUtil.getFontHeight(extraSize) / 2f, extraSize, Color.WHITE, !com.github.cooldood.utils.render.draggable.DraggableRenderer.isBloom);

                return new double[]{width + FontUtil.getStringWidth(extraText, extraSize), height};
            },
            e -> ModuleManager.isEnabled(HUD.class),
            e -> true
    );

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
