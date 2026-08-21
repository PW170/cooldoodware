package com.github.cooldood.screens.ClickGUI;

import com.github.cooldood.Main;
import com.github.cooldood.events.impl.KeyPressedEvent;
import com.github.cooldood.modules.Category;
import com.github.cooldood.modules.Module;
import com.github.cooldood.modules.ModuleManager;
import com.github.cooldood.modules.SubModule;
import com.github.cooldood.modules.impl.client.ClickGUIModule;
import com.github.cooldood.utils.client.C;
import com.github.cooldood.utils.client.KeybindHandler;
import com.github.cooldood.utils.client.ScreenUtil;
import com.github.cooldood.utils.render.FontUtil;
import com.github.cooldood.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {
    public static final int fontSize = 9;
    public static int GUI_TAB_WIDTH = 110;

    public static final int BASE_X = -GUI_TAB_WIDTH / 2;
    public static final int BASE_Y = 0;

    public static final Color COL_BLUE = new Color(0, 163, 255);
    public static final Color COL_BG = new Color(20, 20, 20, 220);

    public static float fpsMultiplier = 1;
    public static boolean leftMouseDown = false;
    public static int mouseButton = -1;

    public static Module moduleHovered = null;
    public static SubModule subModuleHovered = null;
    public static SubModule currentSubModule = null;

    public static final CategoryRenderer categoryRenderer = new CategoryRenderer();
    public static final ModuleRenderer moduleRenderer = new ModuleRenderer();

    @Override
    public void initGui() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!ModuleManager.isEnabled(ClickGUIModule.class)) {
            C.mc.displayGuiScreen(null);
            ClickGUIScreen.saveCategoryPositions();
            return;
        }

        fpsMultiplier = Math.max(Minecraft.getDebugFPS() * 0.1f, 2);
        int scrolledAmount = Mouse.getDWheel() / 5;

        GL11.glPushMatrix();

        List<Module> modules = ModuleManager.getModules();
        if (!modules.contains(moduleHovered)) moduleHovered = null;

        for (Category category : Category.values()) {
            GL11.glPushMatrix();

            List<Module> modulesInCategory = ModuleManager.getModulesByCategory(category, modules);

            category.renderX += (category.posX - category.renderX) / fpsMultiplier;
            if (Math.abs(category.renderX - category.posX) < 0.01) category.renderX = category.posX;

            category.renderY += (category.posY - category.renderY) / fpsMultiplier;
            if (Math.abs(category.renderY - category.posY) < 0.01) category.renderY = category.posY;

            GL11.glTranslated(category.renderX - BASE_X, category.renderY - BASE_Y, 0);

            categoryRenderer.handleMouse(category, mouseX, mouseY);
            categoryRenderer.render(category);

            RenderUtil.glScissor(BASE_X, BASE_Y + categoryRenderer.CATEGORY_HEIGHT,
                    GUI_TAB_WIDTH, C.res().getScaledHeight());

            category.renderScroll += (category.scroll - category.renderScroll) / fpsMultiplier;
            if (Math.abs(category.scroll - category.renderScroll) < 0.01) category.renderScroll = category.scroll;

            GL11.glTranslated(0, category.renderScroll, 0);
            GL11.glTranslated(0, categoryRenderer.CATEGORY_HEIGHT, 0);

            // Calculate total height first for background
            float totalHeight = 0;
            for (Module module : modulesInCategory) {
                float h = 16;
                if (module.isOpen()) {
                    for (SubModule subModule : module.getChildren()) {
                        if (subModule.shouldRender()) h += 16;
                    }
                }
                totalHeight += h;
            }

            RenderUtil.drawRect(BASE_X, BASE_Y, GUI_TAB_WIDTH, totalHeight, COL_BG);

            float currentY = BASE_Y;
            for (Module module : modulesInCategory) {
                float h = moduleRenderer.render(module, mouseX, mouseY, currentY);
                currentY += h;
            }

            if (ScreenUtil.isMouseOver(category.renderX, category.renderY + categoryRenderer.CATEGORY_HEIGHT, GUI_TAB_WIDTH, C.res().getScaledHeight(), mouseX, mouseY)) {
                category.scroll += scrolledAmount;
            }
            if (category.scroll > 0) category.scroll = 0;

            GL11.glDisable(GL11.GL_SCISSOR_TEST);;
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();

        if (moduleHovered != null && moduleHovered.getAnnotation().description() != null) {
            String desc = moduleHovered.getAnnotation().description();
            float bw = FontUtil.getStringWidth(desc, 8) + 8;
            float bh = FontUtil.getFontHeight(8) + 8;
            RenderUtil.drawRect(mouseX + 4, mouseY - bh - 4, bw, bh, new Color(0, 0, 0, 200));
            FontUtil.drawString(desc, mouseX + 8, mouseY - bh, 8, Color.WHITE, false);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (KeybindHandler.listeningModule != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                KeybindHandler.removeKeybind(KeybindHandler.listeningModule);
                KeybindHandler.listeningModule = null;
            } else KeybindHandler.onKeyPressed(new KeyPressedEvent(keyCode, true));
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            ModuleManager.setEnabled(ClickGUIModule.class, false);
        } else if (subModuleHovered != null && subModuleHovered.isSlider()) {
            double increment = subModuleHovered.getAnnotation().increment();
            if ((subModuleHovered.getField().getType() == long.class
                    || subModuleHovered.getField().getType() == int.class) && increment < 1) increment = 1;
            double value = Double.parseDouble(subModuleHovered.get().toString());
            if (keyCode == Keyboard.KEY_RIGHT) subModuleHovered.set(value + increment);
            if (keyCode == Keyboard.KEY_LEFT)  subModuleHovered.set(value - increment);
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        ClickGUIScreen.mouseButton = mouseButton;
        if (mouseButton == 0) leftMouseDown = true;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        ClickGUIScreen.mouseButton = -1;
        if (mouseButton == 0) {
            leftMouseDown = false;
            categoryRenderer.currentDraggingCategory = null;
            currentSubModule = null;
        }
    }

    private static final String categorySavingFile = Main.extraSavedFeaturesPath + "categoryPositions" + Main.configExtension;

    public static void saveCategoryPositions() {
        try {
            HashMap<String, float[]> posJSON = new HashMap<>();
            for (Category category : Category.values())
                posJSON.put(category.name(), new float[]{category.posX, category.posY});
            Files.createDirectories(Paths.get(Main.extraSavedFeaturesPath));
            Files.write(Paths.get(categorySavingFile), C.gson.toJson(posJSON).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadCategoryPositions() {
        try {
            if (Files.exists(Paths.get(categorySavingFile))) {
                String configFileText = FileUtils.readFileToString(new File(categorySavingFile));
                HashMap<String, ArrayList<Double>> posJSON = C.gson.fromJson(configFileText, HashMap.class);
                for (Category category : Category.values()) {
                    if (posJSON.containsKey(category.name())) {
                        ArrayList<Double> xy = posJSON.get(category.name());
                        category.posX = category.renderX = xy.get(0).floatValue();
                        category.posY = category.renderY = xy.get(1).floatValue();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}