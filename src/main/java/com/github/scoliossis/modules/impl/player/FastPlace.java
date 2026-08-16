package com.github.scoliossis.modules.impl.player;

import com.github.scoliossis.modules.*;
import com.github.scoliossis.utils.minecraft.InventoryUtil;

@RegisterModule(
        name = "Fast Place",
        description = "todo: add description",
        category = Category.PLAYER
)
public class FastPlace extends Module {
    @RegisterSubModule(name = "Blocks Only")
    public static boolean blocksOnly = true;

    @RegisterSubModule(name = "Place Delay", min = 1, max = 5)
    public static int placeDelay = 1;

    public static int getPlaceDelay() {
        return ModuleManager.isEnabled(FastPlace.class) && (!blocksOnly || InventoryUtil.isValidBlock()) ? placeDelay : 4;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
