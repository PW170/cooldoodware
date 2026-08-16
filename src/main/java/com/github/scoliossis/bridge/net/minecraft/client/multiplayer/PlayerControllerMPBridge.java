package com.github.scoliossis.bridge.net.minecraft.client.multiplayer;

public interface PlayerControllerMPBridge {
    static PlayerControllerMPBridge from(Object instance) {
        return (PlayerControllerMPBridge) instance;
    }

    float bridge$getCurBlockDamageMP();
}
