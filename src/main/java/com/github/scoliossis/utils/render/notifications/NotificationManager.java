package com.github.scoliossis.utils.render.notifications;

import com.github.scoliossis.modules.impl.client.ThemeModule;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {
    private static final List<Notification> notifications = new ArrayList<>();

    // Claymorphic Colors
    private static final Color CLAY_BG = new Color(30, 30, 36, 245);
    private static final Color CLAY_SHADOW = new Color(0, 0, 0, 80);
    private static final Color CLAY_HIGHLIGHT = new Color(255, 255, 255, 25);
    private static final Color TEXT_MUTED = new Color(190, 190, 200);

    private static final float PAD = 10;
    private static final float NOTIF_WIDTH = 180;
    private static final int TITLE_SIZE = 16;
    private static final int DESC_SIZE = 12;

    public static void post(String title, String description, NotificationType type, long durationMs) {
        notifications.add(new Notification(title, description, type, durationMs));
    }

    public static void render() {
        if (notifications.isEmpty()) return;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();

        float yOffset = screenHeight - 20;

        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notif = iterator.next();

            float notifHeight = PAD * 2 + FontUtil.getFontHeight(TITLE_SIZE) + FontUtil.getFontHeight(DESC_SIZE) + 4;
            
            yOffset -= notifHeight + 10; // 10 is spacing between notifications

            float targetX = screenWidth - NOTIF_WIDTH - 10;
            float targetY = yOffset;

            if (notif.isExpired()) {
                targetX = screenWidth + 20; // Slide off screen
                
                // Remove once animated off screen
                if (notif.getX() >= screenWidth) {
                    iterator.remove();
                    continue;
                }
            } else if (notif.getTimeLeft() < 300) {
                 // Fast slide off screen at the very end
                 targetX = screenWidth + 20;
            }

            // Initialize position if just spawned
            if (notif.getX() == 200 && notif.getY() == 50) {
                notif.animate(screenWidth + 20, targetY); 
            }

            notif.animate(targetX, targetY);

            drawClayNotification(notif.getX(), notif.getY(), NOTIF_WIDTH, notifHeight, notif);
        }
    }

    private static void drawClayNotification(float x, float y, float w, float h, Notification notif) {
        GL11.glPushMatrix();

        // 1. Drop shadow
        RenderUtil.drawRoundedRect(x + 2, y + 2, w, h, 8, CLAY_SHADOW);
        
        // 2. Base clay body
        RenderUtil.drawRoundedRect(x, y, w, h, 8, CLAY_BG);
        
        // 3. Top rim highlight
        RenderUtil.drawRoundedRect(x + 1, y + 1, w - 2, 2, 8, CLAY_HIGHLIGHT);

        // 4. Accent Strip (based on type, if INFO use theme color)
        Color accent = notif.getType() == NotificationType.INFO ? 
            RenderUtil.getColorsFade(0, ThemeModule.getThemeColours(), 4f) : 
            notif.getType().getColor();
            
        Color accentStrip = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140);
        RenderUtil.drawRoundedRect(x, y, w, 2, 8, accentStrip);

        // 5. Progress Bar (bottom)
        float progress = Math.max(0, Math.min(1, (float) notif.getTimeLeft() / notif.getMaxTime()));
        if (progress > 0) {
            Color progColor = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 100);
            RenderUtil.drawRoundedRect(x, y + h - 2, w * progress, 2, 0, progColor); // Optional bottom bar
        }

        // 6. Text
        float cursorY = y + PAD;
        FontUtil.drawString(notif.getTitle(), x + PAD, cursorY, TITLE_SIZE, accent, false);
        cursorY += FontUtil.getFontHeight(TITLE_SIZE) + 4;
        FontUtil.drawString(notif.getDescription(), x + PAD, cursorY, DESC_SIZE, TEXT_MUTED, false);

        GL11.glPopMatrix();
    }
}
