package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import com.github.scoliossis.utils.minecraft.RotationUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RotationEvent extends Event {
    public RotationUtil.Rotation rotation;
}
