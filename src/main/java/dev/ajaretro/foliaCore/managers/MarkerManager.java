package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Marker;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarkerManager {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Map<String, Marker>> playerMarkers;
    private final ConcurrentHashMap<UUID, ScheduledTask> activeGpsTasks;

    private File dataFile;
    private FileConfiguration dataConfig;

    public MarkerManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.playerMarkers = new ConcurrentHashMap<>();
        this.activeGpsTasks = new ConcurrentHashMap<>();
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "markers.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("markers.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadMarkers();
    }

    private void loadMarkers() {
        ConfigurationSection markersSection = dataConfig.getConfigurationSection("markers");
        if (markersSection == null) return;

        for (String uuidString : markersSection.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                ConfigurationSection playerMarkerSection = markersSection.getConfigurationSection(uuidString);
                if (playerMarkerSection == null) continue;

                Map<String, Marker> markers = new ConcurrentHashMap<>();
                for (String markerName : playerMarkerSection.getKeys(false)) {
                    ConfigurationSection markerSection = playerMarkerSection.getConfigurationSection(markerName);
                    if (markerSection == null) continue;

                    Map<String, Object> markerData = markerSection.getValues(false);
                    markers.put(markerName.toLowerCase(), Marker.deserialize(markerName, markerData));
                }
                playerMarkers.put(playerUUID, markers);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load markers for invalid UUID: " + uuidString);
            }
        }
    }

    public void saveData() {
        if (!plugin.isEnabled()) {
            saveDataSync();
            return;
        }
        saveDataAsync();
    }

    private void saveDataSync() {
        try {
            dataConfig.set("markers", null);
            for (Map.Entry<UUID, Map<String, Marker>> entry : playerMarkers.entrySet()) {
                String uuidString = entry.getKey().toString();
                for (Map.Entry<String, Marker> markerEntry : entry.getValue().entrySet()) {
                    dataConfig.set("markers." + uuidString + "." + markerEntry.getKey(), markerEntry.getValue().serialize());
                }
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save marker data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveDataSync());
    }

    public void setMarker(UUID playerUUID, String name, Location location) {
        Marker marker = new Marker(name, location);
        playerMarkers.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>()).put(name.toLowerCase(), marker);
        saveData();
    }

    public void deleteMarker(UUID playerUUID, String name) {
        Map<String, Marker> markers = playerMarkers.get(playerUUID);
        if (markers != null) {
            markers.remove(name.toLowerCase());
            saveData();
        }
    }

    public Marker getMarker(UUID playerUUID, String name) {
        Map<String, Marker> markers = playerMarkers.get(playerUUID);
        if (markers == null) return null;
        return markers.get(name.toLowerCase());
    }

    public Map<String, Marker> getMarkers(UUID playerUUID) {
        return playerMarkers.getOrDefault(playerUUID, Collections.emptyMap());
    }

    public boolean isGpsActive(UUID playerUUID) {
        return activeGpsTasks.containsKey(playerUUID);
    }

    public void stopGps(Player player) {
        ScheduledTask task = activeGpsTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            player.sendActionBar(Component.text("GPS navigation stopped.", NamedTextColor.RED));
        }
    }

    public void startGps(Player player, Marker marker) {
        stopGps(player);

        Location targetLocation = marker.toLocation();
        if (targetLocation == null) {
            plugin.getMessenger().sendError(player, "The world for that marker is not loaded!");
            return;
        }

        plugin.getMessenger().sendSuccess(player, "Starting GPS to " + marker.name() + ". Look at your action bar. Type /gps off to stop.");

        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, (scheduledTask) -> {

            if (!player.isOnline()) {
                scheduledTask.cancel();
                activeGpsTasks.remove(player.getUniqueId());
                return;
            }

            Location playerLoc = player.getLocation();

            if (!playerLoc.getWorld().equals(targetLocation.getWorld())) {
                player.sendActionBar(Component.text("Wrong World! Target is in " + marker.worldName(), NamedTextColor.RED));
                return;
            }

            double distance = playerLoc.distance(targetLocation);
            if (distance < 5.0) {
                player.sendActionBar(Component.text("You have arrived at " + marker.name() + "!", NamedTextColor.GREEN));
                scheduledTask.cancel();
                activeGpsTasks.remove(player.getUniqueId());
                return;
            }

            Vector direction = targetLocation.toVector().subtract(playerLoc.toVector()).normalize();
            String arrow = getArrow(playerLoc.getYaw(), direction);

            String distanceString = String.format("%,.0f", distance) + "m";
            Component message = Component.text()
                    .append(Component.text(arrow, NamedTextColor.GOLD))
                    .append(Component.text(" " + marker.name() + " | ", NamedTextColor.WHITE))
                    .append(Component.text(distanceString, NamedTextColor.GOLD))
                    .build();

            player.sendActionBar(message);

        }, null, 1L, 20L);

        activeGpsTasks.put(player.getUniqueId(), task);
    }

    private String getArrow(float playerYaw, Vector direction) {
        playerYaw = (playerYaw % 360 + 360) % 360;
        double vectorAngle = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        double angle = (vectorAngle - playerYaw) % 360;

        if (angle < 0) angle += 360;

        if (angle <= 22.5 || angle > 337.5) return "⬆";
        if (angle > 22.5 && angle <= 67.5) return "↗";
        if (angle > 67.5 && angle <= 112.5) return "➡";
        if (angle > 112.5 && angle <= 157.5) return "↘";
        if (angle > 157.5 && angle <= 202.5) return "⬇";
        if (angle > 202.5 && angle <= 247.5) return "↙";
        if (angle > 247.5 && angle <= 292.5) return "⬅";
        if (angle > 292.5 && angle <= 337.5) return "↖";
        return "?";
    }
}