package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MouseScrolledEvent extends Event {
    public int scrollAmount;
}
