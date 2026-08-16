package com.github.scoliossis;

import com.github.scoliossis.commands.CommandManager;
import com.github.scoliossis.modules.ModuleManager;
import com.github.scoliossis.utils.alts.microsoft.AuthServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

@Mod(modid = Main.MOD_ID, name = Main.MOD_NAME, version = Main.MOD_VERSION)
public class Main {
    public static final String MOD_ID = "@MOD_ID@";
    public static final String MOD_NAME = "@MOD_NAME@";
    public static final String MOD_VERSION = "@MOD_VERSION@";

    public static final String baseConfig = "base";

    public static final String baseFolderPath = "config/" + Main.MOD_ID + "/";
    public static final String configPath = baseFolderPath + "config/";
    public static final String configExtension = ".cfg";
    public static final String extraSavedFeaturesPath = baseFolderPath + "extras/";

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        Display.setTitle(MOD_NAME + " " + MOD_VERSION);

        ModuleManager.init();
        CommandManager.init();

        new AuthServer();
    }
}
