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
    private final ConcurrentHashMap<UUID, TeleportRequest> pendingTpaRequests;
    private Location spawnLocation;

    private File dataFile;
    private FileConfiguration dataConfig;

    private static final long TPA_REQUEST_TIMEOUT_MS = 60 * 1000;
    private static final long TELEPORT_DELAY_TICKS = 60L;

    public enum TpaType { TPA, TPAHERE }

    public record TeleportRequest(UUID requester, UUID target, TpaType type, long timestamp) {}

    public TeleportManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.playerHomes = new ConcurrentHashMap<>();
        this.pendingTeleports = new ConcurrentHashMap<>();
        this.pendingTpaRequests = new ConcurrentHashMap<>();
        this.spawnLocation = null;
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
        loadSpawn();
    }

    private void loadHomes() {
        // ... (this method is unchanged)
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

    private void loadSpawn() {
        ConfigurationSection spawnSection = dataConfig.getConfigurationSection("spawn");
        if (spawnSection != null) {
            try {
                Map<String, Object> spawnData = spawnSection.getValues(false);
                this.spawnLocation = Location.deserialize(spawnData);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load spawn location from teleport_data.yml!");
            }
        }
    }

    public void saveData() {
        // ... (saveHomes part is unchanged)
        try {
            dataConfig.set("homes", null);
            for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
                String uuidString = entry.getKey().toString();
                for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                    dataConfig.set("homes." + uuidString + "." + homeEntry.getKey(), homeEntry.getValue().serialize());
                }
            }

            if (this.spawnLocation != null) {
                dataConfig.set("spawn", this.spawnLocation.serialize());
            } else {
                dataConfig.set("spawn", null);
            }

            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save teleport data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        // ... (this method is unchanged)
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveData();
        });
    }

    // --- Spawn Methods ---

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        saveDataAsync();
    }

    public Location getSpawn() {
        if (this.spawnLocation == null) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        return this.spawnLocation;
    }

    // --- Home Methods ---

    public void setHome(UUID playerUUID, String homeName, Location location) {
        // ... (this method is unchanged)
        playerHomes.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>()).put(homeName.toLowerCase(), new Home(location));
        saveDataAsync();
    }

    public Home getHome(UUID playerUUID, String homeName) {
        // ... (this method is unchanged)
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return null;
        return homes.get(homeName.toLowerCase());
    }

    public void deleteHome(UUID playerUUID, String homeName) {
        // ... (this method is unchanged)
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes != null) {
            homes.remove(homeName.toLowerCase());
            saveDataAsync();
        }
    }

    public int getHomeCount(UUID playerUUID) {
        // ... (this method is unchanged)
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return (homes == null) ? 0 : homes.size();
    }

    public Map<String, Home> getHomes(UUID playerUUID) {
        // ... (this method is unchanged)
        return playerHomes.getOrDefault(playerUUID, Collections.emptyMap());
    }

    public int getMaxHomes(Player player) {
        // ... (this method is unchanged)
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

    // --- Teleport Delay Methods ---

    public void startTeleport(Player player, Location location, String successMessage) {
        // ... (this method is unchanged)
        if (isTeleporting(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You are already teleporting!");
            return;
        }

        plugin.getMessenger().sendMessage(player, "Teleporting... Don't move for 3 seconds...");

        var task = player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
            completeTeleport(player.getUniqueId());
            player.teleportAsync(location);
            plugin.getMessenger().sendSuccess(player, successMessage);
        }, null, TELEPORT_DELAY_TICKS);

        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(Player player) {
        // ... (this method is unchanged)
        ScheduledTask existingTask = pendingTeleports.remove(player.getUniqueId());
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
            plugin.getMessenger().sendError(player, "Teleport cancelled.");
        }
    }

    public void completeTeleport(UUID uuid) {
        // ... (this method is unchanged)
        pendingTeleports.remove(uuid);
    }

    public boolean isTeleporting(UUID uuid) {
        // ... (this method is unchanged)
        return pendingTeleports.containsKey(uuid);
    }

    // --- TPA Request Methods ---

    public void createTpaRequest(UUID requester, UUID target, TpaType type) {
        // ... (this method is unchanged)
        TeleportRequest request = new TeleportRequest(requester, target, type, System.currentTimeMillis());
        pendingTpaRequests.put(target, request);
    }

    public TeleportRequest getTpaRequest(UUID target) {
        // ... (this method is unchanged)
        TeleportRequest request = pendingTpaRequests.get(target);
        if (request == null) {
            return null;
        }

        if (System.currentTimeMillis() - request.timestamp() > TPA_REQUEST_TIMEOUT_MS) {
            pendingTpaRequests.remove(target);
            return null;
        }

        return request;
    }

    public void removeTpaRequest(UUID target) {
        // ... (this method is unchanged)
        pendingTpaRequests.remove(target);
    }
}