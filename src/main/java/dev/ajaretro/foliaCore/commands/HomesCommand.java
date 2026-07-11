/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Home;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;

public class HomesCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public HomesCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.homes.list")) {
            plugin.getMessenger().sendError(player, "You do not have permission to list your homes.");
            return true;
        }

        Map<String, Home> homes = tm.getHomes(player.getUniqueId());
        int maxHomes = tm.getMaxHomes(player);

        if (homes.isEmpty()) {
            plugin.getMessenger().sendMessage(player, "You have no homes set.");
            return true;
        }

        String homesList = homes.keySet().stream()
                .sorted()
                .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.WHITE));

        plugin.getMessenger().sendMessage(player, "Your Homes (" + homes.size() + "/" + (maxHomes == Integer.MAX_VALUE ? "Unlimited" : maxHomes) + "): " + ChatColor.WHITE + homesList);
        return true;
    }
}