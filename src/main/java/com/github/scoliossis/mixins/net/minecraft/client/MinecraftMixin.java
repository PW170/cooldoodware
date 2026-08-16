package com.github.scoliossis.mixins.net.minecraft.client;

import com.github.scoliossis.bridge.net.minecraft.client.MinecraftBridge;
import com.github.scoliossis.bridge.net.minecraft.util.SessionBridge;
import com.github.scoliossis.bridge.net.minecraft.util.TimerBridge;
import com.github.scoliossis.events.Bus;
import com.github.scoliossis.events.impl.*;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.modules.impl.combat.AutoBlock;
import com.github.scoliossis.modules.impl.player.FastPlace;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.PlayerUtil;
import com.github.scoliossis.utils.minecraft.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftBridge {
    @Shadow private Timer timer;
    @Shadow private Session session;
    @Shadow protected abstract void clickMouse();
    @Shadow protected abstract void rightClickMouse();
    @Shadow public GuiScreen currentScreen;
    @Shadow public GameSettings gameSettings;
    @Shadow public boolean inGameHasFocus;
    @Shadow protected abstract void sendClickBlockToController(boolean leftClick);
    @Shadow private int rightClickDelayTimer;
    @Shadow public MovingObjectPosition objectMouseOver;

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    public void onRunTick(CallbackInfo ci) {
        Bus.post(new ClientTickEvent());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;getMouseOver(F)V"))
    private void onRunGameLoop(CallbackInfo ci) {
        if (C.isInGame()) {
            // i dont like this
            PlayerUtil.fakePlayerPosAndRot();
            PlayerUtil.setRotationEvent(new RotationEvent(RotationUtil.getCurrentClientRotation()));
            PlayerUtil.resetFakePlayerPosAndRot();
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
    private int runTick$getEventDWheel() {
        int scrollAmount = Mouse.getEventDWheel();
        if (scrollAmount != 0) {
            MouseScrolledEvent event = new MouseScrolledEvent(scrollAmount);
            Bus.post(event);

            if (event.isCancelled()) return 0;
        }

        return scrollAmount;
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V"))
    private void runTick$dispatchKeypresses(CallbackInfo ci) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (i != 0 && !Keyboard.isRepeatEvent()) {
            Bus.post(new KeyPressedEvent(i, Keyboard.getEventKeyState()));
        }
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void clickMouse(CallbackInfo ci) {
        if (Bus.post(new ClickMouseEvent.Left())) ci.cancel();
    }

    @Inject(method = "sendClickBlockToController", at = @At("HEAD"), cancellable = true)
    private void sendClickBlockToController(CallbackInfo ci) {
        if (Bus.post(new ClickMouseEvent.Left())) ci.cancel();
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isPressed()Z", ordinal = 7))
    public boolean onAttemptClick(KeyBinding instance) {
        if (!PlayerUtil.canAttack() && AutoBlock.isBlocking()) return false;

        boolean isPressed = instance.isPressed();

        if (isPressed && AutoBlock.canSwingWhileBlocking()) {
            this.clickMouse();
        }

        return isPressed;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isPressed()Z", ordinal = 10))
    public boolean onSuccessfulClick(KeyBinding instance) {
        boolean isPressed = instance.isPressed();

        if (isPressed && ModuleManager.isEnabled(AutoBlock.class)) {
            AutoBlock.swingQueued = true;
            return false;
        }

        return isPressed;
    }

    // blehhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;sendClickBlockToController(Z)V"))
    public void onSuccessfulClick(Minecraft instance, boolean b) {
        if (ModuleManager.isEnabled(AutoBlock.class)) {
            AutoBlock.clickBlockQueued = true;
        }
        else {
            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
        }
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isEntityInsideOpaqueBlock()Z"))
    private boolean overrideCanF5inBlocks(EntityPlayerSP instance) {
        if (PlayerUtil.noClipRender) return false;
        return C.p().isEntityInsideOpaqueBlock();
    }

    @Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", shift = At.Shift.AFTER), cancellable = true)
    private void onRightClickMouse(CallbackInfo ci) {
        this.rightClickDelayTimer = FastPlace.getPlaceDelay();
        if (Bus.post(new ClickMouseEvent.Right())) ci.cancel();
    }

    public TimerBridge bridge$getTimer() {
        return TimerBridge.from(this.timer);
    }

    public void bridge$setSession(SessionBridge session) {
        this.session = (Session) session;
    }

    public void bridge$clickMouse() {
        this.clickMouse();
    }

    public void bridge$rightClickMouse() {
        this.rightClickMouse();
    }

    public void bridge$sendClickBlockToController(boolean leftClick) {
        this.sendClickBlockToController(leftClick);
    }
}
