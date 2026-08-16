package com.github.scoliossis.modules.impl.combat;

import com.github.scoliossis.bridge.net.minecraft.client.multiplayer.PlayerControllerMPBridge;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.ClickMouseEvent;
import com.github.scoliossis.events.impl.MotionEvent;
import com.github.scoliossis.events.impl.PlayerUpdateEvent;
import com.github.scoliossis.events.impl.RotationEvent;
import com.github.scoliossis.modules.*;
import com.github.scoliossis.modules.impl.player.Fucker;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.client.MathUtil;
import com.github.scoliossis.utils.minecraft.*;
import com.github.scoliossis.utils.render.EasingUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RegisterModule(
        name = "Kill Aura",
        description = "Now I Am Become Death, the Destroyer of Worlds.",
        category = Category.COMBAT
)
public class KillAura extends Module {
    // filler settings from my other client
    @RegisterSubModule(name = "Range")
    public SubCategory rangeSubcategory = new SubCategory();

    @RegisterSubModule(name = "Rotation Range", parent = "Range", max = 8)
    public static double killAuraRotationRange = 5;

    @RegisterSubModule(name = "Attack Range", parent = "Range", min = 1, max = 6)
    public static double killAuraAttackRange = 3.1;

    @RegisterSubModule(name = "FOV", parent = "Range", min = 1, max = 180)
    public static double FOV = 180;

    @RegisterSubModule(name = "Targeting")
    public SubCategory killAuraTargetingSubCategory = new SubCategory();

    @RegisterSubModule(name = "Through Walls", parent = "Targeting")
    public static boolean throughWalls = false;

    @RegisterSubModule(name = "Target Choice", parent = "Targeting")
    public static KillAuraTargeting killAuraTarget = KillAuraTargeting.Best;
    public enum KillAuraTargeting {
        Switch, Best, Single
    }

    @RegisterSubModule(name = "Target Sorting", parent = "Target Choice", modeParentString = {"Best", "Single"})
    public static KillAuraSorting killAuraSorting = KillAuraSorting.Health;
    public enum KillAuraSorting {
        Distance, Hurt_Time, Health
    }

    @RegisterSubModule(name = "Rotation")
    public SubCategory rotationsSubcategory = new SubCategory();

    @RegisterSubModule(name = "Rotation Mode", parent = "Rotation")
    public static KillAuraRotations rotations = KillAuraRotations.Smooth;
    public enum KillAuraRotations {
        Simple, Smooth, Eased, Snap, Manual, None
    }

    @RegisterSubModule(name = "Min Rotation", parent = "Rotation Mode", modeParentString = "Simple", min = 0, max = 180)
    public static float minRotation = 0;

    @RegisterSubModule(name = "Max Rotation", parent = "Rotation Mode", modeParentString = "Simple", min = 1, max = 180)
    public static float maxRotation = 100;

    @RegisterSubModule(name = "Rotation Smoothing", parent = "Rotation Mode", modeParentString = "Smooth", min = 1, max = 50)
    public static float smoothRotationSpeed = 1;

    @RegisterSubModule(name = "Easing", parent = "Rotation Mode", modeParentString = "Eased")
    public static EasingUtil.EasingFunctions easingFunction = EasingUtil.EasingFunctions.Ease_In_Out_Sine;

    @RegisterSubModule(name = "Easing Ticks", parent = "Rotation Mode", modeParentString = "Eased", min = 3, max = 20)
    public static int easingTicks = 10;

    @RegisterSubModule(name = "Client Side", parent = "Rotation Mode", modeParentString = {"Simple", "Smooth", "Eased"})
    public static boolean clientSideRotation = true;

    @RegisterSubModule(name = "Only Necessary", parent = "Rotation Mode", modeParentString = {"Simple", "Smooth", "Eased"}, description = "Only changes rotation if not currently looking at a valid target")
    public static boolean onlyNecessary = true;

    @RegisterSubModule(name = "Jitter Pitch", parent = "Rotation Mode", modeParentString = {"Simple", "Smooth", "Eased", "Snap"}, description = "Pitch Moves Up And Down")
    public static boolean jitterPitch = true;
    @RegisterSubModule(name = "Jitter Ticks", parent = "Jitter Pitch", min = 2, max = 500)
    public static int pitchJitter = 20;
    @RegisterSubModule(name = "Jitter Size", parent = "Jitter Pitch", min = 0.1, max = 1.5)
    public static double jitterSize = 0.3;

    @RegisterSubModule(name = "Random Point", parent = "Rotation Mode", modeParentString = {"Simple", "Smooth", "Eased", "Snap"}, description = "Picks a random valid roations instead of always the closest")
    public static boolean randomValidRotation = true;

    @RegisterSubModule(name = "Attacking")
    public SubCategory attackingSubCat = new SubCategory();

    @RegisterSubModule(name = "Swing Misses", parent = "Attacking", description = "Swings even if it won't do damage")
    public static boolean swingMisses = true;
    @RegisterSubModule(name = "Swords Only", parent = "Attacking")
    public static boolean swordOnlyAura = true;
    @RegisterSubModule(name = "Left Click Only", parent = "Attacking", description = "Only enables when left click is held down")
    public static boolean leftClickDownOnly = true;
    @RegisterSubModule(name = "Not While Mining", parent = "Attacking", description = "Only enables when not mining a block")
    public static boolean noMine = true;
    @RegisterSubModule(name = "No GUI", parent = "Attacking", description = "Disables aura while in any gui")
    public static boolean noGUI = true;

    @RegisterSubModule(name = "CPS Min", min = 0, max = 20, parent = "Attacking")
    public static double cpsMin = 5;
    @RegisterSubModule(name = "CPS Max", min = 0, max = 20, parent = "Attacking")
    public static double cpsMax = 10;

    private static int nextAttackTick = -1;
    private static int easedRotationTick = 1;

    private static EntityLivingBase lastTarget = null;
    private static int switchTargetIndex = 0;
    private static RotationUtil.Rotation lastRotation = null;

    // goes before scaffold because its less important imo
    @SubscribeEvent(priority = 999)
    public static void onRotationEvent(RotationEvent event) {
        if (!shouldRotate()) {
            lastRotation = null;
            easedRotationTick = 0;
            return;
        }

        EntityLivingBase prevTarget = lastTarget;
        EntityLivingBase target = getTarget();

        if (target == null || !shouldRotateToEntity(target)) {
            lastRotation = null;
            easedRotationTick = 0;
            return;
        }

        if (target != prevTarget) easedRotationTick = 0;

        lastRotation = getRotation(target);
        event.rotation = new RotationUtil.Rotation(lastRotation.pitch, lastRotation.yaw);
    }

    @SubscribeEvent
    public static void tryAttackTarget(PlayerUpdateEvent event) {
        Entity target = WorldUtil.getMouseOver(PlayerUtil.currentRotation(), killAuraAttackRange, throughWalls);

        if (target == lastTarget) easedRotationTick-=2;
        if (!shouldAttack()) return;

        if (rotations == KillAuraRotations.None) {
            EntityLivingBase bestTarget = getTarget();

            if (bestTarget != null && shouldAttackEntity(bestTarget)) attack(bestTarget);
            return;
        }

        if (!TargetUtil.isValidTarget(target, false)) {
            if (swingMisses) PlayerUtil.swingHand();
            return;
        }

        attack(target);
    }

    private static boolean willSwing() {
        if (!shouldAttack()) return false;
        if (rotations == KillAuraRotations.None) return true;

        if (!TargetUtil.isValidTarget(WorldUtil.getMouseOver(PlayerUtil.currentRotation(), killAuraAttackRange, throughWalls), false)) {
            return swingMisses;
        }

        return true;
    }

    @SubscribeEvent
    public static void onPlayerMotion(MotionEvent event) {
        if (lastTarget != null && lastRotation != null && clientSideRotation && rotations != KillAuraRotations.Snap && shouldRotate() && shouldRotateToEntity(lastTarget)) {
            C.p().rotationYaw = RotationUtil.applyWrap360(C.p().rotationYaw, lastRotation.yaw);
            C.p().rotationPitch = lastRotation.pitch;
        }
    }

    @SubscribeEvent
    public static void clickMouseEvent(ClickMouseEvent.Left event) {
        if (willSwing()) {
            event.setCancelled(true);
        }
    }

    private static boolean shouldAura() {
        return (!swordOnlyAura || InventoryUtil.getHeldItem() instanceof ItemSword)
                && (Fucker.getCurrentTarget() == null || !Fucker.noKillAura)
                && (!leftClickDownOnly || Mouse.isButtonDown(0))
                && (!noGUI || C.mc.currentScreen == null)
                && (!noMine || PlayerControllerMPBridge.from(C.mc.playerController).bridge$getCurBlockDamageMP() == 0);
    }

    private static boolean shouldAttack() {
        return lastTarget != null
                && shouldAura()
                && MovementUtil.ticks >= nextAttackTick
                && PlayerUtil.canAttack()
                && (cpsMin != 0 || cpsMax != 0)
                && !Fucker.shouldRotate()
                && (isTargetInFOV(lastTarget) || TargetUtil.isValidTarget(WorldUtil.getMouseOver(PlayerUtil.currentRotation(), killAuraAttackRange, throughWalls), false));
    }

    private static EntityLivingBase getTarget() {
        List<EntityLivingBase> sortedTargets = TargetUtil.getPossibleTargets(
                        Math.max(killAuraRotationRange, killAuraAttackRange),
                        throughWalls,
                        rotations != KillAuraRotations.None
                )
                .stream()
                .sorted(Comparator.comparingDouble(TargetUtil::getDistanceToEntity))
                .sorted(Comparator.comparingDouble(entity -> {
                    if (killAuraTarget == KillAuraTargeting.Switch) return entity.getEntityId();

                    switch (killAuraSorting) {
                        case Distance:
                            return TargetUtil.getDistanceToEntity(entity);
                        case Health:
                            return entity.getHealth();
                        case Hurt_Time:
                            return entity.hurtTime;
                        default:
                            return 0;
                    }
                })).collect(Collectors.toList());

        if (sortedTargets.isEmpty()) {
            lastTarget = null;
            return null;
        }

        int targetIndex = 0;
        switch (killAuraTarget) {
            case Single:
                targetIndex =
                        (lastTarget != null && sortedTargets.contains(lastTarget) && shouldAttackEntity(lastTarget))
                        ? sortedTargets.indexOf(lastTarget)
                        : 0;
                break;
            case Switch:
                switchTargetIndex = switchTargetIndex % sortedTargets.size();
                int prevSwitchIndex = switchTargetIndex;

                // dont switch onto a target out of range
                while (!shouldAttackEntity(sortedTargets.get(switchTargetIndex))) {
                    switchTargetIndex = (switchTargetIndex + 1) % sortedTargets.size();
                    if (switchTargetIndex == prevSwitchIndex) break;
                }

                targetIndex = switchTargetIndex;
                break;
        }

        lastTarget = sortedTargets.get(targetIndex);
        return lastTarget;
    }

    private static boolean shouldAttackEntity(EntityLivingBase EntityLivingBase) {
        return TargetUtil.getDistanceToEntity(EntityLivingBase) <= killAuraAttackRange;
    }

    private static boolean shouldRotate() {
        return shouldAura()
                && (rotations != KillAuraRotations.Snap || shouldAttack())
                && rotations != KillAuraRotations.None
                && rotations != KillAuraRotations.Manual;
    }

    private static boolean shouldRotateToEntity(EntityLivingBase entity) {
        return TargetUtil.getDistanceToEntity(entity) <= killAuraRotationRange && isTargetInFOV(entity);
    }

    private static boolean isTargetInFOV(EntityLivingBase entity) {
        Vec3 rotationPoint = TargetUtil.getTargetRotationPoint(entity, killAuraRotationRange, throughWalls, randomValidRotation);
        if (rotationPoint == null) return false;

        return Math.abs(Math.abs(RotationUtil.getCurrentClientRotation().yaw) - Math.abs(RotationUtil.getRotation(rotationPoint).yaw)) % 180 <= FOV;
    }

    private static RotationUtil.Rotation getRotation(EntityLivingBase entity) {
        Vec3 targetRotationPoint = TargetUtil.getTargetRotationPoint(entity, killAuraRotationRange, throughWalls, randomValidRotation);

        // should only be null if rotationRange is lower than attack range
        if (targetRotationPoint == null) return PlayerUtil.currentRotation();

        double extraYcoord = jitterPitch ? EasingUtil.EasingFunctions.Ease_In_Out_Sine.ease((MovementUtil.ticks % pitchJitter) / (pitchJitter/2d)) * jitterSize : 0;
        targetRotationPoint = targetRotationPoint.addVector(0, targetRotationPoint.yCoord >= entity.getEntityBoundingBox().maxY - entity.height / 2 ? -extraYcoord : extraYcoord, 0);

        if (onlyNecessary && TargetUtil.isValidTarget(WorldUtil.getMouseOver(getCurrentRotation(), killAuraAttackRange, throughWalls), false))
            return getCurrentRotation();

        RotationUtil.Rotation targetRotation = RotationUtil.getRotation(getCurrentRotation(), C.p().getPositionEyes(1), targetRotationPoint);

        switch (rotations) {
            case Simple:
                return RotationUtil.getLimitedRotation(getCurrentRotation(), targetRotation, MathUtil.getRandomInRange(minRotation, maxRotation));
            case Smooth:
                return RotationUtil.getSmoothRotation(getCurrentRotation(), targetRotation, smoothRotationSpeed);
            case Eased:
                if (easedRotationTick < 0) easedRotationTick = 1;
                if (easedRotationTick < easingTicks) easedRotationTick++;
                return RotationUtil.getEasedRotation(getCurrentRotation(), targetRotation, easingFunction, (double) easedRotationTick / easingTicks);
            default:
                return RotationUtil.applyGcd(getCurrentRotation(), targetRotation);
        }
    }

    private static RotationUtil.Rotation getCurrentRotation() {
        return clientSideRotation ? RotationUtil.getCurrentClientRotation() : PlayerUtil.lastRotation();
    }

    private static double lostPrecision = 0;
    private static void attack(Entity target) {
        if (PlayerUtil.attack(target)) {
            int nextCps = (int) MathUtil.getRandomInRange(cpsMin, cpsMax);

            double ticksTillNextAttack = 20d / (Math.max(nextCps, 1)) + lostPrecision;
            lostPrecision += ticksTillNextAttack - Math.floor(ticksTillNextAttack);
            while (lostPrecision >= 1) lostPrecision--;
            ticksTillNextAttack = Math.floor(ticksTillNextAttack);

            nextAttackTick = (int) (MovementUtil.ticks + ticksTillNextAttack);
            switchTargetIndex += 1;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
