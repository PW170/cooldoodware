package com.github.scoliossis.utils.render.draggable;

import com.github.scoliossis.Main;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RenderTickEvent;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.ScreenUtil;
import com.github.scoliossis.utils.minecraft.ChatUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.gui.GuiChat;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class DraggableRenderer {
    public static ArrayList<Draggable> draggables = new ArrayList<>();

    private static Draggable dragging = null;
    private static Rectangle draggingCoords = null;

    private final static String draggablesPath = Main.extraSavedFeaturesPath;
    private final static String draggablesFile = "draggables" + Main.configExtension;

    // more gibberish code i hate everything
    @SubscribeEvent
    public static void drawDraggables(RenderTickEvent event) {
        for (Draggable draggable : draggables) {
            if (!shouldRender(draggable)) continue;

            try {
                int renderX = (int) (draggable.x * C.res().getScaledWidth());
                int renderY = (int) (draggable.y * C.res().getScaledHeight());

                GL11.glPushMatrix();
                GL11.glTranslated(renderX, renderY, 0);
                double[] size = draggable.render.call();

                if (canDrag()) {
                    draggingCoords = dragging == null ? new Rectangle((int) ScreenUtil.getMouseX() - renderX, (int) ScreenUtil.getMouseY() - renderY) : draggingCoords;

                    if (dragging == draggable) {
                        RenderUtil.drawRectOutline(0, 0, size[0], size[1], 1, Color.WHITE);

                        draggable.x = (ScreenUtil.getMouseX() - draggingCoords.width) / C.res().getScaledWidth();
                        draggable.y = (ScreenUtil.getMouseY() - draggingCoords.height) / C.res().getScaledHeight();
                    }

                    dragging = Mouse.isButtonDown(0) ? dragging == null && ScreenUtil.isMouseOver(0, 0, size[0], size[1], ScreenUtil.getMouseX(), ScreenUtil.getMouseY()) ? draggable : dragging : null;
                }

                GL11.glPopMatrix();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean canDrag() {
        return C.mc.currentScreen instanceof GuiChat;
    }

    public static void saveDraggables() {
        HashMap<String, double[]> draggingJSON = new HashMap<>();

        File file = new File(draggablesPath + "/" + draggablesFile);
        if (file.exists()) {
            try {
                draggingJSON = C.gson.fromJson(FileUtils.readFileToString(file), HashMap.class);
            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                ChatUtil.prefixMessage("Failed to read previous positions json");
            }
        }

        if (draggingJSON == null) draggingJSON = new HashMap<>();

        for (Draggable draggable : draggables) {
            draggingJSON.put(draggable.id, new double[] {draggable.x, draggable.y});
        }

        try {
            Files.createDirectories(Paths.get(draggablesPath));
            Files.write(file.toPath(), C.gson.toJson(draggingJSON).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadDraggingPositions() {
        File file = new File(draggablesPath + "/" + draggablesFile);
        if (!file.exists()) {
            System.err.println("No draggables config found!");
            return;
        }

        try {
            HashMap<String, ArrayList<Double>>  draggingJSON = C.gson.fromJson(FileUtils.readFileToString(file), HashMap.class);

            for (Draggable draggable : draggables) {
                if (draggingJSON.containsKey(draggable.id)) {
                    ArrayList<Double> positions = draggingJSON.get(draggable.id);
                    draggable.x = Double.parseDouble(String.valueOf(positions.get(0)));
                    draggable.y = Double.parseDouble(String.valueOf(positions.get(1)));
                }
                else {
                    System.err.println("Dragable config for " + draggable.id + " not found!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ChatUtil.prefixMessage("Failed to read previous positions json");
        }
    }

    private static boolean shouldRender(Draggable draggable) {
        return (draggable.conditions.test(null) || C.mc.currentScreen instanceof GuiChat)
                && draggable.canRender.test(null);
    }
}
