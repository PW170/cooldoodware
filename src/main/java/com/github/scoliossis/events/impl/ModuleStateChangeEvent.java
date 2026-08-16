package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import com.github.scoliossis.modules.Module;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModuleStateChangeEvent extends Event {
    public final Module module;
    public final boolean state;
}
