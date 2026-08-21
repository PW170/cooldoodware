package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;

// nice name scale
@AllArgsConstructor
public class EventEvent extends Event {
    public Event event;
}
