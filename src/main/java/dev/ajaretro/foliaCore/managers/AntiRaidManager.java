package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages anti-raid detection and lockdown enforcement.
 * Uses per-player, per-second block change tracking to detect mass block modifications.
 */
public class AntiRaidManager {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, BlockTracker> playerTrackers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, LockdownState> lockdownStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> pauseExpiryTimes = new ConcurrentHashMap<>();
    
    private File dataFile;
    private FileConfiguration dataConfig;

    // Configuration
    private int thresholdPerSecond;
    private boolean autoLockdown;
    private boolean notifyStaff;

    private static final long CHECK_INTERVAL_MS = 1000L; // Reset counter every second

    public static class BlockTracker {
        public AtomicInteger blockCount;
        public long lastReset;

        public BlockTracker() {
            this.blockCount = new AtomicInteger(0);
            this.lastReset = System.currentTimeMillis();
        }

        public void increment() {
            long now = System.currentTimeMillis();
            if (now - lastReset >= CHECK_INTERVAL_MS) {
                blockCount.set(0);
                lastReset = now;
            }
            blockCount.incrementAndGet();
        }

        public int getCount() {
            long now = System.currentTimeMillis();
            if (now - lastReset >= CHECK_INTERVAL_MS) {
                blockCount.set(0);
                lastReset = now;
                return 0;
            }
            return blockCount.get();
        }
    }

    public static class LockdownState {
        public UUID playerUUID;
        public String playerName;
        public long lockedTime;
        public boolean requiresConsoleReset;

        public LockdownState(UUID playerUUID, String playerName) {
            this.playerUUID = playerUUID;
            this.playerName = playerName;
            this.lockedTime = System.currentTimeMillis();
            this.requiresConsoleReset = true;
        }
    }

    public AntiRaidManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.thresholdPerSecond = plugin.getConfigManager().getInt("antiraid.threshold-per-second", 50);
        this.autoLockdown = plugin.getConfigManager().getBoolean("antiraid.auto-lockdown", true);
        this.notifyStaff = plugin.getConfigManager().getBoolean("antiraid.notify-staff", true);
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "security_data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create security_data.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadLockdowns();
    }

    private void loadLockdowns() {
        ConfigurationSection lockdownSection = dataConfig.getConfigurationSection("lockdowns");
        if (lockdownSection == null) return;

        for (String uuidStr : lockdownSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection section = lockdownSection.getConfigurationSection(uuidStr);
                if (section != null) {
                    String playerName = section.getString("player-name", "Unknown");
                    LockdownState state = new LockdownState(uuid, playerName);
                    state.lockedTime = section.getLong("locked-time", System.currentTimeMillis());
                    lockdownStates.put(uuid, state);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in lockdowns: " + uuidStr);
            }
        }
    }

    public void trackBlockChange(UUID playerUUID) {
        // Check if player is paused (allowed to build freely)
        Long pauseExpiry = pauseExpiryTimes.get(playerUUID);
        if (pauseExpiry != null && System.currentTimeMillis() < pauseExpiry) {
            return; // Player is in pause mode, don't track
        }

        BlockTracker tracker = playerTrackers.computeIfAbsent(playerUUID, k -> new BlockTracker());
        tracker.increment();

        int count = tracker.getCount();
        if (count >= thresholdPerSecond && autoLockdown && !isLocked(playerUUID)) {
            triggerLockdown(playerUUID);
        }
    }

    public void triggerLockdown(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        LockdownState state = new LockdownState(playerUUID, player.getName());
        lockdownStates.put(playerUUID, state);

        // Notify player
        plugin.getMessenger().sendError(player, "SECURITY LOCKDOWN TRIGGERED. Block modification disabled. Contact a console administrator.");

        // Notify staff
        if (notifyStaff) {
            String message = "§c[ANTIRAID] §f" + player.getName() + " §chas been locked down for exceeding block threshold.";
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("foliacore.staff")) {
                    staff.sendMessage(message);
                }
            }
            plugin.getLogger().warning("[ANTIRAID] " + player.getName() + " triggered lockdown!");
        }

        saveData();
    }

    public void pausePlayer(UUID playerUUID, long durationMs) {
        long expiryTime = System.currentTimeMillis() + durationMs;
        pauseExpiryTimes.put(playerUUID, expiryTime);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            plugin.getMessenger().sendSuccess(player, "Block modification pause activated.");
        }
    }

    public void resetLockdown(UUID playerUUID) {
        lockdownStates.remove(playerUUID);
        playerTrackers.remove(playerUUID);
        saveData();
    }

    public boolean isLocked(UUID playerUUID) {
        return lockdownStates.containsKey(playerUUID);
    }

    public boolean canModifyBlocks(UUID playerUUID) {
        return !isLocked(playerUUID);
    }

    public LockdownState getLockdownState(UUID playerUUID) {
        return lockdownStates.get(playerUUID);
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
            dataConfig.set("lockdowns", null);
            for (Map.Entry<UUID, LockdownState> entry : lockdownStates.entrySet()) {
                String path = "lockdowns." + entry.getKey();
                dataConfig.set(path + ".player-name", entry.getValue().playerName);
                dataConfig.set(path + ".locked-time", entry.getValue().lockedTime);
                dataConfig.set(path + ".requires-console-reset", entry.getValue().requiresConsoleReset);
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save security data!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveDataSync());
    }
}
