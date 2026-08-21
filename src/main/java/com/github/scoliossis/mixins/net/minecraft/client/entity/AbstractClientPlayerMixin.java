package com.github.scoliossis.mixins.net.minecraft.client.entity;

import com.github.scoliossis.bridge.net.minecraft.client.entity.AbstractClientPlayerBridge;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.modules.impl.render.NoFOV;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin implements AbstractClientPlayerBridge {
    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(at = @At("HEAD"), method = "getFovModifier", cancellable = true)
    public void getFovModifier(CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.isEnabled(NoFOV.class)) cir.setReturnValue(1f);
    }

    @Override
    public NetworkPlayerInfo bridge$getPlayerInfo() {
        return this.getPlayerInfo();
    }
}
