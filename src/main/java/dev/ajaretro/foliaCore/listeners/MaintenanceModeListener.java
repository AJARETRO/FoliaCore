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
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Handles maintenance mode kick logic.
 */
public class MaintenanceModeListener implements Listener {

    private final FoliaCore plugin;

    public MaintenanceModeListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigManager().isMaintenanceMode()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("foliacore.admin")) {
            String kickMessage = plugin.getConfigManager().getMaintenanceKickMessage();
            player.kick(LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage));
        }
    }
}
