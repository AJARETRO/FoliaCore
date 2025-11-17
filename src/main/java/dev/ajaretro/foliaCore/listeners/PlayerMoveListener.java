package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMoveListener implements Listener {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public PlayerMoveListener(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) {
            return;
        }

        Player player = event.getPlayer();
        if (tm.isTeleporting(player.getUniqueId())) {
            tm.cancelTeleport(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        tm.cancelTeleport(player);
        tm.removeTpaRequest(player.getUniqueId());
    }
}