package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.MotionEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;

@RegisterModule(
        name = "Full Bright",
        description = "Provides Full Bright functionality for the client.",
        category = Category.RENDER
)
public class FullBright extends Module {
    private static float oldGamma = 0;

    @Override
    protected void onEnable() {
        oldGamma = C.mc.gameSettings.gammaSetting;
    }

    @Override
    protected void onDisable() {
        C.mc.gameSettings.gammaSetting = oldGamma;
    }

    @SubscribeEvent
    public static void onMotionEvent(MotionEvent event) {
        C.mc.gameSettings.gammaSetting = 100;
    }
}
