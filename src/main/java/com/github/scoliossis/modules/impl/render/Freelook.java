package com.github.scoliossis.modules.impl.render;

import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.utils.client.C;
import com.github.scoliossis.utils.minecraft.PlayerUtil;
import com.github.scoliossis.utils.minecraft.RotationUtil;

@RegisterModule(
        name = "Freelook",
        description = "Yeah, I'm out that Brooklyn, now I'm down in Tribeca Right next to De Niro, but I'll be hood forever",
        category = Category.RENDER
)
// todo: fix it setting rotation after its changed by server
public class Freelook extends Module {
    private int lastPerspective = 0;

    @Override
    protected void onEnable() {
        if (!C.isInGame() || ModuleManager.isEnabled(Freecam.class)) {
            this.toggle();
            return;
        }

        PlayerUtil.fakeRotation = PlayerUtil.realRotation = RotationUtil.getCurrentClientRotation();
        lastPerspective = C.mc.gameSettings.thirdPersonView;
        C.mc.gameSettings.thirdPersonView = 1;
    }

    @Override
    protected void onDisable() {
        if (!C.isInGame() || ModuleManager.isEnabled(Freecam.class)) return;

        if (C.mc.gameSettings.thirdPersonView == 1)
            C.mc.gameSettings.thirdPersonView = lastPerspective;

        // reset fake camera
        PlayerUtil.fakeRotation = null;
    }
}
