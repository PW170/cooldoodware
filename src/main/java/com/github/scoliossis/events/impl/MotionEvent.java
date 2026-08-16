package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;
import net.minecraft.util.Vec3;

@AllArgsConstructor
public class MotionEvent extends Event {
    public Vec3 pos;

    public boolean sneaking;
    public boolean sprinting;
    public boolean onGround;
}
