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

/**
 * Handles security features:
 * - Staff IP-locking
 * - Maintenance mode enforcement
 * - Spawn assignment on join/respawn
 */
public class SecurityListener implements Listener {

    private final FoliaCore plugin;

    public SecurityListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getConfigManager().securityEnabled) return;

        // Check maintenance mode
        if (plugin.getConfigManager().getBoolean("system.maintenance", false)) {
            // Note: AsyncPlayerPreLoginEvent doesn't have getPlayer(), use getName() for logging
            String playerName = event.getName();
            if (!event.getName().equals("admin")) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                    LegacyComponentSerializer.legacyAmpersand().deserialize("&c&lServer Maintenance\n&fPlease try again later."));
                return;
            }
        }

        // Staff IP-lock check
        if (plugin.getConfigManager().getBoolean("security.staff-ip-lock", true)) {
            String playerName = event.getName();
            // Note: Can't check permissions in AsyncPlayerPreLoginEvent - check stored staff list instead
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

        // Teleport new players to first spawn if configured
        if (plugin.getConfigManager().teleportEnabled && plugin.getSpawnManager() != null) {
            if (!player.hasPlayedBefore()) {
                var firstSpawn = plugin.getSpawnManager().getFirstSpawn();
                if (firstSpawn != null) {
                    player.getScheduler().runDelayed(plugin, (task) -> {
                        player.teleportAsync(firstSpawn);
                    }, null, 1L);
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
        // Load trusted IPs from security.yml
        var securityConfig = plugin.getConfigManager().securityConfig;
        if (securityConfig == null) return false;

        var trustedIPs = securityConfig.getStringList("trusted_ips." + playerName);
        return trustedIPs.contains(ipAddress);
    }

    private boolean isStaffMember(String playerName) {
        // Check if player is in staff list from config
        var config = plugin.getConfigManager().getConfig();
        if (config == null) return false;
        
        var staffList = config.getStringList("security.staff-members");
        return staffList.contains(playerName.toLowerCase());
    }
}
