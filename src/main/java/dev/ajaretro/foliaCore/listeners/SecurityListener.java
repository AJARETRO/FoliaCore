/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import dev.ajaretro.foliaCore.data.Ban;
import dev.ajaretro.foliaCore.utils.TimeUtil;

/**
 * Handles security features:
 * - Ban checks on prelogin
 * - Staff IP-locking
 * - Maintenance mode enforcement
 * - Spawn assignment on join/respawn
 * - Vanish hiding on join
 */
public class SecurityListener implements Listener {

    private final FoliaCore plugin;

    public SecurityListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        // 1. Check bans first
        if (plugin.getBanManager() != null && plugin.getBanManager().isBanned(event.getUniqueId())) {
            Ban ban = plugin.getBanManager().getBan(event.getUniqueId());
            if (ban != null) {
                String kickMsg = "&c&lYou are banned!\n&7Reason: &f" + ban.getReason();
                if (!ban.isPermanent()) {
                    long remaining = ban.getExpiryTime() - System.currentTimeMillis();
                    kickMsg += "\n&7Expires in: &f" + TimeUtil.formatDuration(remaining);
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    LegacyComponentSerializer.legacyAmpersand().deserialize(kickMsg));
                return;
            }
        }

        if (!plugin.getConfigManager().securityEnabled) return;

        // 2. Check maintenance mode
        if (plugin.getConfigManager().getBoolean("system.maintenance-mode", false)) {
            String playerName = event.getName();
            // Allow OPs/admins to join during maintenance (matching Security/Maintenance config logic)
            if (!isStaffMember(playerName) && !playerName.equalsIgnoreCase("admin")) {
                String kickMsg = plugin.getConfigManager().getString("system.maintenance-kick-message", 
                    "&c&lServer Maintenance\n&fPlease try again later.");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                    LegacyComponentSerializer.legacyAmpersand().deserialize(kickMsg));
                return;
            }
        }

        // 3. Staff IP-lock check
        if (plugin.getConfigManager().getBoolean("security.staff-ip-lock", true)) {
            String playerName = event.getName();
            if (isStaffMember(playerName)) {
                String ipAddress = event.getAddress().getHostAddress();
                if (!isIPTrusted(playerName, ipAddress)) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        LegacyComponentSerializer.legacyAmpersand().deserialize("&c&lUnauthorized Location\n&fYour IP is not registered. Contact admin."));
                    plugin.getLogger().warning("[SECURITY] Staff member " + playerName + " attempted login from untrusted IP: " + ipAddress);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Hide already-vanished players from the joining player
        if (plugin.getVanishManager() != null) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (plugin.getVanishManager().isVanished(online.getUniqueId())) {
                    if (!player.hasPermission("foliacore.vanish.see")) {
                        player.hidePlayer(plugin, online);
                    }
                }
            }
        }

        // Teleport new players to first spawn if configured
        if (plugin.getConfigManager().teleportEnabled && plugin.getSpawnManager() != null) {
            if (!player.hasPlayedBefore()) {
                var firstSpawn = plugin.getSpawnManager().getFirstSpawn();
                if (firstSpawn != null) {
                    player.getScheduler().runDelayed(plugin, (task) -> {
                        if (player.isOnline()) {
                            player.teleportAsync(firstSpawn);
                        }
                    }, null, 5L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Teleport to spawn on respawn if configured
        if (plugin.getConfigManager().teleportEnabled && plugin.getSpawnManager() != null) {
            if (plugin.getSpawnManager().teleportOnRespawn()) {
                var spawn = plugin.getSpawnManager().getSpawn();
                if (spawn != null) {
                    event.setRespawnLocation(spawn);
                }
            }
        }
    }

    private boolean isIPTrusted(String playerName, String ipAddress) {
        var securityConfig = plugin.getConfigManager().securityConfig;
        if (securityConfig == null) return false;

        var trustedIPs = securityConfig.getStringList("trusted_ips." + playerName);
        return trustedIPs.contains(ipAddress);
    }

    private boolean isStaffMember(String playerName) {
        var config = plugin.getConfigManager().getConfig();
        if (config == null) return false;
        
        var staffList = config.getStringList("security.staff-members");
        return staffList.contains(playerName.toLowerCase());
    }
}
