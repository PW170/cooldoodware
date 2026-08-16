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
        description = "Displays text on the screen with various degrees of helpfulness",
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

    public static Draggable coolwareWatermark = new Draggable(
            "CoolWareWatermark",
            () -> {
                FontUtil.drawStringFade(CLIENT_NAME, 0, 0, fontSize, ThemeModule.getThemeColours(), watermarkFadeSpeed, watermarkFadeSpread, true);

                return new double[] {FontUtil.getStringWidth(CLIENT_NAME, fontSize), FontUtil.getFontHeight(fontSize)};
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
