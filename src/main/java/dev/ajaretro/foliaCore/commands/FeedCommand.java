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

public class FeedCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public FeedCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /feed [player]");
                return true;
            }
            player = (Player) sender;
            if (!player.hasPermission("foliacore.feed")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
        } else {
            if (!sender.hasPermission("foliacore.feed.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
            if (player == null || !player.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }
        }

        player.setFoodLevel(20);
        player.setSaturation(20);
        
        plugin.getMessenger().sendSuccess(player, ChatColor.GREEN + "You have been fed!");
        if (!player.equals(sender)) {
            plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has been fed.");
        }

        return true;
    }
}
