package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.Main;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.draggable.Draggable;
import com.github.scoliossis.utils.tenacity.render.GradientUtil;
import java.awt.Color;

@RegisterModule(
        name = "HUD",
        description = "Heads up display for client information.",
        category = Category.CLIENT
)
public class HUD extends Module {
    private static final String CLIENT_NAME = "Tenacity"; // "Copy all visuals from tenacity source code completely"

    public static Draggable coolwareWatermark = new Draggable(
            "CoolWareWatermark",
            () -> {
                Color[] theme = ThemeModule.getThemeColours();
                Color color1 = theme[0];
                Color color2 = theme.length > 1 ? theme[1] : theme[0];

                float width = FontUtil.getStringWidth(CLIENT_NAME, 40);
                float height = FontUtil.getFontHeight(40);

                GradientUtil.applyGradientHorizontal(0, 0, width, height, 1, color1, color2, () -> {
                    FontUtil.drawString(CLIENT_NAME, 0, 0, 40, Color.WHITE, true);
                });
                
                String extraText = " | " + C.mc.getDebugFPS() + "fps";
                FontUtil.drawString(extraText, width, height / 2f - FontUtil.getFontHeight(20) / 2f, 20, Color.WHITE, true);

                return new double[]{width + FontUtil.getStringWidth(extraText, 20), height};
            },
            e -> ModuleManager.isEnabled(HUD.class),
            e -> true
    );

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}
