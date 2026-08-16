package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RenderWorldEvent extends Event {
    public float partialTicks;
}
