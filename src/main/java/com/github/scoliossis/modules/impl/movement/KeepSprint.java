package com.github.scoliossis.modules.impl.movement;

import com.github.scoliossis.modules.*;

@RegisterModule(
        name = "Keep Sprint",
        description = "todo: funny discripton soon",
        category = Category.MOVEMENT,
        dangerous = true
)
public class KeepSprint extends Module {
    @RegisterSubModule(name = "Keep Sprint")
    public static boolean keepSprint = true;

    @RegisterSubModule(name = "Keep Motion")
    public static boolean keepMotion = true;

    public static boolean shouldKeepSprint() {
        return ModuleManager.isEnabled(KeepSprint.class) && keepSprint;
    }

    public static boolean shouldKeepMotion() {
        return ModuleManager.isEnabled(KeepSprint.class) && keepMotion;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
