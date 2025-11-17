package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.MarkerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerGpsListener implements Listener {

    private final MarkerManager markerManager;

    public PlayerGpsListener(FoliaCore plugin) {
        this.markerManager = plugin.getMarkerManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        markerManager.stopGps(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        markerManager.stopGps(event.getPlayer());
    }
}