package com.github.cooldood.screens.ClickGUI;

import com.github.cooldood.modules.Module;
import com.github.cooldood.modules.SubModule;
import com.github.cooldood.modules.SubCategory;
import com.github.cooldood.utils.client.KeybindHandler;
import com.github.cooldood.utils.client.ScreenUtil;
import com.github.cooldood.utils.render.FontUtil;
import com.github.cooldood.utils.render.RenderUtil;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import java.awt.Color;

public class ModuleRenderer {
    private final int MODULE_HEIGHT = 29;

    public static String moduleName(Module module) {
        String name = module.getAnnotation().name();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public float render(Module module, int mouseX, int mouseY, float currentY) {
        float x = ClickGUIScreen.BASE_X;
        float y = currentY;
        float w = ClickGUIScreen.GUI_TAB_WIDTH;
        float h = MODULE_HEIGHT;

        float totalHeight = h;

        boolean isHovered = ScreenUtil.isMouseOver(module.getAnnotation().category().renderX + x, module.getAnnotation().category().renderY + ClickGUIScreen.categoryRenderer.CATEGORY_HEIGHT + module.getAnnotation().category().renderScroll + y, w, h, mouseX, mouseY);

        if (isHovered) {
            ClickGUIScreen.moduleHovered = module;
            if (ClickGUIScreen.mouseButton == 0) {
                module.toggle();
                ClickGUIScreen.mouseButton = -1;
            } else if (ClickGUIScreen.mouseButton == 1) {
                module.setOpen(!module.isOpen());
                ClickGUIScreen.mouseButton = -1;
            } else if (ClickGUIScreen.mouseButton == 2) {
                KeybindHandler.listeningModule = module;
                ClickGUIScreen.mouseButton = -1;
            }
        }

        String name = moduleName(module);
        float textY = y + h / 2f - FontUtil.getFontHeight(ClickGUIScreen.fontSize + 1) / 2f + 1f;

        Color textColor = module.isEnabled() ? ClickGUIScreen.COL_BLUE : new Color(200, 200, 200);
        FontUtil.drawString(name, x + 8, textY, ClickGUIScreen.fontSize + 1, textColor, false);

        if (!module.getChildren().isEmpty()) {
            String arrow = module.isOpen() ? "▼" : "▲";
            float arrowX = x + w - FontUtil.getStringWidth(arrow, ClickGUIScreen.fontSize - 1) - 10;
            FontUtil.drawString(arrow, arrowX, textY + 1, ClickGUIScreen.fontSize - 1, new Color(150, 150, 150), false);
        }

        String keybindName = KeybindHandler.listeningModule == module
                ? "[...]"
                : module.getKeybind() != -1 ? "[" + Keyboard.getKeyName(module.getKeybind()) + "]" : "";

        if (!keybindName.isEmpty()) {
            float kbW = FontUtil.getStringWidth(keybindName, ClickGUIScreen.fontSize - 1);
            float kbX = x + w - 24 - kbW; // To the left of the arrow
            FontUtil.drawString(keybindName, kbX, textY, ClickGUIScreen.fontSize - 1, new Color(150, 150, 150), false);
        }

        if (module.isOpen()) {
            for (SubModule subModule : module.getChildren()) {
                if (!subModule.shouldRender()) continue;
                
                Class<?> type = subModule.getField().getType();
                float subH = 22; // default submod height
                if (type == SubCategory.class) subH = 24;
                if (type == int.class || type == double.class || type == float.class || type == long.class) subH = 25;
                
                float subY = currentY + totalHeight;
                
                boolean subHovered = ScreenUtil.isMouseOver(module.getAnnotation().category().renderX + x, module.getAnnotation().category().renderY + ClickGUIScreen.categoryRenderer.CATEGORY_HEIGHT + module.getAnnotation().category().renderScroll + subY, w, subH, mouseX, mouseY);
                if (subHovered) {
                    ClickGUIScreen.subModuleHovered = subModule;
                }

                String subName = subModule.getAnnotation().name();
                float subTextY = subY + subH / 2f - FontUtil.getFontHeight(8) / 2f + 1f;

                if (type == boolean.class || type == Boolean.class) {
                    FontUtil.drawString(subName, x + 16, subTextY, 9, new Color(200, 200, 200), false);
                    boolean val = (Boolean) subModule.get();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        subModule.set(!val);
                        ClickGUIScreen.mouseButton = -1;
                    }
                    Color checkColor = val ? ClickGUIScreen.COL_BLUE : new Color(20, 20, 20);
                    // Small circular toggle on right (using rounded rect)
                    RenderUtil.drawRoundedRect(x + w - 30, subY + subH/2f - 6, 12, 12, 6, checkColor);
                    RenderUtil.drawRoundedRectOutline(x + w - 30, subY + subH/2f - 6, 12, 12, 6, 1, new Color(40,40,40));
                } else if (type.isEnum()) {
                    FontUtil.drawString(subName, x + 16, subTextY, 9, new Color(200, 200, 200), false);
                    String val = subModule.get().toString();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        Enum<?> current = (Enum<?>) subModule.get();
                        Enum<?>[] constants = current.getClass().getEnumConstants();
                        int next = (current.ordinal() + 1) % constants.length;
                        subModule.set(constants[next]);
                        ClickGUIScreen.mouseButton = -1;
                    }
                    FontUtil.drawString(val + " >", x + w - FontUtil.getStringWidth(val + " >", 9) - 16, subTextY, 9, Color.WHITE, false);
                } else if (type == SubCategory.class) {
                    SubCategory cat = (SubCategory) subModule.get();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        cat.open = !cat.open;
                        ClickGUIScreen.mouseButton = -1;
                    }
                    // Centered section divider
                    float textW = FontUtil.getStringWidth(subName, 9);
                    float centerX = x + w / 2f;
                    FontUtil.drawString(subName, centerX - textW/2f, subTextY, 9, Color.WHITE, false);
                    
                    // Lines extending to sides
                    RenderUtil.drawRect(x + 16, subY + subH/2f, (w - textW)/2f - 24, 1, new Color(60, 60, 60));
                    RenderUtil.drawRect(centerX + textW/2f + 8, subY + subH/2f, (w - textW)/2f - 24, 1, new Color(60, 60, 60));
                } else if (type == int.class || type == double.class || type == float.class || type == long.class) {
                    float min = (float) subModule.getAnnotation().min();
                    float max = (float) subModule.getAnnotation().max();
                    float val = Float.parseFloat(subModule.get().toString());
                    
                    FontUtil.drawString(subName, x + 16, subY + 4, 9, new Color(200, 200, 200), false);
                    
                    String valStr = String.valueOf(subModule.get());
                    if (valStr.endsWith(".0")) valStr = valStr.replace(".0", "");
                    FontUtil.drawString(valStr, x + w - FontUtil.getStringWidth(valStr, 8) - 16, subY + 4, 8, new Color(180,180,180), false);

                    float sliderW = w - 32;
                    float sliderX = x + 16;
                    float sliderY = subY + 16;
                    
                    RenderUtil.drawRect(sliderX, sliderY, sliderW, 2, new Color(10, 10, 10));
                    
                    float pct = (val - min) / (max - min);
                    RenderUtil.drawRect(sliderX, sliderY, sliderW * pct, 2, ClickGUIScreen.COL_BLUE);
                    // Circular handle
                    RenderUtil.drawRoundedRect(sliderX + (sliderW * pct) - 3, sliderY - 2, 6, 6, 3, ClickGUIScreen.COL_BLUE);
                    
                    if (subHovered && ClickGUIScreen.leftMouseDown) {
                        ClickGUIScreen.currentSubModule = subModule;
                    }
                    if (ClickGUIScreen.currentSubModule == subModule) {
                        float relativeX = (mouseX - (module.getAnnotation().category().renderX + sliderX));
                        float newPct = MathHelper.clamp_float(relativeX / sliderW, 0, 1);
                        double newVal = min + (max - min) * newPct;
                        
                        double increment = subModule.getAnnotation().increment();
                        if ((type == long.class || type == int.class) && increment < 1) increment = 1;
                        newVal = Math.round(newVal / increment) * increment;
                        
                        subModule.set(newVal);
                    }
                    
                } else if (type == Color.class) {
                    FontUtil.drawString(subName, x + 16, subTextY, 9, new Color(200, 200, 200), false);
                    RenderUtil.drawRect(x + w - 26, subY + subH/2f - 5, 10, 10, (Color) subModule.get());
                }

                totalHeight += subH;
            }
        }
        return totalHeight;
    }
}
