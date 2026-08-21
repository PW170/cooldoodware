package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.PlayerUtil;

@RegisterModule(
        name = "No Slow",
        description = "Provides No Slow functionality for the client.",
        category = Category.MOVEMENT,
        dangerous = true
)
public class NoSlow extends Module {
    public static boolean shouldSlowDown() {
        return !ModuleManager.isEnabled(NoSlow.class) && PlayerUtil.isUsingItem();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
