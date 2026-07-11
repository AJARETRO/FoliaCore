/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * Toggles scoreboard (sidebar) visibility for players.
 * Allows players to hide or show their sidebar display.
 */
public class ScoreboardToggleCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public ScoreboardToggleCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.scoreboard.toggle")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        // Toggle the state
        boolean newState = !plugin.getDisplayManager().isSidebarEnabled(player.getUniqueId());
        plugin.getDisplayManager().setSidebarEnabled(player.getUniqueId(), newState);
        plugin.getDisplayManager().refreshPlayer(player);

        if (newState) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Scoreboard is now " + ChatColor.GREEN + "visible.");
        } else {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Scoreboard is now " + ChatColor.RED + "hidden.");
        }

        return true;
    }

    /**
     * Check if the player has their scoreboard enabled.
     * @param playerUuid the player's UUID
     * @return true if scoreboard is enabled (visible), false if hidden
     */
    public boolean isScoreboardEnabled(UUID playerUuid) {
        return plugin.getDisplayManager().isSidebarEnabled(playerUuid);
    }

    /**
     * Set scoreboard state for a player.
     * @param playerUuid the player's UUID
     * @param enabled true to show, false to hide
     */
    public void setScoreboardEnabled(UUID playerUuid, boolean enabled) {
        plugin.getDisplayManager().setSidebarEnabled(playerUuid, enabled);
    }
}
