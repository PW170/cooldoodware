package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.MoveFlyingEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.client.C;

@RegisterModule(
        name = "Speed",
        description = "Don't stop me now, I'm havin' such a good time, I'm havin' a ball!",
        category = Category.MOVEMENT,
        dangerous = true
)
public class Speed extends Module {
    @RegisterSubModule(name = "Speed", max = 5)
    public static float speed = 2;

    @SubscribeEvent
    public static void onMoveFlying(MoveFlyingEvent event) {
        C.p().setVelocity(0,C.p().motionY,0);
        event.friction = speed;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
