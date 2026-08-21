package com.github.scoliossis.mixins.net.minecraft.util;

import com.github.scoliossis.bridge.net.minecraft.util.SessionBridge;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Session.class)
public class SessionMixin implements SessionBridge {
}