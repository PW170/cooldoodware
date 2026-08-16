package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;

@RegisterModule(
        name = "Item Physics",
        description = "Makes dropped items have physics",
        category = Category.RENDER
)
public class ItemPhysics extends Module {
    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
