/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public WarpCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /warp <name>");
            return true;
        }

        String warpName = args[0];
        Warp warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            plugin.getMessenger().sendError(player, "Warp '" + ChatColor.GOLD + warpName + ChatColor.RED + "' not found.");
            return true;
        }

        // Check for specific warp permission
        String perm = "foliacore.warp." + warp.getName().toLowerCase();
        if (!player.hasPermission("foliacore.warp.all") && !player.hasPermission(perm)) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this warp.");
            return true;
        }

        Location location = warp.toLocation();
        if (location == null) {
            plugin.getMessenger().sendError(player, "The world for this warp is not loaded! Please contact an admin.");
            return true;
        }

        // We re-use our safe TeleportManager!
        String successMsg = "Teleported to warp '" + ChatColor.GOLD + warp.getName() + ChatColor.GREEN + "'.";
        plugin.getTeleportManager().startTeleport(player, location, successMsg);
        return true;
    }
}