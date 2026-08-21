package com.github.cooldood.screens.ClickGUI;

import com.github.cooldood.modules.Category;
import com.github.cooldood.utils.client.ScreenUtil;
import com.github.cooldood.utils.render.FontUtil;
import com.github.cooldood.utils.render.RenderUtil;
import java.awt.Color;

public class CategoryRenderer {
    public final int CATEGORY_HEIGHT = 33;
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

        float textH = FontUtil.getFontHeight(ClickGUIScreen.fontSize + 2);
        float textY = y + h / 2f - textH / 2f + 1f;

        // Roughly 16-18px font for header
        FontUtil.drawString(categoryName, x + 30, textY, ClickGUIScreen.fontSize + 4, Color.WHITE, true);
        
        // Pseudo icon at left
        String icon = "";
        switch (categoryName) {
            case "Combat": icon = "⚔"; break;
            case "Movement": icon = "👟"; break;
            case "Player": icon = "👤"; break;
            case "Exploits": icon = "✨"; break;
            case "Visuals": icon = "👁"; break;
            case "Misc": icon = "⚙"; break;
            case "Client": icon = "💻"; break;
            default: icon = "·"; break;
        }
        FontUtil.drawString(icon, x + 8, textY, ClickGUIScreen.fontSize + 4, Color.WHITE, false);
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
