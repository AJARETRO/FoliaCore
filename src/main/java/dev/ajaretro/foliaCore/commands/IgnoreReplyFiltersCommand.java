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
import org.bukkit.entity.Player;

public class IgnoreReplyFiltersCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public IgnoreReplyFiltersCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("msgtoggle")) {
            if (!player.hasPermission("foliacore.msgtoggle")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            boolean disabled = plugin.getChatManager().toggleMsg(player.getUniqueId());
            plugin.getMessenger().sendSuccess(player, "Private messaging is now " + (disabled ? "§cDISABLED" : "§aENABLED") + "§a.");
            return true;
        }

        if (cmd.equals("rtoggle")) {
            if (!player.hasPermission("foliacore.rtoggle")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            boolean disabled = plugin.getChatManager().toggleReplyToggle(player.getUniqueId());
            plugin.getMessenger().sendSuccess(player, "Replies are now " + (disabled ? "§cDISABLED" : "§aENABLED") + "§a.");
            return true;
        }

        return true;
    }
}
