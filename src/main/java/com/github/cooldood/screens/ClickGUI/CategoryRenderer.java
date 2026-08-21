package com.github.cooldood.screens.ClickGUI;

import com.github.cooldood.modules.Category;
import com.github.cooldood.utils.client.ScreenUtil;
import com.github.cooldood.utils.render.FontUtil;
import com.github.cooldood.utils.render.RenderUtil;
import java.awt.Color;

public class CategoryRenderer {
    public final int CATEGORY_HEIGHT = 20;
    protected Category currentDraggingCategory = null;
    private float categoryDragStartX = -1;
    private float categoryDragStartY = -1;

    public void render(Category category) {
        float x = ClickGUIScreen.BASE_X;
        float y = ClickGUIScreen.BASE_Y;
        float w = ClickGUIScreen.GUI_TAB_WIDTH;
        float h = CATEGORY_HEIGHT;

        RenderUtil.drawRect(x, y, w, h, ClickGUIScreen.COL_BLUE);

        String name = category.name().replaceAll("_", " ");
        String categoryName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        float textH = FontUtil.getFontHeight(ClickGUIScreen.fontSize);
        float textY = y + h / 2f - textH / 2f + 1f;

        // Draw an icon placeholder or just the text
        String icon = "";
        switch (categoryName) {
            case "Combat": icon = "b"; break;
            case "Movement": icon = "m"; break;
            case "Player": icon = "p"; break;
            case "Exploits": icon = "e"; break;
            case "Visuals": icon = "v"; break;
            case "Misc": icon = "c"; break;
            case "Configs": icon = "s"; break;
        }

        FontUtil.drawString(icon + "  " + categoryName, x + 6, textY, ClickGUIScreen.fontSize, Color.WHITE, false);
    }

    public void handleMouse(Category category, int mouseX, int mouseY) {
        float renderX = category.renderX;
        float renderY = category.renderY;
        float width = ClickGUIScreen.GUI_TAB_WIDTH;
        float height = CATEGORY_HEIGHT;

        if (ScreenUtil.isMouseOver(renderX, renderY, width, height, mouseX, mouseY) && ClickGUIScreen.leftMouseDown && currentDraggingCategory == null) {
            currentDraggingCategory = category;
            categoryDragStartX = renderX - mouseX;
            categoryDragStartY = renderY - mouseY;
        }

        if (currentDraggingCategory == category) {
            if (ClickGUIScreen.leftMouseDown) {
                category.posX = mouseX + categoryDragStartX;
                category.posY = mouseY + categoryDragStartY;
            } else {
                currentDraggingCategory = null;
            }
        }
    }
}
