package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.MovementInputEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;

@RegisterModule(
        name = "Sneak",
        description = "sneaking shouldnt slow me! im unstopable!",
        category = Category.MOVEMENT,
        dangerous = true
)
public class Sneak extends Module {
    @SubscribeEvent
    public static void onMovementInputEvent(MovementInputEvent event) {
        if (ModuleManager.isEnabled(Sneak.class) && C.p().isSneaking()) {
            event.movementInput.moveStrafe /= 0.3f;
            event.movementInput.moveForward /= 0.3f;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
