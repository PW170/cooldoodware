package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.bridge.net.minecraft.client.settings.KeyBindingBridge;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ClickMouseEvent;
import com.github.scoliossis.events.impl.MovementInputEvent;
import com.github.scoliossis.events.impl.PlayerUpdateEvent;
import com.github.scoliossis.events.impl.RenderWorldEvent;
import com.github.scoliossis.events.impl.RotationEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.InventoryUtil;
import com.github.scoliossis.utils.minecraft.MovementUtil;
import com.github.scoliossis.utils.minecraft.PlayerUtil;
import com.github.scoliossis.utils.minecraft.RotationUtil;
import com.github.scoliossis.utils.minecraft.WorldUtil;
import com.github.scoliossis.utils.render.Render3dUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@RegisterModule(
        name = "RiseScaffold",
        description = "Rise scaffold port for coolware",
        category = Category.MOVEMENT,
        dangerous = true
)
public class RiseScaffold extends Module {
    @RegisterSubModule(name = "Mode")
    public static Mode mode = Mode.Normal;
    public enum Mode {
        Normal,
        Godbridge,
        Breesily,
        Snap,
        Telly,
        Eagle,
        Derp
    }

    @RegisterSubModule(name = "Rotation Mode")
    public static RotationMode rotationMode = RotationMode.Normal;
    public enum RotationMode {
        Normal,
        Grim
    }

    @RegisterSubModule(name = "Sprint Mode")
    public static SprintMode sprintMode = SprintMode.Normal;
    public enum SprintMode {
        Normal,
        Disabled,
        Legit,
        Bypass,
        Vulcan,
        Verus,
        Matrix,
        WatchdogPrediction,
        WatchdogJump,
        Watchdog
    }

    @RegisterSubModule(name = "Tower")
    public static TowerMode towerMode = TowerMode.Legit;
    public enum TowerMode {
        Disabled,
        Vulcan,
        Vanilla,
        Normal,
        AirJump,
        Watchdog,
        MMC,
        NCP,
        Matrix,
        Legit,
        Verus,
        WatchdogPrediction18
    }

    @RegisterSubModule(name = "Same Y")
    public static SameYMode sameY = SameYMode.Off;
    public enum SameYMode {
        Off,
        On,
        AutoJump
    }

    @RegisterSubModule(name = "Downwards")
    public static DownwardsMode downwards = DownwardsMode.Off;
    public enum DownwardsMode {
        Off,
        Normal,
        Watchdog,
        Verus
    }

    @RegisterSubModule(name = "Yaw Offset")
    public static int yawOffset = 0;
    @RegisterSubModule(name = "Block Place Reach", min = 2, max = 6, increment = 0.1)
    public static float blockReach = 4.5f;
    @RegisterSubModule(name = "Block Reach Extend", min = 0, max = 2, increment = 0.1)
    public static float blockReachExtend = 0.0f;

    @RegisterSubModule(name = "Rotate")
    public static boolean rotate = true;
    @RegisterSubModule(name = "Only Place Best", parent = "Rotate")
    public static boolean onlyPlaceBest = true;
    @RegisterSubModule(name = "No Duplicate Rot", parent = "Rotate")
    public static boolean noDuplicateRot = true;

    @RegisterSubModule(name = "Telly Only On Right Click")
    public static boolean tellyOnlyOnRightClick = false;
    @RegisterSubModule(name = "Only When Jumping")
    public static boolean onlyWhenJumping = true;
    @RegisterSubModule(name = "Smooth Rotation")
    public static boolean smoothRotation = true;
    @RegisterSubModule(name = "Rotation Ticks", min = 1, max = 5)
    public static int rotationTicks = 3;
    @RegisterSubModule(name = "Telly Ticks", min = 0, max = 5)
    public static int tellyTicks = 1;
    @RegisterSubModule(name = "Telly Place Delay", min = 0, max = 5)
    public static int tellyPlaceDelay = 4;
    @RegisterSubModule(name = "Telly Forward Ticks", min = 0, max = 5)
    public static int tellyForwardTicks = 1;

    @RegisterSubModule(name = "Timer", min = 0.1, max = 10.0, increment = 0.1)
    public static float timer = 1.0f;
    @RegisterSubModule(name = "Place Delay", min = 0, max = 5, increment = 1)
    public static int placeDelay = 0;
    @RegisterSubModule(name = "Swap Time", min = 1, max = 10)
    public static int swapTime = 5;
    @RegisterSubModule(name = "Use Largest Stack")
    public static boolean useLargestStack = false;
    @RegisterSubModule(name = "Blocks Only")
    public static boolean blocksOnly = false;
    @RegisterSubModule(name = "Auto F5")
    public static boolean autoF5 = true;
    @RegisterSubModule(name = "Show Target Block")
    public static boolean showTargetBlock = true;
    @RegisterSubModule(name = "Target Block Colour", parent = "Show Target Block")
    public static Color targetBlockColour = new Color(227, 155, 248);
    @RegisterSubModule(name = "Show Previous Blocks")
    public static boolean showPreviousBlocks = true;
    @RegisterSubModule(name = "Fade Time", parent = "Show Previous Blocks", min = 50, max = 10000, increment = 50)
    public static long showPreviousBlocksTime = 3000L;
    @RegisterSubModule(name = "Item Spoof")
    public static boolean itemSpoof = false;
    @RegisterSubModule(name = "Crouch On Edge")
    public static boolean crouchOnEdge = false;
    @RegisterSubModule(name = "Crouch In Air", parent = "Crouch On Edge")
    public static boolean crouchInAir = false;
    @RegisterSubModule(name = "Manual Place")
    public static boolean manualPlace = false;
    @RegisterSubModule(name = "Right Click Down")
    public static boolean rightClickOnly = false;
    @RegisterSubModule(name = "Crouch Down")
    public static boolean crouchDownOnly = false;
    @RegisterSubModule(name = "Uncrouch", parent = "Crouch Down")
    public static boolean uncrouchAuto = true;
    @RegisterSubModule(name = "Moving Backwards")
    public static boolean movingBackwards = false;
    @RegisterSubModule(name = "Pitch Range")
    public static boolean pitchRange = false;
    @RegisterSubModule(name = "Min Pitch", parent = "Pitch Range", min = -90, max = 90)
    public static int minPitch = 35;
    @RegisterSubModule(name = "Max Pitch", parent = "Pitch Range", min = -90, max = 90)
    public static int maxPitch = 90;
    @RegisterSubModule(name = "Tower Pitch Range")
    public static boolean towerPitchRange = false;
    @RegisterSubModule(name = "Tower Min Pitch", parent = "Tower Pitch Range", min = -90, max = 90)
    public static int towerMinPitch = -90;
    @RegisterSubModule(name = "Tower Max Pitch", parent = "Tower Pitch Range", min = -90, max = 90)
    public static int towerMaxPitch = 0;

    @Getter
    private static boolean shouldScaffold = false;

    private static final ConcurrentHashMap<BlockPos, Long> previousInteractions = new ConcurrentHashMap<>();
    private static float lastPlacedDeltaX = -1;
    private static int blocksPlaced = 0;
    private static int tellyTicksCounter = 0;
    private static int tellyPlaceDelayCounter = 0;
    private static boolean tellyBlockPlaced = true;
    private static int tellyForwardTicksCount = -1;
    private static boolean lastJump = false;
    private static boolean didPlace = false;
    private static int lastPlaceStack = -1;
    private static boolean overridingSneak = false;
    private static BlockTarget targetBlock = null;
    private static boolean previousF5 = false;
    private static int stage = 0;
    private static int startY = 256;
    private static boolean shouldKeepY = false;
    private static boolean shouldTower = false;
    private static int previousStack = -1;
    private static int lastSwitchTick = 0;

    public static int getSlot() {
        return InventoryUtil.biggestBlockSlot();
    }

    @SubscribeEvent
    public static void onKeyInput(MovementInputEvent event) {
        if (!shouldScaffold) return;

        tellyForwardTicksCount = C.p().onGround && tellyBlockPlaced ? tellyForwardTicksCount + 1 : -1;

        if (shouldTelly()) {
            event.movementInput.jump = shouldTellyJump(event.movementInput.jump);
            tellyBlockPlaced &= !event.movementInput.jump;
        } else {
            event.movementInput.jump |= shouldTower() && towerMode == TowerMode.Legit;
        }

        lastJump = C.mc.gameSettings.keyBindJump.isKeyDown();
    }

    @SubscribeEvent(priority = 3000)
    public static void onRotationEvent(RotationEvent event) {
        didPlace |= C.p().inventory.currentItem == lastPlaceStack && InventoryUtil.isSlotEmpty(C.p().inventory.currentItem) && shouldScaffold;

        int bestStack = InventoryUtil.biggestBlockSlot();
        if (!InventoryUtil.isValidBlock() && (blocksOnly && !didPlace) || bestStack == -1 || !shouldScaffold()) {
            disable();
            return;
        }

        if (!InventoryUtil.isValidBlock() || (useLargestStack && blocksPlaced % swapTime == 0)) {
            if (previousStack == -1) previousStack = C.p().inventory.currentItem;
            C.p().inventory.currentItem = bestStack;
        }

        if (!shouldScaffold) enable();

        didPlace = false;

        Vec3 positionToRotateFrom = C.p().getPositionVector();
        if (!shouldPlaceBlock()) {
            Vec3 predictedNextPosition = getPredictedNextPosition();
            if (predictedNextPosition != null) positionToRotateFrom = predictedNextPosition;
        }

        targetBlock = getBestTargetBlock(positionToRotateFrom);
        if (targetBlock == null) return;

        if (shouldTelly() && C.p().onGround && (tellyBlockPlaced || tellyForwardTicks == 0)) {
            tellyTicksCounter = 0;
            tellyPlaceDelayCounter = 0;
        }

        tellyTicksCounter++;
        if (tellyTicksCounter <= tellyTicks) return;
        tellyPlaceDelayCounter++;

        if (shouldRotate()) rotate(positionToRotateFrom, targetBlock, event);
        if (!manualPlace) tryPlace = true;
    }

    @SubscribeEvent
    public static void onPlayerUpdate(PlayerUpdateEvent event) {
        if (!shouldScaffold()) return;

        if (C.p().onGround) {
            if (stage > 0) stage--;
            if (stage < 0) stage++;
            startY = shouldKeepY ? startY : MathHelper.floor_double(C.p().posY);
            shouldKeepY = false;
        } else if (stage > 0 && (sameY == SameYMode.AutoJump || sameY == SameYMode.On)) {
            int nextBlockY = MathHelper.floor_double(C.p().posY + C.p().motionY);
            if (nextBlockY <= startY && C.p().posY > (double) (startY + 1)) {
                shouldKeepY = true;
            }
        }

        if (InventoryUtil.isValidBlock() && crouchDownOnly && uncrouchAuto) {
            KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(false);
        }

        if (!shouldPlaceBlock() || !InventoryUtil.isValidBlock()) {
            if (overridingSneak && (C.p().onGround || !crouchInAir)) {
                KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(Keyboard.isKeyDown(C.mc.gameSettings.keyBindSneak.getKeyCode()));
                overridingSneak = false;
            }
            return;
        }

        setShouldTower();

        if (crouchOnEdge && (C.p().onGround || crouchInAir)) {
            KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(true);
            overridingSneak = true;
        }

        if (!tryPlace) return;
        tryPlace();
    }

    private static boolean tryPlace = false;

    @SubscribeEvent
    public static void onRightClick(ClickMouseEvent.Right event) {
        if (!shouldScaffold() || !InventoryUtil.isValidBlock()) return;
        if (WorldUtil.isOverAir()) {
            event.setCancelled(true);
            tryPlace = true;
        }
    }

    @SubscribeEvent
    public static void onRenderWorldEvent(RenderWorldEvent event) {
        if (showPreviousBlocks) {
            previousInteractions.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() > showPreviousBlocksTime);
            previousInteractions.forEach((blockPos, time) -> {
                double animationValue = (double) (System.currentTimeMillis() - time) / showPreviousBlocksTime;
                Render3dUtil.draw3dBox(
                        blockPos.getX(),
                        blockPos.getY(),
                        blockPos.getZ(),
                        1,
                        1,
                        1,
                        new Color(targetBlockColour.getRed(), targetBlockColour.getGreen(), targetBlockColour.getBlue(), (int) (255 * (1 - animationValue))),
                        event.partialTicks
                );
            });
        }

        if (showTargetBlock && targetBlock != null) {
            BlockPos blockPos = targetBlock.pos.offset(targetBlock.direction);
            Render3dUtil.draw3dBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, targetBlockColour, event.partialTicks);
        }
    }

    private static boolean shouldScaffold() {
        return (!crouchDownOnly || Keyboard.isKeyDown(C.mc.gameSettings.keyBindSneak.getKeyCode()))
                && (!rightClickOnly || C.mc.gameSettings.keyBindUseItem.isKeyDown())
                && (!pitchRange || (C.p().rotationPitch <= maxPitch && C.p().rotationPitch >= minPitch))
                && (!movingBackwards || Keyboard.isKeyDown(C.mc.gameSettings.keyBindBack.getKeyCode()));
    }

    private static boolean shouldTelly() {
        return rotate && mode == Mode.Telly && (!onlyWhenJumping || C.mc.gameSettings.keyBindJump.isKeyDown());
    }

    private static boolean shouldTower() {
        if (towerMode == TowerMode.Disabled) return false;
        if (C.p().onGround || MovementUtil.airTicks == 1) setShouldTower();
        return shouldTower;
    }

    private static void setShouldTower() {
        shouldTower = (!C.p().onGround || towerMode == TowerMode.Vanilla || towerMode == TowerMode.Legit)
                && (!towerPitchRange || (C.p().rotationPitch <= towerMaxPitch && C.p().rotationPitch >= towerMinPitch))
                && (!C.mc.gameSettings.keyBindJump.isKeyDown() || towerMode != TowerMode.Legit || C.p().onGround);
    }

    private static boolean shouldPlaceBlock() {
        return WorldUtil.isOverAir()
                && (C.p().onGround || !shouldKeepY || WorldUtil.isOverAir(C.p().getPositionVector().subtract(0, 1, 0)));
    }

    private static boolean shouldRotate() {
        return rotate && mode != Mode.Derp;
    }

    private static boolean shouldKeepY() {
        return sameY != SameYMode.Off && stage > 0;
    }

    private static Vec3 getPredictedNextPosition() {
        Vec3 pos = C.p().getPositionVector();
        double velocityX = C.p().posX - C.p().prevPosX;
        double velocityZ = C.p().posZ - C.p().prevPosZ;
        for (int i = 1; i <= 20; i++) {
            pos = pos.add(new Vec3(velocityX, 0, velocityZ));
            if (WorldUtil.isOverAir(pos)) return pos;
        }
        return null;
    }

    private static void tryPlace() {
        MovingObjectPosition rayTrace = WorldUtil.rayTrace(blockReach + blockReachExtend, PlayerUtil.currentRotation());
        if (rayTrace == null || rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        if (rotate && onlyPlaceBest && (targetBlock == null || !rayTrace.getBlockPos().offset(rayTrace.sideHit).equals(targetBlock.pos.offset(targetBlock.direction)))) return;
        if (!rotate && shouldKeepY() && rayTrace.sideHit == EnumFacing.UP) return;
        if (rayTrace.getBlockPos().offset(rayTrace.sideHit).getY() > C.p().posY) return;
        if (shouldTelly() && tellyPlaceDelayCounter < tellyPlaceDelay + Math.max(0, rotationTicks - 1)) return;

        if (C.mc.playerController.onPlayerRightClick(C.p(), C.w(), C.p().getHeldItem(), rayTrace.getBlockPos(), rayTrace.sideHit, rayTrace.hitVec)) {
            PlayerUtil.swingHand();
            if (InventoryUtil.isSlotEmpty(C.p().inventory.currentItem)) {
                C.p().inventory.removeStackFromSlot(C.p().inventory.currentItem);
            }

            lastPlacedDeltaX = Math.abs(PlayerUtil.currentRotation().yaw - PlayerUtil.lastRotation().yaw);
            blocksPlaced++;
            previousInteractions.put(rayTrace.getBlockPos().offset(rayTrace.sideHit), System.currentTimeMillis());
            lastPlaceStack = C.p().inventory.currentItem;
            tellyBlockPlaced = C.p().onGround;
        }
    }

    private static void rotate(Vec3 playerPosition, BlockTarget blockTarget, RotationEvent event) {
        BlockPos targetPos = blockTarget.pos.offset(blockTarget.direction);
        float yaw = (float) Math.toDegrees(Math.atan2(targetPos.getZ() + 0.5 - playerPosition.zCoord, targetPos.getX() + 0.5 - playerPosition.xCoord)) - 90.0f + yawOffset;
        float pitch = (float) -Math.toDegrees(Math.atan2(targetPos.getY() + 0.5 - (playerPosition.yCoord + C.p().getEyeHeight()),
                Math.hypot(targetPos.getX() + 0.5 - playerPosition.xCoord, targetPos.getZ() + 0.5 - playerPosition.zCoord)));
        pitch = MathHelper.clamp_float(pitch, -90.0f, 90.0f);

        if (mode == Mode.Godbridge) {
            yaw = C.p().rotationYaw - C.p().rotationYaw % 90.0f - 180.0f + 45 * (C.p().rotationYaw > 0.0f ? 1 : -1);
            pitch = 76.4f;
        } else if (mode == Mode.Breesily) {
            yaw = yaw + (float) ((Math.random() - 0.5) * 1.0);
            pitch = 80.0f + (float) ((Math.random() - 0.5) * 1.0);
        } else if (mode == Mode.Eagle) {
            pitch = 78.0f;
        } else if (mode == Mode.Snap || mode == Mode.Telly) {
            pitch = Math.max(82.0f, pitch);
        }

        RotationUtil.Rotation rotation = new RotationUtil.Rotation(pitch, yaw);
        if (rotationMode == RotationMode.Grim) {
            event.rotation = rotation;
        } else {
            event.rotation = RotationUtil.getEasedRotation(PlayerUtil.lastRotation(), rotation, com.github.scoliossis.utils.render.EasingUtil.EasingFunctions.Ease_Out_Expo, Math.min(1.0, rotationTicks / 5.0));
        }
    }

    private static BlockTarget getBestTargetBlock(Vec3 position) {
        int playerY = MathHelper.floor_double(C.p().posY);
        BlockPos targetPos = new BlockPos(position.xCoord, playerY, position.zCoord);
        BlockPos point1 = targetPos.add(-blockReach - blockReachExtend, -blockReach - blockReachExtend, -blockReach - blockReachExtend);
        BlockPos point2 = targetPos.add(blockReach + blockReachExtend, -1, blockReach + blockReachExtend);
        Iterator<BlockPos> blocksInRange = BlockPos.getAllInBox(point1, point2).iterator();

        double bestDistance = Double.MAX_VALUE;
        BlockTarget bestBlock = null;

        while (blocksInRange.hasNext()) {
            BlockPos blockPos = blocksInRange.next();
            Block currentBlock = C.w().getBlockState(blockPos).getBlock();
            if (currentBlock == null || InventoryUtil.isBlockInteractable(currentBlock) || !InventoryUtil.isSolidBlock(currentBlock)) continue;

            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.DOWN) continue;
                BlockPos blockPosOffset = blockPos.offset(facing);
                if (InventoryUtil.isSolidBlock(C.w().getBlockState(blockPosOffset).getBlock())) continue;
                if (blockPosOffset.getY() + 1 > C.p().posY) continue;

                double distance = position.distanceTo(new Vec3(blockPosOffset.getX() + 0.5, blockPosOffset.getY() + 0.5, blockPosOffset.getZ() + 0.5));
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestBlock = new BlockTarget(blockPos, facing);
                }
            }
        }

        return bestBlock;
    }

    private static boolean shouldTellyJump(boolean jumpDown) {
        return (tellyBlockPlaced && tellyForwardTicksCount >= tellyForwardTicks)
                || (jumpDown && !lastJump)
                || tellyForwardTicks == 0;
    }

    private static void enable() {
        shouldScaffold = true;
        blocksPlaced = 0;
        if (autoF5) {
            previousF5 = C.mc.gameSettings.thirdPersonView != 0;
            C.mc.gameSettings.thirdPersonView = 1;
        }
    }

    private static void disable() {
        shouldScaffold = false;
        shouldTower = false;
        targetBlock = null;
        if (previousStack != -1 && C.isInGame()) {
            C.p().inventory.currentItem = previousStack;
            previousStack = -1;
        }
        if (autoF5 && previousF5) {
            C.mc.gameSettings.thirdPersonView = 0;
        }
        overridingSneak = false;
        KeyBindingBridge.from(C.mc.gameSettings.keyBindSneak).bridge$setDown(Keyboard.isKeyDown(C.mc.gameSettings.keyBindSneak.getKeyCode()));
    }

    @Override
    protected void onEnable() {
        enable();
    }

    @Override
    protected void onDisable() {
        disable();
    }

    @AllArgsConstructor
    private static class BlockTarget {
        public BlockPos pos;
        public EnumFacing direction;
    }
}
