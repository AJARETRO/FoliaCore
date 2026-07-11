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

public class ChatShoutCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public ChatShoutCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isChatEnabled()) {
            plugin.getMessenger().sendError(sender, "Chat module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.toggleshout")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        boolean shout = plugin.getChatManager().toggleShout(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Shout mode is now " + (shout ? "§aENABLED" : "§cDISABLED") + "§a.");
        return true;
    }
}
