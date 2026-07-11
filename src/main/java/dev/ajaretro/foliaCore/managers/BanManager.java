/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Ban;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe manager for player bans.
 * Handles permanent and temporary bans, with automatic expiry checking.
 */
public class BanManager {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Ban> bans;
    private final ConcurrentHashMap<String, Ban> bansByName;
    private File dataFile;
    private FileConfiguration dataConfig;

    public BanManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.bans = new ConcurrentHashMap<>();
        this.bansByName = new ConcurrentHashMap<>();
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "bans.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("bans.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadBans();
    }

    private void loadBans() {
        ConfigurationSection bansSection = dataConfig.getConfigurationSection("bans");
        if (bansSection == null) return;

        for (String uuid : bansSection.getKeys(false)) {
            try {
                ConfigurationSection banSection = bansSection.getConfigurationSection(uuid);
                if (banSection == null) continue;

                Ban ban = Ban.deserialize(banSection.getValues(false));
                
                // Skip expired temporary bans
                if (ban.isExpired()) {
                    continue;
                }
                
                bans.put(ban.getPlayerUUID(), ban);
                bansByName.put(ban.getPlayerName().toLowerCase(), ban);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load ban: " + uuid);
            }
        }
    }

    public void banPlayer(UUID playerUUID, String playerName, String reason, boolean permanent, long expiryTime) {
        long banTime = System.currentTimeMillis();
        Ban ban = new Ban(playerUUID, playerName, reason, banTime, expiryTime, permanent);
        
        bans.put(playerUUID, ban);
        bansByName.put(playerName.toLowerCase(), ban);
        
        saveData();
    }

    public void unbanPlayer(UUID playerUUID) {
        Ban ban = bans.remove(playerUUID);
        if (ban != null) {
            bansByName.remove(ban.getPlayerName().toLowerCase());
        }
        saveData();
    }

    public Ban getBan(UUID playerUUID) {
        Ban ban = bans.get(playerUUID);
        if (ban != null && ban.isExpired()) {
            unbanPlayer(playerUUID);
            return null;
        }
        return ban;
    }

    public Ban getBanByName(String playerName) {
        Ban ban = bansByName.get(playerName.toLowerCase());
        if (ban != null && ban.isExpired()) {
            unbanPlayer(ban.getPlayerUUID());
            return null;
        }
        return ban;
    }

    public boolean isBanned(UUID playerUUID) {
        return getBan(playerUUID) != null;
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
            dataConfig.set("bans", null);
            for (Map.Entry<UUID, Ban> entry : bans.entrySet()) {
                dataConfig.set("bans." + entry.getKey(), entry.getValue().serialize());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save bans to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveDataSync());
    }
}
