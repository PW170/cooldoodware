package com.github.scoliossis.modules.impl.player;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.RotationEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.MathUtil;
import com.github.scoliossis.utils.minecraft.PlayerUtil;
import com.github.scoliossis.utils.minecraft.RotationUtil;

@RegisterModule(
        name = "Anti Aim",
        description = "poopware antiaim bypahh faceit 2026",
        category = Category.PLAYER,
        dangerous = true
)
public class AntiAim extends Module {
    // always goes first
    @SubscribeEvent(priority = 1)
    public static void onPlayerUpdate(RotationEvent event) {
        event.rotation = RotationUtil.applyGcd(
                new RotationUtil.Rotation(
                        MathUtil.getRandomInRange(-90, 90),
                        PlayerUtil.lastRotation().yaw + MathUtil.getRandomInRange(-180, 180)
                )
        );
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
