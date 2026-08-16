package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.modules.*;

@RegisterModule(
        name = "No Jump Delay",
        description = "just read the module name, no description is needed i hope.",
        category = Category.MOVEMENT
)
public class NoJumpDelay extends Module {
    @RegisterSubModule(name = "Delay Ticks", max = 20)
    public static int delay = 1;

    public static int getDelay() {
        return ModuleManager.isEnabled(NoJumpDelay.class) ? delay : 10;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
