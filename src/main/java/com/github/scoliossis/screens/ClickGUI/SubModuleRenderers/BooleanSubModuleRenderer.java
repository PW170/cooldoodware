package com.github.scoliossis.screens.ClickGUI.SubModuleRenderers;

import com.github.scoliossis.modules.SubModule;
import com.github.scoliossis.modules.impl.client.ClickGUIModule;
import com.github.scoliossis.screens.ClickGUI.ClickGUIScreen;
import com.github.scoliossis.screens.ClickGUI.SubModuleRenderer;
import com.github.scoliossis.utils.client.ScreenUtil;
import com.github.scoliossis.utils.render.EasingUtil;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BooleanSubModuleRenderer extends SubModuleRenderer {
    private static final int TOGGLE_W = 22;
    private static final int TOGGLE_H = 10;

    @Override
    public void handleMouse(int mouseX, int mouseY, SubModule subModule) {
        if (ScreenUtil.isMouseOver(ClickGUIScreen.BASE_X, ClickGUIScreen.BASE_Y,
                ClickGUIScreen.GUI_TAB_WIDTH, SUBMODULE_HEIGHT, mouseX, mouseY)) {
            if (ClickGUIScreen.mouseButton == 0) {
                boolean currentState = (boolean) subModule.get();
                subModule.set(!currentState);
                EasingUtil.addAnimation(
                        subModule.getUniqueKey(),
                        !currentState ? ClickGUIModule.openAnimationLength : ClickGUIModule.closeAnimationLength,
                        !currentState,
                        !currentState ? ClickGUIModule.openAnimation : ClickGUIModule.closeAnimation
                );
            }
            ClickGUIScreen.mouseButton = -1;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, SubModule subModule) {
        super.render(mouseX, mouseY, subModule);

        boolean value = (boolean) subModule.get();
        Color catColor = subModule.getParentModule().getAnnotation().category().color;

        // Draw sub-module card
        ClickGUIScreen.drawSubModuleCard(SUBMODULE_HEIGHT);

        // Label
        FontUtil.drawString(subModule.getAnnotation().name(),
                getSubModuleTextX(), getSubModuleTextY(),
                ClickGUIScreen.fontSize, Color.WHITE, true);

        // Toggle pill
        float toggleX = ClickGUIScreen.BASE_X + ClickGUIScreen.GUI_TAB_WIDTH
                - ClickGUIScreen.PANEL_PADDING - 4 - TOGGLE_W - 4;
        float toggleY = ClickGUIScreen.BASE_Y + SUBMODULE_HEIGHT / 2f - TOGGLE_H / 2f;

        // Track
        Color trackBg = value
                ? new Color(catColor.getRed(), catColor.getGreen(), catColor.getBlue(), 200)
                : new Color(55, 55, 70, 255);
        RenderUtil.drawRoundedRect(toggleX, toggleY, TOGGLE_W, TOGGLE_H, TOGGLE_H / 2f, trackBg);

        // Thumb
        float thumbSize = TOGGLE_H - 2;
        float thumbX    = value ? toggleX + TOGGLE_W - thumbSize - 1 : toggleX + 1;
        float thumbY    = toggleY + 1;
        RenderUtil.drawRoundedRect(thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2f, Color.WHITE);

        GL11.glTranslated(0, SUBMODULE_HEIGHT + 2, 0);
    }
}
