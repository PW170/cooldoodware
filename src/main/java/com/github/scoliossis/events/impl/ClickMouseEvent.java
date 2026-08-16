package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;

public class ClickMouseEvent {
    @AllArgsConstructor
    public static class Left extends Event  {
    }

    @AllArgsConstructor
    public static class Right extends Event  {
    }
}
