/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PTimeCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public PTimeCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.ptime")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /ptime <reset|day|night|dawn|dusk|time>");
            return true;
        }

        String arg = args[0].toLowerCase();
        if (arg.equals("reset") || arg.equals("normal")) {
            player.resetPlayerTime();
            plugin.getMessenger().sendSuccess(player, "Your time has been reset to synchronize with the server.");
            return true;
        }

        long timeTicks;
        switch (arg) {
            case "day":
                timeTicks = 1000L;
                break;
            case "night":
                timeTicks = 13000L;
                break;
            case "dawn":
            case "morning":
                timeTicks = 0L;
                break;
            case "dusk":
            case "evening":
                timeTicks = 12000L;
                break;
            default:
                try {
                    timeTicks = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    plugin.getMessenger().sendError(player, "Invalid time value. Use reset, day, night, dawn, dusk, or a number.");
                    return true;
                }
                break;
        }

        player.setPlayerTime(timeTicks, false);
        plugin.getMessenger().sendSuccess(player, "Your time has been set to: " + arg);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.ptime")) {
            return List.of("reset", "day", "night", "dawn", "dusk");
        }
        return Collections.emptyList();
    }
}
