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

/**
 * Displays the latency (ping) of a player.
 */
public class PingCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public PingCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.ping")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /ping [player]");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }

            if (!sender.hasPermission("foliacore.ping.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to check other players' ping.");
                return true;
            }
        }

        int ping = target.getPing();
        String pingColor = getPingColor(ping);

        if (target.equals(sender)) {
            plugin.getMessenger().sendSuccess(sender, "Your ping: " + pingColor + ping + "ms");
        } else {
            plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " ping: " + pingColor + ping + "ms");
        }

        return true;
    }

    private String getPingColor(int ping) {
        if (ping <= 50) return ChatColor.GREEN.toString();
        if (ping <= 100) return ChatColor.YELLOW.toString();
        if (ping <= 200) return ChatColor.GOLD.toString();
        return ChatColor.RED.toString();
    }
}
