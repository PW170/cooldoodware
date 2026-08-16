package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ModuleStateChangeEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.render.EasingUtil;
import com.github.scoliossis.utils.render.FontUtil;
import com.github.scoliossis.utils.render.RenderUtil;
import com.github.scoliossis.utils.render.draggable.Draggable;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

@RegisterModule(
        name = "Array List",
        description = "\"What's Happening?\"",
        category = Category.CLIENT,
        enabledByDefault = true
)
public class ArrayListModule extends Module {
    @RegisterSubModule(name = "Colour")
    public SubCategory colour = new SubCategory();

    @RegisterSubModule(name = "Don't Use Theme", parent = "Colour")
    public static boolean dontUseTheme = true;

    @RegisterSubModule(name = "Colour Mode", parent = "Don't Use Theme")
    public static RenderUtil.ThemeColours customColour = RenderUtil.ThemeColours.Gray;

    @RegisterSubModule(name = "Pulsing Colour", parent = "Colour", description = "Epic pulsing hack 2025")
    public static boolean pulsingColour = false;

    @RegisterSubModule(name = "Pulsing Speed", parent = "Pulsing Colour", min = 0.1, max = 10)
    public static float pulseSpeed = 1;

    @RegisterSubModule(name = "Pulsing Spread", parent = "Pulsing Colour", min = 0.1, max = 50)
    public static float pulseMulti = 1;

    @RegisterSubModule(name = "Animation In")
    public static SubCategory animationIn = new SubCategory();

    @RegisterSubModule(name = "Ease In", parent = "Animation In")
    public static EasingUtil.EasingFunctions easeInFunction = EasingUtil.EasingFunctions.Ease_In_Out_Sine;

    @RegisterSubModule(name = "Pop In Time", description = "Time for the pop in effect to finish", max = 1000, increment = 50, parent = "Animation In")
    public static long popInTime = 250;

    @RegisterSubModule(name = "Animation Out")
    public static SubCategory animationOut = new SubCategory();

    @RegisterSubModule(name = "Ease Out", parent = "Animation Out")
    public static EasingUtil.EasingFunctions easeOutFunction = EasingUtil.EasingFunctions.Ease_In_Out_Expo;

    @RegisterSubModule(name = "Pop Out Time", description = "Time for the pop out effect to finish", max = 1000, increment = 50, parent = "Animation Out")
    public static long popOutTime = 250;

    @RegisterSubModule(name = "Other")
    public SubCategory other = new SubCategory();

    @RegisterSubModule(name = "No Capitals", parent = "Other")
    public static boolean noCapitals = false;

    @RegisterSubModule(name = "No Spaces", parent = "Other")
    public static boolean noSpaces = true;

    @RegisterSubModule(name = "Show Mode", parent = "Other")
    public static boolean showMode = true;

    @RegisterSubModule(name = "Pointing Right", parent = "Other")
    public static boolean pointingRight = false;

    @RegisterSubModule(name = "Font Size", parent = "Other", min = 5, max = 40, increment = 1)
    public static int fontSize = 10;

    @RegisterSubModule(name = "gap Y", parent = "Other", min = 0, max = 10, increment = 1)
    public static int gapY = 0;

    @SubscribeEvent
    public static void onModuleStateChangedEvent(ModuleStateChangeEvent event) {
        EasingUtil.addAnimation(event.module.getUniqueKey("arraylist"), event.state ? popInTime : popOutTime, event.state, event.state ? easeInFunction : easeOutFunction);
    }

    public static Draggable arraylistRenderer = new Draggable(
            "ArrayList",
            () -> {
                List<Module> modules = ModuleManager.getModules();
                modules.removeIf(e -> (!e.isEnabled() && EasingUtil.getAnimation(e.getUniqueKey("arraylist")) == -1) || e.hide);

                if (modules.isEmpty()) return new double[] {0,0};

                modules.sort(Comparator.comparingDouble(e -> -FontUtil.getStringWidth(getModuleName(e), fontSize)));

                int fullWidth = FontUtil.getStringWidth(getModuleName(modules.get(0)), fontSize);
                double fullHeight = 0;

                int CARD_PAD_X = 6;
                int CARD_PAD_Y = 3;
                float RADIUS    = 5f;

                GL11.glPushMatrix();
                for (int i = 0; i < modules.size(); i++) {
                    Module module = modules.get(i);
                    String moduleName = getModuleName(module);

                    int textW  = FontUtil.getStringWidth(moduleName, fontSize);
                    int cardW  = textW + CARD_PAD_X * 2;
                    float textH = FontUtil.getFontHeight(fontSize);
                    float cardH = textH + CARD_PAD_Y * 2 + gapY;

                    int x = pointingRight ? 0 : -cardW;

                    GL11.glPushMatrix();
                    RenderUtil.glScissor(x, 0, cardW + 1, (int) cardH);

                    double animation = EasingUtil.getAnimation(module.getUniqueKey("arraylist"));
                    if (animation != -1) GL11.glTranslated(pointingRight ? -cardW * (1 - animation) : cardW * (1 - animation), cardH * (1 - animation), 0);
                    else animation = 1;

                    // Claymorphic card
                    RenderUtil.drawRoundedRect(x + 1, 1, cardW, cardH, RADIUS, new Color(0, 0, 0, 80)); // shadow
                    RenderUtil.drawRoundedRect(x, 0, cardW, cardH, RADIUS, new Color(26, 26, 34, 220)); // body
                    RenderUtil.drawRoundedRect(x + 1, 1, cardW - 2, 2, RADIUS, new Color(255, 255, 255, 22)); // rim

                    // Left accent strip — gray
                    RenderUtil.drawRoundedRect(x + (pointingRight ? 0 : cardW - 3), CARD_PAD_Y / 2f, 3, cardH - CARD_PAD_Y, 2, new Color(140, 140, 140, 200));

                    // Text — white, proper case
                    Color[] colours = dontUseTheme ? customColour.getColours() : ThemeModule.getThemeColours();
                    Color colour = colours[i % colours.length];
                    if (pulsingColour) colour = RenderUtil.getColorsFade(fullHeight * pulseMulti, cardH * pulseMulti, colours, pulseSpeed)[0];

                    FontUtil.drawString(moduleName, x + CARD_PAD_X, CARD_PAD_Y, fontSize, Color.WHITE, true);

                    GL11.glPopMatrix();
                    RenderUtil.disableScissor();

                    GL11.glTranslated(0, cardH * animation, 0);
                    fullHeight += cardH * animation;
                }
                GL11.glPopMatrix();

                return new double[] {pointingRight ? fullWidth : -fullWidth, fullHeight};
            },
            e -> ModuleManager.isEnabled(ArrayListModule.class),
            e -> true
    );

    private static String getModuleName(Module module) {
        String name = module.getAnnotation().name();
        if (noCapitals) name = name.toLowerCase();
        if (noSpaces) name = name.replaceAll(" ", "");
        String arrayListExtraInfo = module.arrayListExtraInfo();
        return name + (arrayListExtraInfo.isEmpty() || !showMode ? "" : " §8[" + arrayListExtraInfo + "]");
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
