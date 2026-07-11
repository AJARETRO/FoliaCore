/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

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

/**
 * Manages player homes, spawns, and teleportation requests.
 *
 * This manager handles:
 * - Player home creation and teleportation
 * - Server spawn management
 * - TPA/TPA Here requests with timeout validation
 * - Teleport delay with movement cancellation
 *
 * All operations are fully compatible with Folia's multi-threaded architecture.
 */
public class TeleportManager {

    private final FoliaCore plugin;
    private final Map<UUID, Map<String, Home>> playerHomes = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledTask> pendingTeleports = new ConcurrentHashMap<>();
    private final Map<UUID, TeleportRequest> pendingTpaRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Location> playerLastLocations = new ConcurrentHashMap<>();
    private volatile Location spawnLocation;
    private volatile Location firstSpawnLocation;
    private final File dataFile;
    private FileConfiguration dataConfig;

    private static final long TPA_REQUEST_TIMEOUT_MS = 60 * 1000;
    private static final long TELEPORT_DELAY_TICKS = 60L;

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
        loadFirstSpawn();
    }

    public void startTeleport(@NotNull Player player, @NotNull Location location, String successMessage) {
        if (isTeleporting(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Teleportation already in progress.");
            return;
        }

        // Check if location is safe before teleporting
        Location safeLocation = findSafeLocation(location);
        if (safeLocation == null) {
            plugin.getMessenger().sendError(player, "Destination is not safe for teleportation. No solid ground found.");
            return;
        }

        plugin.getMessenger().sendMessage(player, "Teleporting... Stand still for 3 seconds.");

        ScheduledTask task = player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
            pendingTeleports.remove(player.getUniqueId());

            player.teleportAsync(safeLocation).thenAccept(result -> {
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

    public void saveData() {
        final var homesSnapshot = new java.util.HashMap<>(this.playerHomes);
        final var spawnSnapshot = this.spawnLocation;
        final var firstSpawnSnapshot = this.firstSpawnLocation;
        if (!plugin.isEnabled()) {
            saveDataSync(homesSnapshot, spawnSnapshot, firstSpawnSnapshot);
            return;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveDataSync(homesSnapshot, spawnSnapshot, firstSpawnSnapshot);
        });
    }

    private void saveDataSync(Map<UUID, Map<String, Home>> homesSnapshot, Location spawnSnapshot, Location firstSpawnSnapshot) {
        try {
            YamlConfiguration tempConfig = new YamlConfiguration();

            for (var entry : homesSnapshot.entrySet()) {
                String uuid = entry.getKey().toString();
                for (var homeEntry : entry.getValue().entrySet()) {
                    tempConfig.set("homes." + uuid + "." + homeEntry.getKey(), homeEntry.getValue().serialize());
                }
            }

            if (spawnSnapshot != null) {
                tempConfig.set("spawn", spawnSnapshot.serialize());
            }
            if (firstSpawnSnapshot != null) {
                tempConfig.set("first_spawn", firstSpawnSnapshot.serialize());
            }

            tempConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teleport data.");
            e.printStackTrace();
        }
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

    private void loadFirstSpawn() {
        ConfigurationSection firstSpawnSection = dataConfig.getConfigurationSection("first_spawn");
        if (firstSpawnSection != null) {
            try {
                this.firstSpawnLocation = Location.deserialize(firstSpawnSection.getValues(false));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to deserialize first spawn location.");
            }
        }
    }

    public void setHome(UUID playerUUID, String homeName, Location location) {
        playerHomes.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>())
                .put(homeName.toLowerCase(), new Home(location));
        saveData();
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

    public void setFirstSpawn(Location location) {
        this.firstSpawnLocation = location;
        saveData();
    }

    @Nullable
    public Location getFirstSpawn() {
        return this.firstSpawnLocation;
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

    public void setLastLocation(UUID playerUUID, Location location) {
        if (location != null) {
            playerLastLocations.put(playerUUID, location);
        }
    }

    @Nullable
    public Location getLastLocation(UUID playerUUID) {
        return playerLastLocations.get(playerUUID);
    }

    public void clearLastLocation(UUID playerUUID) {
        playerLastLocations.remove(playerUUID);
    }

    /**
     * Checks if a location is safe for teleportation.
     * Verifies that target block and block above are AIR, and block below is SOLID.
     */
    public boolean isSafeLocation(@NotNull Location location) {
        org.bukkit.block.Block targetBlock = location.getBlock();
        org.bukkit.block.Block blockAbove = targetBlock.getRelative(org.bukkit.block.BlockFace.UP);
        org.bukkit.block.Block blockBelow = targetBlock.getRelative(org.bukkit.block.BlockFace.DOWN);

        // Check if target and above are air
        if (!targetBlock.isPassable() || !blockAbove.isPassable()) {
            return false;
        }

        // Check if below is solid
        return blockBelow.getType().isSolid();
    }

    /**
     * Finds a safe location near the target location if it's unsafe.
     * Searches upward and nearby chunks.
     */
    @Nullable
    public Location findSafeLocation(@NotNull Location location) {
        if (isSafeLocation(location)) {
            return location;
        }

        // Try nearby locations (up to 5 blocks away)
        for (int y = 0; y <= 5; y++) {
            Location testLoc = location.clone().add(0, y, 0);
            if (isSafeLocation(testLoc)) {
                return testLoc;
            }
        }

        // Try side locations
        int[] offsets = {-1, 1};
        for (int dx : offsets) {
            for (int dz : offsets) {
                Location testLoc = location.clone().add(dx, 1, dz);
                if (isSafeLocation(testLoc)) {
                    return testLoc;
                }
            }
        }

        return null;
    }

    private final ConcurrentHashMap<UUID, Boolean> autoAccept = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> tpToggle = new ConcurrentHashMap<>();
    private int minTprRange = 100;
    private int maxTprRange = 5000;

    public boolean toggleAutoAccept(UUID uuid) {
        return autoAccept.compute(uuid, (k, v) -> v == null || !v);
    }

    public boolean isAutoAcceptEnabled(UUID uuid) {
        return autoAccept.getOrDefault(uuid, false);
    }

    public boolean toggleTpToggle(UUID uuid) {
        return tpToggle.compute(uuid, (k, v) -> v == null || !v);
    }

    public boolean isTpToggleDisabled(UUID uuid) {
        return tpToggle.getOrDefault(uuid, false);
    }

    public int getMinTprRange() {
        return minTprRange;
    }

    public void setMinTprRange(int val) {
        this.minTprRange = val;
    }

    public int getMaxTprRange() {
        return maxTprRange;
    }

    public void setMaxTprRange(int val) {
        this.maxTprRange = val;
    }

    public void renameHome(UUID uuid, String oldName, String newName) {
        var homes = playerHomes.get(uuid);
        if (homes != null) {
            Home h = homes.remove(oldName.toLowerCase());
            if (h != null) {
                homes.put(newName.toLowerCase(), h);
                saveData();
            }
        }
    }
}