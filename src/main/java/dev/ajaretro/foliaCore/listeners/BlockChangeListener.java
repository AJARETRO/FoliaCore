/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Monitors block changes to detect raid/spam behavior.
 */
public class BlockChangeListener implements Listener {

    private final FoliaCore plugin;

    public BlockChangeListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().antiRaidEnabled) return;

        Player player = event.getPlayer();
        if (player.hasPermission("foliacore.admin.antiraid")) return; // Admins exempt

        if (!plugin.getAntiRaidManager().canModifyBlocks(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessenger().sendError(player, "You are locked down and cannot modify blocks.");
            return;
        }

        plugin.getAntiRaidManager().trackBlockChange(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigManager().antiRaidEnabled) return;

        Player player = event.getPlayer();
        if (player.hasPermission("foliacore.admin.antiraid")) return; // Admins exempt

        if (!plugin.getAntiRaidManager().canModifyBlocks(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessenger().sendError(player, "You are locked down and cannot modify blocks.");
            return;
        }

        plugin.getAntiRaidManager().trackBlockChange(player.getUniqueId());
    }
}
