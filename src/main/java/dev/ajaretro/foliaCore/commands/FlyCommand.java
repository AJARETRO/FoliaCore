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
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class FlyCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public FlyCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /fly [player]");
                return true;
            }
            player = (Player) sender;
            if (!player.hasPermission("foliacore.fly")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
        } else {
            if (!sender.hasPermission("foliacore.fly.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
            if (player == null || !player.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }
        }

        boolean newFlying = !player.getAllowFlight();
        player.setAllowFlight(newFlying);
        
        if (newFlying) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Flight has been enabled.");
            if (!player.equals(sender)) {
                plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + "'s flight has been enabled.");
            }
        } else {
            player.setFlying(false);
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Flight has been disabled.");
            if (!player.equals(sender)) {
                plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + "'s flight has been disabled.");
            }
        }

        return true;
    }
}
