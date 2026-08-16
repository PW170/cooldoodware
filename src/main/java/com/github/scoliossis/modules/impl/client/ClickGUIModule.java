package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ClientTickEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.screens.ClickGUI.ClickGUIScreen;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.ScreenUtil;
import com.github.scoliossis.utils.render.EasingUtil;

@RegisterModule(
        name = "Click GUI",
        description = "People always tell you 'Be humble. Be humble.' When was the last time someone told you to be amazing? Be great! Be awesome! Be awesome!",
        category = Category.CLIENT
)
public class ClickGUIModule extends Module {
    @RegisterSubModule(name = "Fancy Dragging")
    public static boolean fancyDragging = true;

    @RegisterSubModule(name = "Open", parent = "Animations")
    public static EasingUtil.EasingFunctions openAnimation = EasingUtil.EasingFunctions.Ease_In_Out_Sine;

    @RegisterSubModule(name = "Open Length", parent = "Animations", max = 1000, increment = 50)
    public static long openAnimationLength = 100;

    @RegisterSubModule(name = "Close", parent = "Animations")
    public static EasingUtil.EasingFunctions closeAnimation = EasingUtil.EasingFunctions.Ease_In_Out_Sine;

    @RegisterSubModule(name = "Close Length", parent = "Animations", max = 1000, increment = 50)
    public static long closeAnimationLength = 100;

    private static final ClickGUIScreen screen = new ClickGUIScreen();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (!(C.mc.currentScreen instanceof ClickGUIScreen)) {
            ModuleManager.getModule(ClickGUIModule.class).setEnabled(false);
        }
    }

    @Override
    protected void onEnable() {
        if (!C.isInGame()) {
            this.toggle();
            return;
        }
        ScreenUtil.setGuiToDisplay(screen);
    }

    @Override
    protected void onDisable() {
    }
}
