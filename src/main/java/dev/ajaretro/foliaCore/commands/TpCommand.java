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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TpCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.tp")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        Player executor;
        Player target;

        if (args.length == 1) {
            // /tp <player> - teleport self to player
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /tp <player> [target]");
                return true;
            }
            executor = (Player) sender;
            target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(executor, "Player not found or is not online.");
                return true;
            }

            plugin.getTeleportManager().setLastLocation(executor.getUniqueId(), executor.getLocation());
            plugin.getTeleportManager().startTeleport(executor, target.getLocation(), "You have been teleported to " + ChatColor.GOLD + target.getName());

        } else if (args.length == 2) {
            // /tp <player> <target> - teleport player to target
            if (!sender.hasPermission("foliacore.tp.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to teleport other players.");
                return true;
            }

            executor = Bukkit.getPlayer(args[0]);
            target = Bukkit.getPlayer(args[1]);

            if (executor == null || !executor.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }

            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(sender, "Target player not found or is not online.");
                return true;
            }

            plugin.getTeleportManager().setLastLocation(executor.getUniqueId(), executor.getLocation());
            plugin.getTeleportManager().startTeleport(executor, target.getLocation(), "You have been teleported to " + ChatColor.GOLD + target.getName());
            plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + executor.getName() + ChatColor.GREEN + " has been teleported to " + ChatColor.GOLD + target.getName());

        } else {
            plugin.getMessenger().sendError(sender, "Usage: /tp <player> [target]");
            return true;
        }

        return true;
    }
}
