package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;

@RegisterModule(
        name = "Cool Main Menu",
        description = "Provides Cool Main Menu functionality for the client.",
        category = Category.CLIENT,
        enabledByDefault = true
)
public class CoolMainMenu extends Module {
    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
