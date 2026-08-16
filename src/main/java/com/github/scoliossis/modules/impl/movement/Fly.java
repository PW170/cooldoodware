package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.MoveFlyingEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.modules.impl.render.Freecam;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.KeybindHandler;

@RegisterModule(
        name = "Fly",
        description = "aeroplane hake",
        category = Category.MOVEMENT,
        dangerous = true
)
public class Fly extends Module {
    @RegisterSubModule(name = "Horizontal Speed", max = 10)
    public static float horizontalSpeed = 2;
    @RegisterSubModule(name = "Vertical Speed", max = 10)
    public static float verticalSpeed = 2;

    @SubscribeEvent
    public static void onMoveFlying(MoveFlyingEvent event) {
        C.p().setVelocity(0,0,0);

        if (ModuleManager.isEnabled(Freecam.class) || C.mc.currentScreen != null) return;

        event.friction = horizontalSpeed;
        C.p().motionY +=
                (KeybindHandler.isKeyDown(C.mc.gameSettings.keyBindJump) ? verticalSpeed : 0) +
                        (KeybindHandler.isKeyDown(C.mc.gameSettings.keyBindSneak) ? -verticalSpeed : 0);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
