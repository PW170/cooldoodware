package com.github.scoliossis.modules.impl.player;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.PlayerUpdateEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.minecraft.TimerUtil;

@RegisterModule(
        name = "Timer",
        description = "ilegal spead hax jose de chiterl",
        category = Category.PLAYER,
        dangerous = true
)
public class Timer extends Module {
    @RegisterSubModule(name = "Timer Speed", min = 0.01, max = 10)
    public static float timerSpeed = 1;

    public static float prevTimer = 1;

    @SubscribeEvent
    public static void onPlayerUpdate(PlayerUpdateEvent event) {
        if (timerSpeed != prevTimer) {
            TimerUtil.popTimer(prevTimer);
            TimerUtil.pushTimer(timerSpeed);

            prevTimer = timerSpeed;
        }
    }

    @Override
    public String arrayListExtraInfo() {
        return "" + timerSpeed;
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        TimerUtil.popTimer(timerSpeed);
        prevTimer = -1;
    }
}
