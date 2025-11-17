package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Home;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class TeleportManager {

    private static TeleportManager instance;
    private final FoliaCore plugin;

    private final ConcurrentHashMap<UUID, Map<String, Home>> playerHomes;
    private final ConcurrentHashMap<UUID, ScheduledTask> pendingTeleports;

    private File dataFile;
    private FileConfiguration dataConfig;

    public TeleportManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.playerHomes = new ConcurrentHashMap<>();
        this.pendingTeleports = new ConcurrentHashMap<>();
    }

    public static TeleportManager getInstance() {
        return instance;
    }

    public void load() {
        instance = this;

        dataFile = new File(plugin.getDataFolder(), "teleport_data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("teleport_data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadHomes();
    }

    private void loadHomes() {
        ConfigurationSection homesSection = dataConfig.getConfigurationSection("homes");
        if (homesSection == null) return;

        for (String uuidString : homesSection.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                ConfigurationSection playerHomeSection = homesSection.getConfigurationSection(uuidString);
                if (playerHomeSection == null) continue;

                Map<String, Home> homes = new ConcurrentHashMap<>();
                for (String homeName : playerHomeSection.getKeys(false)) {
                    ConfigurationSection homeSection = playerHomeSection.getConfigurationSection(homeName);
                    if (homeSection == null) continue;

                    Map<String, Object> homeData = homeSection.getValues(false);
                    homes.put(homeName.toLowerCase(), Home.deserialize(homeData));
                }
                playerHomes.put(playerUUID, homes);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load homes for invalid UUID: " + uuidString);
            }
        }
    }

    public void saveData() {
        try {
            dataConfig.set("homes", null);
            for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
                String uuidString = entry.getKey().toString();
                for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                    dataConfig.set("homes." + uuidString + "." + homeEntry.getKey(), homeEntry.getValue().serialize());
                }
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save teleport data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveData();
        });
    }

    public void setHome(UUID playerUUID, String homeName, Location location) {
        playerHomes.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>()).put(homeName.toLowerCase(), new Home(location));
        saveDataAsync();
    }

    public Home getHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return null;
        return homes.get(homeName.toLowerCase());
    }

    public void deleteHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes != null) {
            homes.remove(homeName.toLowerCase());
            saveDataAsync();
        }
    }

    public int getHomeCount(UUID playerUUID) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return (homes == null) ? 0 : homes.size();
    }

    public Map<String, Home> getHomes(UUID playerUUID) {
        return playerHomes.getOrDefault(playerUUID, Collections.emptyMap());
    }

    public int getMaxHomes(Player player) {
        if (player.hasPermission("foliacore.homes.unlimited")) {
            return Integer.MAX_VALUE;
        }

        int max = 0;
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith("foliacore.homes.")) {
                try {
                    String numString = perm.substring(16);
                    int num = Integer.parseInt(numString);
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }

        if (max == 0 && player.hasPermission("foliacore.homes.default")) {
            return 1;
        }

        return max;
    }

    public void startTeleport(Player player, ScheduledTask task) {
        cancelTeleport(player);
        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(Player player) {
        ScheduledTask existingTask = pendingTeleports.remove(player.getUniqueId());
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
            plugin.getMessenger().sendError(player, "Teleport cancelled.");
        }
    }

    public void completeTeleport(UUID uuid) {
        pendingTeleports.remove(uuid);
    }

    public boolean isTeleporting(UUID uuid) {
        return pendingTeleports.containsKey(uuid);
    }
}