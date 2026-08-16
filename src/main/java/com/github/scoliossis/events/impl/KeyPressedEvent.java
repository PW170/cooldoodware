package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyPressedEvent extends Event {
    public int keyCode;
    public boolean pressed;
}
