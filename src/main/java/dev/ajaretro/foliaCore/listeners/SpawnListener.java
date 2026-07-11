/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Handles first spawn teleportation for new players and respawn logic.
 * Uses region-aware scheduling for Folia compatibility.
 */
public class SpawnListener implements Listener {

    private final FoliaCore plugin;

    public SpawnListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigManager().isFirstSpawnEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        // Check if player is new (no homes or is joining for first time)
        if (player.hasPlayedBefore()) {
            return;
        }

        Location firstSpawn = plugin.getTeleportManager().getFirstSpawn();
        if (firstSpawn == null) {
            return;
        }

        // Schedule teleportation on next tick to ensure player is fully loaded
        player.getScheduler().run(plugin, (task) -> {
            Location safeLocation = plugin.getTeleportManager().findSafeLocation(firstSpawn);
            if (safeLocation == null) {
                safeLocation = firstSpawn;
            }
            
            player.teleportAsync(safeLocation);
            plugin.getMessenger().sendSuccess(player, "Welcome! You have been teleported to spawn.");
        }, null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Teleport to spawn on death respawn
        Location spawnLocation = plugin.getTeleportManager().getSpawn();
        if (spawnLocation != null) {
            Location safeLocation = plugin.getTeleportManager().findSafeLocation(spawnLocation);
            if (safeLocation != null) {
                event.setRespawnLocation(safeLocation);
                plugin.getTeleportManager().setLastLocation(player.getUniqueId(), player.getLocation());
            }
        }
    }
}
