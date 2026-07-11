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

import java.util.Set;

public class JailsCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public JailsCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.jails")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        Set<String> jailNames = plugin.getJailManager().getJails().keySet();
        if (jailNames.isEmpty()) {
            plugin.getMessenger().sendMessage(sender, "§cNo jails are currently defined.");
            return true;
        }

        plugin.getMessenger().sendSuccess(sender, "Available jails: §f" + String.join(", ", jailNames));
        return true;
    }
}
