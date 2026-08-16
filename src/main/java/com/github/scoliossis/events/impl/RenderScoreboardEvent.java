package com.github.scoliossis.events.impl;

import com.github.scoliossis.events.Event;
import lombok.AllArgsConstructor;
import net.minecraft.scoreboard.ScoreObjective;

@AllArgsConstructor
public class RenderScoreboardEvent extends Event {
    public ScoreObjective scoreObjective;
}
