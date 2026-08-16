package com.github.scoliossis.commands.impl;

import com.github.scoliossis.modules.impl.client.AutoQueueHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class AutoQueueCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "autoqueue";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autoqueue on|off|mode bedwars|skywars";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) return;

        String action = args[0].toLowerCase();
        switch (action) {
            case "on":
                AutoQueueHandler.setEnabled(true);
                break;
            case "off":
                AutoQueueHandler.setEnabled(false);
                break;
            case "mode":
                if (args.length >= 2) AutoQueueHandler.setGameMode(args[1]);
                break;
            case "delay":
                if (args.length >= 2) {
                    try {
                        AutoQueueHandler.setDelayMs(Integer.parseInt(args[1]));
                    } catch (NumberFormatException ignored) { }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
