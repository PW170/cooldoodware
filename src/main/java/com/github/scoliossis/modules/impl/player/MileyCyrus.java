package com.github.scoliossis.modules.impl.player;

import com.github.scoliossis.bridge.net.minecraft.client.settings.KeyBindingBridge;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.MotionEvent;
import com.github.scoliossis.events.impl.PlayerUpdateEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.KeybindHandler;

@RegisterModule(
        name = "Miley Cyrus",
        description = "Cyrus became the subject of media and public scrutiny when she twerked against Thicke's crotch.",
        category = Category.PLAYER
)
public class MileyCyrus extends Module {
    @RegisterSubModule(name = "Server Side", description = "Only other players can see your crouching" )
    public static boolean serverSide = false;

    @SubscribeEvent
    public static void onPlayerUpdateEvent(PlayerUpdateEvent event) {
        if (!serverSide)
            KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(C.p().ticksExisted % 2 == 0);
    }

    @SubscribeEvent
    public static void onPlayerMotionEvent(MotionEvent event) {
        if (serverSide)
            event.sneaking = C.p().ticksExisted % 2 == 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(KeybindHandler.isKeyDown(C.mc.gameSettings.keyBindSneak));
    }
}
