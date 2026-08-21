package com.github.scoliossis.bridge.net.minecraft.client.settings;

public interface KeyBindingBridge {
    static KeyBindingBridge from(Object instance) {
        return (KeyBindingBridge) instance;
    }

    void bridge$setDown(boolean pressed);
}