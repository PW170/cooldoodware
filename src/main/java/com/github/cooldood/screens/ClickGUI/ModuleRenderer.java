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
    private final int MODULE_HEIGHT = 16;

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
        float textY = y + h / 2f - FontUtil.getFontHeight(ClickGUIScreen.fontSize) / 2f + 1f;

        Color textColor = module.isEnabled() ? ClickGUIScreen.COL_BLUE : new Color(200, 200, 200);
        FontUtil.drawString(name, x + 6, textY, ClickGUIScreen.fontSize, textColor, false);

        if (!module.getChildren().isEmpty()) {
            String arrow = module.isOpen() ? "v" : ">";
            float arrowX = x + w - FontUtil.getStringWidth(arrow, ClickGUIScreen.fontSize) - 6;
            FontUtil.drawString(arrow, arrowX, textY, ClickGUIScreen.fontSize, new Color(200, 200, 200), false);
        }

        String keybindName = KeybindHandler.listeningModule == module
                ? "[...]"
                : module.getKeybind() != -1 ? "[" + Keyboard.getKeyName(module.getKeybind()) + "]" : "";

        if (!keybindName.isEmpty()) {
            float kbW = FontUtil.getStringWidth(keybindName, ClickGUIScreen.fontSize - 1);
            float kbX = x + w - 16 - kbW; // To the left of the arrow
            FontUtil.drawString(keybindName, kbX, textY, ClickGUIScreen.fontSize - 1, new Color(150, 150, 150), false);
        }

        if (module.isOpen()) {
            for (SubModule subModule : module.getChildren()) {
                if (!subModule.shouldRender()) continue;
                
                float subH = 16;
                float subY = currentY + totalHeight;
                
                boolean subHovered = ScreenUtil.isMouseOver(module.getAnnotation().category().renderX + x, module.getAnnotation().category().renderY + ClickGUIScreen.categoryRenderer.CATEGORY_HEIGHT + module.getAnnotation().category().renderScroll + subY, w, subH, mouseX, mouseY);
                if (subHovered) {
                    ClickGUIScreen.subModuleHovered = subModule;
                }

                String subName = subModule.getAnnotation().name();
                float subTextY = subY + subH / 2f - FontUtil.getFontHeight(8) / 2f + 1f;
                FontUtil.drawString(subName, x + 8, subTextY, 8, new Color(180, 180, 180), false);

                Class<?> type = subModule.getField().getType();
                if (type == boolean.class || type == Boolean.class) {
                    boolean val = (Boolean) subModule.get();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        subModule.set(!val);
                        ClickGUIScreen.mouseButton = -1;
                    }
                    Color checkColor = val ? ClickGUIScreen.COL_BLUE : new Color(50, 50, 50);
                    RenderUtil.drawRect(x + w - 16, subY + 3, 10, 10, checkColor);
                } else if (type.isEnum()) {
                    String val = subModule.get().toString();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        Enum<?> current = (Enum<?>) subModule.get();
                        Enum<?>[] constants = current.getClass().getEnumConstants();
                        int next = (current.ordinal() + 1) % constants.length;
                        subModule.set(constants[next]);
                        ClickGUIScreen.mouseButton = -1;
                    }
                    FontUtil.drawString(val, x + w - FontUtil.getStringWidth(val, 8) - 8, subTextY, 8, Color.WHITE, false);
                } else if (type == SubCategory.class) {
                    SubCategory cat = (SubCategory) subModule.get();
                    if (subHovered && ClickGUIScreen.mouseButton == 0) {
                        cat.open = !cat.open;
                        ClickGUIScreen.mouseButton = -1;
                    }
                    String arrow = cat.open ? "v" : ">";
                    FontUtil.drawString(arrow, x + w - FontUtil.getStringWidth(arrow, 8) - 8, subTextY, 8, new Color(150, 150, 150), false);
                } else if (type == int.class || type == double.class || type == float.class || type == long.class) {
                    float min = (float) subModule.getAnnotation().min();
                    float max = (float) subModule.getAnnotation().max();
                    float val = Float.parseFloat(subModule.get().toString());
                    
                    float sliderW = 40;
                    float sliderX = x + w - sliderW - 8;
                    float sliderY = subY + subH / 2f - 1;
                    
                    RenderUtil.drawRect(sliderX, sliderY, sliderW, 2, new Color(50, 50, 50));
                    
                    float pct = (val - min) / (max - min);
                    RenderUtil.drawRect(sliderX, sliderY, sliderW * pct, 2, ClickGUIScreen.COL_BLUE);
                    
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
                    
                    String valStr = String.valueOf(subModule.get());
                    if (valStr.endsWith(".0")) valStr = valStr.replace(".0", "");
                    FontUtil.drawString(valStr, sliderX - FontUtil.getStringWidth(valStr, 7) - 4, subY + subH / 2f - FontUtil.getFontHeight(7) / 2f + 1f, 7, Color.WHITE, false);
                } else if (type == Color.class) {
                    RenderUtil.drawRect(x + w - 16, subY + 3, 10, 10, (Color) subModule.get());
                }

                totalHeight += subH;
            }
        }
        return totalHeight;
    }
}
