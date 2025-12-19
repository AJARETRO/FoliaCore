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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final FoliaCore plugin;

    // Thread-safe storage for Cross-Region Access
    private final Map<UUID, Map<String, Home>> playerHomes = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledTask> pendingTeleports = new ConcurrentHashMap<>();
    private final Map<UUID, TeleportRequest> pendingTpaRequests = new ConcurrentHashMap<>();

    private volatile Location spawnLocation; // Volatile for visibility across threads
    private final File dataFile;
    private FileConfiguration dataConfig;

    private static final long TPA_REQUEST_TIMEOUT_MS = 60 * 1000;
    private static final long TELEPORT_DELAY_TICKS = 60L; // 3 seconds at 20 TPS

    public enum TpaType { TPA, TPAHERE }
    public record TeleportRequest(UUID requester, UUID target, TpaType type, long timestamp) {}

    public TeleportManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "teleport_data.yml");
    }

    public void load() {
        if (!dataFile.exists()) {
            plugin.saveResource("teleport_data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadHomes();
        loadSpawn();
    }

    // --- Logic ---

    public void startTeleport(@NotNull Player player, @NotNull Location location, String successMessage) {
        if (isTeleporting(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Teleportation already in progress.");
            return;
        }

        plugin.getMessenger().sendMessage(player, "Teleporting... Stand still for 3 seconds.");

        // Folia Requirement: Use the entity's scheduler to ensure thread locality.
        ScheduledTask task = player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
            pendingTeleports.remove(player.getUniqueId());

            // Async teleport is safer on Folia as it handles chunk loading internally without blocking
            player.teleportAsync(location).thenAccept(result -> {
                if (result) {
                    plugin.getMessenger().sendSuccess(player, successMessage);
                } else {
                    plugin.getMessenger().sendError(player, "Teleport failed. Destination may be obstructed.");
                }
            });

        }, null, TELEPORT_DELAY_TICKS);

        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(@NotNull Player player) {
        ScheduledTask task = pendingTeleports.remove(player.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
            plugin.getMessenger().sendError(player, "Teleport cancelled due to movement.");
        }
    }

    public boolean isTeleporting(UUID uuid) {
        return pendingTeleports.containsKey(uuid);
    }

    public void cleanupPlayer(UUID uuid) {
        pendingTeleports.remove(uuid);
        pendingTpaRequests.remove(uuid);
    }

    // --- TPA Request Logic ---

    public void createTpaRequest(UUID requester, UUID target, TpaType type) {
        pendingTpaRequests.put(target, new TeleportRequest(requester, target, type, System.currentTimeMillis()));
    }

    @Nullable
    public TeleportRequest getTpaRequest(UUID target) {
        TeleportRequest request = pendingTpaRequests.get(target);
        if (request == null) return null;

        if (System.currentTimeMillis() - request.timestamp() > TPA_REQUEST_TIMEOUT_MS) {
            pendingTpaRequests.remove(target);
            return null;
        }
        return request;
    }

    public void removeTpaRequest(UUID target) {
        pendingTpaRequests.remove(target);
    }

    // --- Persistence (Snapshot Pattern) ---

    /**
     * Saves data using the Snapshot Pattern.
     * Creates a copy of the data in memory, then writes to disk asynchronously.
     * This prevents ConcurrentModificationException if the main thread updates data during save.
     */
    public void saveData() {
        final var homesSnapshot = new java.util.HashMap<>(this.playerHomes);
        final var spawnSnapshot = this.spawnLocation;

        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            try {
                YamlConfiguration tempConfig = new YamlConfiguration();

                // Serialize Homes
                for (var entry : homesSnapshot.entrySet()) {
                    String uuid = entry.getKey().toString();
                    for (var homeEntry : entry.getValue().entrySet()) {
                        tempConfig.set("homes." + uuid + "." + homeEntry.getKey(), homeEntry.getValue().serialize());
                    }
                }

                // Serialize Spawn
                if (spawnSnapshot != null) {
                    tempConfig.set("spawn", spawnSnapshot.serialize());
                }

                tempConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save teleport data.");
                e.printStackTrace();
            }
        });
    }

    // --- Loading Logic ---

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
                    homes.put(homeName.toLowerCase(), Home.deserialize(homeSection.getValues(false)));
                }
                playerHomes.put(playerUUID, homes);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping invalid UUID in homes.yml: " + uuidString);
            }
        }
    }

    private void loadSpawn() {
        ConfigurationSection spawnSection = dataConfig.getConfigurationSection("spawn");
        if (spawnSection != null) {
            try {
                this.spawnLocation = Location.deserialize(spawnSection.getValues(false));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to deserialize spawn location.");
            }
        }
    }

    // --- Home Accessors ---

    public void setHome(UUID playerUUID, String homeName, Location location) {
        playerHomes.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
                .put(homeName.toLowerCase(), new Home(location));
        saveData(); // Triggers async save
    }

    @Nullable
    public Home getHome(UUID playerUUID, String homeName) {
        var homes = playerHomes.get(playerUUID);
        return homes == null ? null : homes.get(homeName.toLowerCase());
    }

    public void deleteHome(UUID playerUUID, String homeName) {
        var homes = playerHomes.get(playerUUID);
        if (homes != null) {
            homes.remove(homeName.toLowerCase());
            saveData();
        }
    }

    public int getHomeCount(UUID playerUUID) {
        var homes = playerHomes.get(playerUUID);
        return homes == null ? 0 : homes.size();
    }

    public Map<String, Home> getHomes(UUID playerUUID) {
        return playerHomes.getOrDefault(playerUUID, Collections.emptyMap());
    }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        saveData();
    }

    public Location getSpawn() {
        return this.spawnLocation != null ? this.spawnLocation : Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public int getMaxHomes(Player player) {
        if (player.hasPermission("foliacore.homes.unlimited")) return Integer.MAX_VALUE;

        int max = 0;
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith("foliacore.homes.")) {
                try {
                    int num = Integer.parseInt(perm.substring(16));
                    max = Math.max(max, num);
                } catch (NumberFormatException ignored) {}
            }
        }
        return (max == 0 && player.hasPermission("foliacore.homes.default")) ? 1 : max;
    }
}