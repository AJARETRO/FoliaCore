package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Handles maintenance mode and spawn-related logic.
 */
public class MaintenanceListener implements Listener {

    private final FoliaCore plugin;

    public MaintenanceListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Maintenance mode check
        if (plugin.getConfigManager().maintenanceMode && !player.hasPermission("foliacore.admin")) {
            String message = plugin.getConfigManager().getString("system.maintenance-message", 
                "&c&lServer Maintenance\n&fPlease try again later.");
            player.kick(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
            return;
        }

        // Auto-teleport new players to first spawn
        if (plugin.getConfigManager().getBoolean("system.auto-spawn-new-players", true) && !player.hasPlayedBefore()) {
            player.getScheduler().runDelayed(plugin, (task) -> {
                if (player.isOnline()) {
                    plugin.getTeleportManager().startTeleport(
                            player,
                            plugin.getSpawnManager().getFirstSpawn(),
                            org.bukkit.ChatColor.GREEN + "Welcome to the server!"
                    );
                }
            }, null, 10L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Vanish manager tracks join internally
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Teleport to spawn on respawn
        event.setRespawnLocation(plugin.getSpawnManager().getSpawn());
    }
}
