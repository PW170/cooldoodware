package com.github.scoliossis.modules.impl.player;

import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.AttackBlockEvent;
import com.github.scoliossis.events.impl.PacketEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.InventoryUtil;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

@RegisterModule(
        name = "Auto Tool",
        description = "x.com/scoliosissy",
        category = Category.PLAYER
)
// todo: dont switch back tick 1
public class AutoTool extends Module {
    @RegisterSubModule(name = "Crouching Only")
    public static boolean onlyWhenCrouching = true;

    private static int currentSlot = -1;

    @SubscribeEvent
    public static void onAttackBlock(AttackBlockEvent event) {
        if (onlyWhenCrouching && !C.p().isSneaking()) return;

        int bestSlot = InventoryUtil.getBestSlotForBlock(event.pos);

        if (bestSlot == C.p().inventory.currentItem) return;

        currentSlot = C.p().inventory.currentItem;
        C.p().inventory.currentItem = InventoryUtil.getBestSlotForBlock(event.pos);
    }

    @SubscribeEvent
    public static void onAttackBlock(PacketEvent.Send event) {
        if (currentSlot == -1) return;
        if (!(event.packet instanceof C07PacketPlayerDigging)) return;

        C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.packet;
        if (packet.getStatus() != C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK && packet.getStatus() != C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) return;

        C.p().inventory.currentItem = currentSlot;
        currentSlot = -1;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
