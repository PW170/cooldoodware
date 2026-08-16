package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;

@RegisterModule(
        name = "No FOV",
        description = "Removes stupid fov multipliers because i want my fov to be constant leave me alone.",
        category = Category.RENDER
)
public class NoFOV extends Module {
    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
