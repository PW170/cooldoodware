package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class AttackBlockEvent extends Event {
    public BlockPos pos;
}
