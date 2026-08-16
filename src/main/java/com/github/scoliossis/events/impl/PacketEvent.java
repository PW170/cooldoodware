package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;
import net.minecraft.network.Packet;

// https://www.youtube.com/watch?v=g9xopViBKOQ
public class PacketEvent {
    @AllArgsConstructor
    public static class Send extends Event  {
        public Packet<?> packet;
    }

    @AllArgsConstructor
    public static class Receive extends Event  {
        public Packet<?> packet;
    }
}