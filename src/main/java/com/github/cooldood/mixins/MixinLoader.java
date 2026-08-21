package com.github.cooldood.mixins;

import com.github.cooldood.Main;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoader implements IFMLLoadingPlugin {
    public MixinLoader() {
        Main.LOGGER.info("[{}] mixins initializing!", Main.MOD_NAME);
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins." + Main.MOD_ID + ".json");
        Main.LOGGER.info("[{}] mixins up!", Main.MOD_NAME);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
