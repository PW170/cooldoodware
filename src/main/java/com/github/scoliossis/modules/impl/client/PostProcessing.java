package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;

@RegisterModule(
        enabledByDefault = true,
        name = "PostProcessing",
        description = "Adds blur and bloom effects.",
        category = Category.CLIENT
)
public class PostProcessing extends Module {
    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
