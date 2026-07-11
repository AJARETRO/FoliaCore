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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnjailCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public UnjailCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.unjail")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(sender, "Usage: /unjail <player>");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        if (!plugin.getJailManager().isJailed(target.getUniqueId())) {
            plugin.getMessenger().sendError(sender, "This player is not jailed.");
            return true;
        }

        plugin.getJailManager().unjailPlayer(target.getUniqueId());
        plugin.getMessenger().sendSuccess(sender, "Successfully unjailed " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.unjail")) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (plugin.getJailManager().isJailed(p.getUniqueId()) && p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
