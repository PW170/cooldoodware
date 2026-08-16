package com.github.scoliossis.modules.impl.combat;

import com.github.scoliossis.bridge.net.minecraft.client.MinecraftBridge;
import com.github.scoliossis.bridge.net.minecraft.client.settings.KeyBindingBridge;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.PlayerUpdateEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.modules.impl.player.Fucker;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.MathUtil;
import com.github.scoliossis.utils.minecraft.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;

// shoutout to litdab. for the idea to make autoblock a separate module
// a really old mushroom build ~2020 did the same, but badly, its a cool idea i think
@RegisterModule(
        name = "Auto Block",
        description = "im enderflame and you are BLOCKED",
        category = Category.COMBAT,
        dangerous = true
)
// todo: on right click option
public class AutoBlock extends Module {
    @RegisterSubModule(name = "Range", max = 6)
    public static double autoblockRange = 3;

    @RegisterSubModule(name = "Through Walls")
    public static boolean throughWalls = true;

    @RegisterSubModule(name = "Autoblock Mode", description = "bypahh")
    public static AutoBlockMode autoblockMode = AutoBlockMode.Blink;

    @RegisterSubModule(name = "Packet Block", parent = "Autoblock Mode", modeParentString = {"Vanilla", "Blink"})
    public static boolean packetBlock = false;

    @RegisterSubModule(name = "Blink Mode", parent = "Autoblock Mode", modeParentString = "Blink")
    public static Blink_Mode blinkMode = Blink_Mode.Hypixel;

    @RegisterSubModule(name = "Min Blink Ticks", min = 2, max = 6, parent = "Blink Mode", modeParentString = "Random")
    public static int minBlinkTicks = 3;

    @RegisterSubModule(name = "Max Blink Ticks", min = 2, max = 6, parent = "Blink Mode", modeParentString = "Random")
    public static int maxBlinkTicks = 3;

    @RegisterSubModule(name = "Blink Pre", parent = "Autoblock Mode", modeParentString = "Blink", dangerous = true)
    public static boolean blinkPre = false;

    @RegisterSubModule(name = "Legit Blink", description = "Unblocks randomly to look legit", parent = "Autoblock Mode", modeParentString = "Blink")
    public static boolean legitBlink = true;
    @RegisterSubModule(name = "Block Ratio", parent = "Legit Blink")
    public static double blockRatio = 0.5;

    @AllArgsConstructor
    public enum Blink_Mode {
        Hypixel(2),
        Reduce(3),
        Random(-1) {
            @Override
            public int getBlinkTicks() {
                return (int) MathUtil.getRandomInRange(minBlinkTicks, maxBlinkTicks + 1);
            }
        };

        @Getter
        public final int blinkTicks;
    }

    @Getter private static boolean isBlocking, isServerBlocking = false;

    public static boolean swingQueued = false;
    public static boolean clickBlockQueued = false;

    public static boolean isBlockingSwing() {
        return PlayerUtil.isUsingItem() || PlayerUtil.getLastUnblock() == MovementUtil.ticks;
    }

    private static ItemStack itemInUse = null;

    private static int blinkTick = 0;
    private static int nextBlinkTicks = 2;
    @Getter
    private static boolean isBlinking = false;

    private static AutoBlockMode lastAutoblockMode;

    @SubscribeEvent(priority = 998)
    public static void tickAutoBlock(PlayerUpdateEvent event) {
        if (Fucker.getCurrentTarget() != null && Fucker.noAutoblock) {
            stopBlocking();
            return;
        }
        if (C.p().getHeldItem() != itemInUse || autoblockMode != lastAutoblockMode) {
            stopBlocking();
        }

        lastAutoblockMode = autoblockMode;

        List<EntityLivingBase> targets = TargetUtil.getPossibleTargets(autoblockRange, throughWalls, true);

        if (targets.isEmpty()) {
            stopBlocking();
            return;
        }

        if (C.p().getHeldItem() != null && C.p().getHeldItem().getItemUseAction() == EnumAction.BLOCK) {
            tickBlocking();
        }
    }

    @SubscribeEvent(priority = 999)
    public static void tickSwingQueued(PlayerUpdateEvent event) {
        if (PlayerUtil.canAttack() && swingQueued) {
            MinecraftBridge.from(C.mc).bridge$clickMouse();
            swingQueued = false;
        }

        if (PlayerUtil.canAttack() && clickBlockQueued) {
            MinecraftBridge.from(C.mc).bridge$sendClickBlockToController(C.mc.currentScreen == null && C.mc.gameSettings.keyBindAttack.isKeyDown() && C.mc.inGameHasFocus);
            clickBlockQueued = false;
        }
    }

    public static void stopBlocking() {
        setBlocking(false, false);

        if (isBlinking) {
            blinkTick = 0;
            isBlinking = false;
            BlinkUtil.popBlink(true, false);
        }

    }


    public static void tickBlocking() {
        if (autoblockMode == AutoBlockMode.Blink) {
            boolean shouldBlock = !legitBlink || Math.random() <= blockRatio;
            switch (blinkTick) {
                case 0:
                    if (!setBlocking(true, shouldBlock)) return;

                    BlinkUtil.popBlink(true, false);

                    if (blinkPre) {
                        isBlinking = true;
                        BlinkUtil.pushBlink(true, false);
                    }
                    else isBlinking = false;
                    break;
                case 1:
                    if (!blinkPre) {
                        isBlinking = true;
                        BlinkUtil.pushBlink(true, false);
                    }

                    setBlocking(true, false);
                    break;
            }

            blinkTick++;

            if (blinkTick > nextBlinkTicks) {
                blinkTick = 0;
                nextBlinkTicks = blinkMode.getBlinkTicks();
            }
        }
        else {
            setBlocking(true, autoblockMode == AutoBlockMode.Vanilla);
        }
    }

    private static boolean setBlocking(boolean clientSide, boolean serverSide) {
        if (serverSide != isServerBlocking) {
            boolean blockSuccess = tryBlock(serverSide);

            if (!blockSuccess) return false;
            isServerBlocking = serverSide;
        }

        isBlocking = clientSide;
        itemInUse = C.p().getHeldItem();

        return true;
    }

    public static boolean canSwingWhileBlocking() {
        return autoblockMode == AutoBlockMode.Vanilla && ModuleManager.isEnabled(AutoBlock.class) && isServerBlocking;
    }

    public static boolean tryBlock(boolean down) {
        if (down && MovementUtil.getOverriddenKeybinds().containsKey(C.mc.gameSettings.keyBindUseItem) && !MovementUtil.getOverriddenKeybinds().get(C.mc.gameSettings.keyBindUseItem))
            return false;

        if (packetBlock) {
            if (down) PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(C.p().getHeldItem()));
            else
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        else {
            KeyBindingBridge.from(C.mc.gameSettings.keyBindUseItem).bridge$setDown(down);

            if (down) MinecraftBridge.from(C.mc).bridge$rightClickMouse();
            else C.mc.playerController.onStoppedUsingItem(C.p());
        }

        return true;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        stopBlocking();
    }

    @Override
    public String arrayListExtraInfo() {
        return autoblockMode.name();
    }

    public enum AutoBlockMode {
        Vanilla, Blink, Fake
    }
}
