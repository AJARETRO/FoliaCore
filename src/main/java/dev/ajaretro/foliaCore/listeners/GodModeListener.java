package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for damage events to prevent damage to players in god mode.
 */
public class GodModeListener implements Listener {

    private final FoliaCore plugin;

    public GodModeListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        if (plugin.isInGodMode(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup handled elsewhere if needed
    }
}
