package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ClientTickEvent;
import com.github.scoliossis.events.impl.MotionEvent;
import com.github.scoliossis.events.impl.PacketEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.modules.impl.client.ThemeModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.TimerUtil;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.github.scoliossis.utils.render.draggable.Draggable;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RegisterModule(
        name = "Statistics",
        description = "Displays statistics about your session",
        category = Category.RENDER
)
public class Statistics extends Module {
    public static int gamesPlayed, killCount, deathCount, victoryCount;
    public static long startTime = System.currentTimeMillis(), endTime = -1;
    public static final String[] KILL_TRIGGERS = {"by *", "para *", "fue destrozado a manos de *"};
    public static final String[] WIN_TRIGGERS = {
            "1st place", "victory!", "winner!", "you won", "you win",
            "1st place!", "#1!", "mvp", "game over", "place: #1",
            "you are the last", "won the game", "team wins!"
    };

    @RegisterSubModule(name = "Show Speed Graph")
    public static boolean motionGraph = true;
    @RegisterSubModule(name = "Separate Graph")
    public static boolean seprateMotionGraph = true;
    @RegisterSubModule(name = "Scale", min = 0.5, max = 2.0, increment = 0.05)
    public static float scale = 1.0f;

    private static final Map<String, Double> statistics = new LinkedHashMap<>();
    private static final List<Float> speeds = new ArrayList<>();

    // Claymorphic Theme Colors
    private static final Color CLAY_BG = new Color(30, 30, 36, 245);
    private static final Color CLAY_INNER = new Color(42, 42, 50, 255);
    private static final Color CLAY_SHADOW = new Color(0, 0, 0, 80);
    private static final Color CLAY_HIGHLIGHT = new Color(255, 255, 255, 25);
    private static final Color TEXT_MUTED = new Color(190, 190, 200);

    private static final float PAD = 14;
    private static final float PANEL_WIDTH = 190;
    private static final int TITLE_SIZE = 22;
    private static final int ROW_SIZE = 16;
    private static final int SMALL_SIZE = 12;

    private static float width, height;

    public static Draggable dragging = new Draggable(
            "sessionstats",
            Statistics::renderStatistics,
            e -> ModuleManager.isEnabled(Statistics.class),
            e -> true
    );

    public static Draggable motionDragging = new Draggable(
            "motionGraph",
            Statistics::renderMotionGraph,
            e -> ModuleManager.isEnabled(Statistics.class) && motionGraph && seprateMotionGraph,
            e -> true
    );

    private static Color accent() {
        return RenderUtil.getColorsFade(0, ThemeModule.getThemeColours(), 4f);
    }

    private static void drawClayPanel(float x, float y, float w, float h, float radius, Color bg) {
        // Drop shadow for puffy clay look
        RenderUtil.drawRoundedRect(x + 2, y + 2, w, h, radius, CLAY_SHADOW);
        // Main clay body
        RenderUtil.drawRoundedRect(x, y, w, h, radius, bg);
        // Top rim highlight
        RenderUtil.drawRoundedRect(x + 1, y + 1, w - 2, 2, radius, CLAY_HIGHLIGHT);
    }

    // Outer panel with theme accent strip — matches ClickGUI category header style
    private static void drawClayPanelWithAccent(float x, float y, float w, float h, float radius, Color bg) {
        drawClayPanel(x, y, w, h, radius, bg);
        Color ac = accent();
        Color accentStrip = new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 140);
        RenderUtil.drawRoundedRect(x, y, w, 2, radius, accentStrip);
    }

    private static double[] renderStatistics() {
        updateSize();

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1f);

        float rowHeight = FontUtil.getFontHeight(ROW_SIZE);
        float titleHeight = FontUtil.getFontHeight(TITLE_SIZE);
        float smallHeight = FontUtil.getFontHeight(SMALL_SIZE);

        float innerPad = 8;
        
        // Block Heights
        float headerBlock = titleHeight + 4;
        float statsInnerHeight = (statistics.size() * (rowHeight + innerPad)) - innerPad;
        float statsBlock = innerPad * 2 + statsInnerHeight;
        float playTimeInnerHeight = rowHeight + 8 + 6;
        float playTimeBlock = innerPad * 2 + playTimeInnerHeight;

        float graphBlock = 0;
        if (motionGraph && !seprateMotionGraph) {
            float graphInnerHeight = smallHeight + 6 + 40;
            graphBlock = innerPad * 2 + graphInnerHeight + PAD;
        }

        height = PAD + headerBlock + PAD + statsBlock + PAD + playTimeBlock + (graphBlock > 0 ? PAD + graphBlock - PAD : 0) + PAD;
        width = PANEL_WIDTH;

        // Base Clay Panel — outer with theme accent strip on top
        drawClayPanelWithAccent(0, 0, width, height, 12, CLAY_BG);

        Color accentColor = accent();
        float contentWidth = width - 2 * PAD;
        float cursor = PAD;

        // Header
        FontUtil.drawString("Session Stats", PAD, cursor, TITLE_SIZE, Color.WHITE, false);
        cursor += titleHeight + PAD;

        // Stats Clay Container
        drawClayPanel(PAD, cursor, contentWidth, statsBlock, 8, CLAY_INNER);
        float statCursor = cursor + innerPad;
        for (Map.Entry<String, Double> entry : statistics.entrySet()) {
            String label = entry.getKey();
            boolean isKD = label.equals("K/D");
            String value = isKD
                    ? String.valueOf(entry.getValue().doubleValue())
                    : String.valueOf(entry.getValue().intValue());

            FontUtil.drawString(label, PAD + innerPad, statCursor, ROW_SIZE, TEXT_MUTED, false);
            float valueWidth = FontUtil.getStringWidth(value, ROW_SIZE);
            FontUtil.drawString(value, width - PAD - innerPad - valueWidth, statCursor, ROW_SIZE, isKD ? accentColor : Color.WHITE, false);

            statCursor += rowHeight + innerPad;
        }
        cursor += statsBlock + PAD;

        // Play Time Clay Container
        drawClayPanel(PAD, cursor, contentWidth, playTimeBlock, 8, CLAY_INNER);
        float ptCursor = cursor + innerPad;
        
        int[] playTime = getPlayTime();
        String playTimeText = formatPlayTime(playTime);
        FontUtil.drawString("Play Time", PAD + innerPad, ptCursor, ROW_SIZE, TEXT_MUTED, false);
        float ptValueWidth = FontUtil.getStringWidth(playTimeText, ROW_SIZE);
        FontUtil.drawString(playTimeText, width - PAD - innerPad - ptValueWidth, ptCursor, ROW_SIZE, Color.WHITE, false);
        
        ptCursor += rowHeight + 8;

        // Progress Bar
        float barWidth = contentWidth - 2 * innerPad;
        float percentage = Math.min((playTime[1] + (playTime[2] / 60f)) / 60f, 1f);
        
        // Inner dark track for the bar
        RenderUtil.drawRoundedRect(PAD + innerPad, ptCursor, barWidth, 6, 3f, CLAY_INNER);
        RenderUtil.drawRoundedRectOutline(PAD + innerPad, ptCursor, barWidth, 6, 3f, 0.5f, CLAY_HIGHLIGHT);
        
        // Fill
        if (percentage > 0) {
            Color[] grad = RenderUtil.getColorsFade(0, barWidth, ThemeModule.getThemeColours(), 4f);
            RenderUtil.drawGradientLR(PAD + innerPad, ptCursor, barWidth * percentage, 6, grad[0], grad[1]);
        }
        
        cursor += playTimeBlock + PAD;

        // Integrated Speed Graph
        if (motionGraph && !seprateMotionGraph) {
            float graphInnerHeight = smallHeight + 6 + 40;
            drawClayPanel(PAD, cursor, contentWidth, innerPad * 2 + graphInnerHeight, 8, CLAY_INNER);
            
            float graphCursor = cursor + innerPad;
            FontUtil.drawString("Speed (BPS)", PAD + innerPad, graphCursor, SMALL_SIZE, TEXT_MUTED, false);
            String avgText = getAverageSpeed() + " avg";
            FontUtil.drawString(avgText, width - PAD - innerPad - FontUtil.getStringWidth(avgText, SMALL_SIZE), graphCursor, SMALL_SIZE, accentColor, false);
            
            graphCursor += smallHeight + 6;
            drawSpeedPlot(PAD + innerPad, graphCursor, contentWidth - 2 * innerPad, 40, accentColor);
        }

        GL11.glPopMatrix();
        return new double[]{width * scale, height * scale};
    }

    private static double[] renderMotionGraph() {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1f);

        Color accentColor = accent();
        float innerPad = 8;
        float headerHeight = FontUtil.getFontHeight(SMALL_SIZE) + 6;
        float plotHeight = 50;
        float contentWidth = PANEL_WIDTH - 2 * PAD;
        float panelInnerHeight = innerPad * 2 + headerHeight + plotHeight;
        
        width = PANEL_WIDTH;
        height = PAD * 2 + panelInnerHeight;

        // Base Clay Panel — outer with theme accent strip on top
        drawClayPanelWithAccent(0, 0, width, height, 12, CLAY_BG);

        // Inner Clay Panel
        drawClayPanel(PAD, PAD, contentWidth, panelInnerHeight, 8, CLAY_INNER);

        float cursor = PAD + innerPad;
        FontUtil.drawString("Movement Speed", PAD + innerPad, cursor, SMALL_SIZE, TEXT_MUTED, false);
        String avgText = getAverageSpeed() + " bps avg";
        FontUtil.drawString(avgText, width - PAD - innerPad - FontUtil.getStringWidth(avgText, SMALL_SIZE), cursor, SMALL_SIZE, accentColor, false);

        cursor += headerHeight;
        drawSpeedPlot(PAD + innerPad, cursor, contentWidth - 2 * innerPad, plotHeight, accentColor);

        GL11.glPopMatrix();
        return new double[]{width * scale, height * scale};
    }

    private static void drawSpeedPlot(float x, float y, float w, float h, Color accent) {
        // Inner dark box
        RenderUtil.drawRoundedRect(x, y, w, h, 6, CLAY_INNER);
        RenderUtil.drawRoundedRectOutline(x, y, w, h, 6, 0.5f, CLAY_HIGHLIGHT);

        if (speeds.size() < 2) return;

        float plotBottom = y + h - 3;
        RenderUtil.beginRender();
        RenderUtil.beginAddingVertex(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        RenderUtil.glColor(accent);
        GL11.glLineWidth(2f); // Thicker for clay feel

        float length = w / (speeds.size() - 1);
        for (int i = 0; i < speeds.size(); i++) {
            float bps = speeds.get(i) * 50;
            float sx = x + i * length;
            float sy = plotBottom - Math.min(bps / 8f, 1f) * (h - 6); 
            RenderUtil.addVertex(sx, sy);
        }

        RenderUtil.finishRender();
    }

    private static double getAverageSpeed() {
        double average = speeds.stream().collect(Collectors.averagingDouble(value -> value.doubleValue() * 50));
        return Math.round(average * 100) / 100.0;
    }

    private static String formatPlayTime(int[] playTime) {
        int h = playTime[0], m = playTime[1], s = playTime[2];
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append(":");
        if (m < 10) sb.append("0");
        sb.append(m).append(":");
        if (s < 10) sb.append("0");
        sb.append(s);
        return sb.toString();
    }

    private static void updateSize() {
        statistics.put("Games Played", (double) gamesPlayed);
        statistics.put("Victories",    (double) victoryCount);
        statistics.put("K/D", deathCount == 0 ? (double) killCount : Math.round((double) killCount / deathCount * 100) / 100.0);
        statistics.put("Kills", (double) killCount);
    }

    @SubscribeEvent
    public static void onChat(PacketEvent.Receive event) {
        if (C.mc.thePlayer == null) return;
        if (!(event.packet instanceof S02PacketChat)) return;

        S02PacketChat packet = (S02PacketChat) event.packet;
        String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());
        String messageStr = packet.getChatComponent().toString();

        if (!message.contains(":") && Arrays.stream(KILL_TRIGGERS).anyMatch(message.replace(C.mc.thePlayer.getName(), "*")::contains)) {
            killCount++;
        }
        // Victory detection — covers Hypixel BedWars, SkyWars, Duels, Murder Mystery, The Bridge etc.
        String lowerMsg = message.toLowerCase();
        if (!message.contains(":") && Arrays.stream(WIN_TRIGGERS).anyMatch(lowerMsg::contains)) {
            victoryCount++;
        }
        if (messageStr.contains("ClickEvent{action=RUN_COMMAND, value='/play ") || messageStr.contains("Want to play again?")) {
            gamesPlayed++;
        }
        if (message.contains("You died!")) {
            deathCount++;
        }
    }

    @SubscribeEvent
    public static void onMotion(MotionEvent event) {
        if (speeds.size() >= 100) {
            speeds.remove(0);
        }
        speeds.add(getPlayerSpeed());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (endTime == -1 && (!C.mc.isSingleplayer() && C.mc.getCurrentServerData() == null)) {
            endTime = System.currentTimeMillis();
        } else if (endTime != -1 && (C.mc.isSingleplayer() || C.mc.getCurrentServerData() != null)) {
            reset();
        }
    }

    private static float getPlayerSpeed() {
        double bps = (Math.hypot(C.p().posX - C.p().prevPosX, C.p().posZ - C.p().prevPosZ) * TimerUtil.getTimer()) * 20;
        return (float) bps / 50;
    }

    public static int[] getPlayTime() {
        long diff = getTimeDiff();
        long diffSeconds = 0, diffMinutes = 0, diffHours = 0;
        if (diff > 0) {
            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
        }
        return new int[]{(int) diffHours, (int) diffMinutes, (int) diffSeconds};
    }

    public static long getTimeDiff() {
        return (endTime == -1 ? System.currentTimeMillis() : endTime) - startTime;
    }

    public static void reset() {
        startTime = System.currentTimeMillis();
        endTime = -1;
        gamesPlayed = 0;
        killCount = 0;
        deathCount = 0;
        victoryCount = 0;
    }

    @Override
    protected void onEnable() {
        speeds.clear();
    }

    @Override
    protected void onDisable() {
    }
}
