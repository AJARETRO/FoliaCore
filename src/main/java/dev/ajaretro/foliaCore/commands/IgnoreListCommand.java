/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IgnoreListCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public IgnoreListCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.ignorelist")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        Set<UUID> ignoredUUIDs = plugin.getIgnoreManager().getIgnoredPlayers(player.getUniqueId());
        if (ignoredUUIDs.isEmpty()) {
            plugin.getMessenger().sendMessage(player, "§aYou are not ignoring anyone.");
            return true;
        }

        List<String> names = new ArrayList<>();
        for (UUID uuid : ignoredUUIDs) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.getName() != null) {
                names.add(op.getName());
            } else {
                names.add(uuid.toString());
            }
        }

        plugin.getMessenger().sendSuccess(player, "Ignored players: §f" + String.join(", ", names));
        return true;
    }
}
