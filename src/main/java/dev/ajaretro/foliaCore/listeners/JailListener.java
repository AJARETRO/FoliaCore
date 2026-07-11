/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.JailedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Restricts player actions while they are jailed.
 */
public class JailListener implements Listener {
    private final FoliaCore plugin;

    public JailListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getJailManager().isJailed(player.getUniqueId())) {
            JailedPlayer jp = plugin.getJailManager().getJailedPlayer(player.getUniqueId());
            Location jailLoc = plugin.getJailManager().getJail(jp.getJailName());
            if (jailLoc != null) {
                // If they move too far from the jail location, teleport them back
                if (!player.getWorld().equals(jailLoc.getWorld()) || player.getLocation().distanceSquared(jailLoc) > 16.0) {
                    event.setCancelled(true);
                    player.teleportAsync(jailLoc);
                    player.sendMessage("§cYou are jailed! You cannot leave this area.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getJailManager().isJailed(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot break blocks while jailed.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getJailManager().isJailed(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot place blocks while jailed.");
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.getJailManager().isJailed(player.getUniqueId())) {
            // Block all commands except those starting with /unjail (for admins if they execute it, though typically admins aren't jailed)
            String cmd = event.getMessage().toLowerCase();
            if (!cmd.startsWith("/unjail") && !cmd.startsWith("/rules")) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot execute commands while jailed.");
            }
        }
    }
}
