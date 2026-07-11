/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.storage;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class YamlStorageProvider implements StorageProvider {
    private final FoliaCore plugin;
    private final File dataFolder;

    public YamlStorageProvider(FoliaCore plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    @Override
    public void init() throws Exception {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    @Override
    public void shutdown() throws Exception {
        // Nothing to clean up for YAML
    }

    // --- Warps ---
    @Override
    public void saveWarps(Collection<Warp> warps) {
        File file = new File(dataFolder, "warps.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Warp warp : warps) {
            config.set("warps." + warp.getName().toLowerCase(), warp.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save warps to YAML: " + e.getMessage());
        }
    }

    @Override
    public Collection<Warp> loadWarps() {
        File file = new File(dataFolder, "warps.yml");
        if (!file.exists()) return Collections.emptyList();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("warps");
        if (section == null) return Collections.emptyList();

        List<Warp> warps = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection warpSec = section.getConfigurationSection(key);
            if (warpSec != null) {
                warps.add(Warp.deserialize(key, warpSec.getValues(false)));
            }
        }
        return warps;
    }

    // --- Homes / Spawns ---
    @Override
    public void saveHomes(Map<UUID, Map<String, Home>> homes) {
        File file = new File(dataFolder, "teleport_data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("homes", null);
        for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                config.set("homes." + uuidStr + "." + homeEntry.getKey().toLowerCase(), homeEntry.getValue().serialize());
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save homes to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Map<String, Home>> loadHomes() {
        File file = new File(dataFolder, "teleport_data.yml");
        if (!file.exists()) return new ConcurrentHashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) return new ConcurrentHashMap<>();

        Map<UUID, Map<String, Home>> loaded = new ConcurrentHashMap<>();
        for (String uuidStr : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection userSec = section.getConfigurationSection(uuidStr);
                if (userSec != null) {
                    Map<String, Home> userHomes = new ConcurrentHashMap<>();
                    for (String homeName : userSec.getKeys(false)) {
                        ConfigurationSection homeSec = userSec.getConfigurationSection(homeName);
                        if (homeSec != null) {
                            userHomes.put(homeName.toLowerCase(), Home.deserialize(homeSec.getValues(false)));
                        }
                    }
                    loaded.put(uuid, userHomes);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load homes for UUID: " + uuidStr);
            }
        }
        return loaded;
    }

    @Override
    public void saveSpawn(Location spawnLocation) {
        File file = new File(dataFolder, "teleport_data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (spawnLocation != null) {
            config.set("spawn", spawnLocation.serialize());
        } else {
            config.set("spawn", null);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save spawn location to YAML: " + e.getMessage());
        }
    }

    @Override
    public Location loadSpawn() {
        File file = new File(dataFolder, "teleport_data.yml");
        if (!file.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("spawn");
        if (section == null) return null;
        try {
            return Location.deserialize(section.getValues(false));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveFirstSpawn(Location firstSpawnLocation) {
        File file = new File(dataFolder, "teleport_data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (firstSpawnLocation != null) {
            config.set("first_spawn", firstSpawnLocation.serialize());
        } else {
            config.set("first_spawn", null);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save first spawn location to YAML: " + e.getMessage());
        }
    }

    @Override
    public Location loadFirstSpawn() {
        File file = new File(dataFolder, "teleport_data.yml");
        if (!file.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("first_spawn");
        if (section == null) return null;
        try {
            return Location.deserialize(section.getValues(false));
        } catch (Exception e) {
            return null;
        }
    }

    // --- Kits ---
    @Override
    public void saveKits(Collection<Kit> kits) {
        File file = new File(dataFolder, "kits.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Kit kit : kits) {
            config.set("kits." + kit.getName().toLowerCase(), kit.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kits to YAML: " + e.getMessage());
        }
    }

    @Override
    public Collection<Kit> loadKits() {
        File file = new File(dataFolder, "kits.yml");
        if (!file.exists()) return Collections.emptyList();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("kits");
        if (section == null) return Collections.emptyList();

        List<Kit> kits = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection kitSec = section.getConfigurationSection(key);
            if (kitSec != null) {
                kits.add(Kit.deserialize(key, kitSec.getValues(false)));
            }
        }
        return kits;
    }

    // --- Bans ---
    @Override
    public void saveBans(Collection<Ban> bans) {
        File file = new File(dataFolder, "bans.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Ban ban : bans) {
            config.set("bans." + ban.getPlayerUUID().toString(), ban.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save bans to YAML: " + e.getMessage());
        }
    }

    @Override
    public Collection<Ban> loadBans() {
        File file = new File(dataFolder, "bans.yml");
        if (!file.exists()) return Collections.emptyList();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("bans");
        if (section == null) return Collections.emptyList();

        List<Ban> bans = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection banSec = section.getConfigurationSection(key);
            if (banSec != null) {
                bans.add(Ban.deserialize(banSec.getValues(false)));
            }
        }
        return bans;
    }

    // --- Mail ---
    @Override
    public void saveMail(Map<UUID, List<Mail>> mailboxes) {
        File file = new File(dataFolder, "chat_data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("mail", null);
        for (Map.Entry<UUID, List<Mail>> entry : mailboxes.entrySet()) {
            List<Map<String, Object>> serializedMail = new ArrayList<>();
            synchronized (entry.getValue()) {
                for (Mail mail : entry.getValue()) {
                    Map<String, Object> mailMap = new LinkedHashMap<>();
                    mailMap.put("sender", mail.getSender().toString());
                    mailMap.put("timestamp", mail.getTimestamp());
                    mailMap.put("message", mail.getMessage());
                    serializedMail.add(mailMap);
                }
            }
            config.set("mail." + entry.getKey().toString(), serializedMail);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save mail to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, List<Mail>> loadMail() {
        File file = new File(dataFolder, "chat_data.yml");
        if (!file.exists()) return new ConcurrentHashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("mail");
        if (section == null) return new ConcurrentHashMap<>();

        Map<UUID, List<Mail>> mailboxes = new ConcurrentHashMap<>();
        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<?> mailList = config.getList("mail." + key);
                List<Mail> loadedMail = new ArrayList<>();
                if (mailList != null) {
                    for (Object obj : mailList) {
                        if (obj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map = (Map<String, Object>) obj;
                            loadedMail.add(new Mail(
                                    UUID.fromString((String) map.get("sender")),
                                    ((Number) map.get("timestamp")).longValue(),
                                    (String) map.get("message")
                            ));
                        }
                    }
                }
                mailboxes.put(uuid, Collections.synchronizedList(loadedMail));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load mail for UUID: " + key);
            }
        }
        return mailboxes;
    }

    // --- Markers ---
    @Override
    public void saveMarkers(Collection<Marker> markers) {
        File file = new File(dataFolder, "markers.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Marker marker : markers) {
            config.set("markers." + marker.getName().toLowerCase(), marker.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save markers to YAML: " + e.getMessage());
        }
    }

    @Override
    public Collection<Marker> loadMarkers() {
        File file = new File(dataFolder, "markers.yml");
        if (!file.exists()) return Collections.emptyList();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("markers");
        if (section == null) return Collections.emptyList();

        List<Marker> markers = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection markerSec = section.getConfigurationSection(key);
            if (markerSec != null) {
                markers.add(Marker.deserialize(key, markerSec.getValues(false)));
            }
        }
        return markers;
    }

    // --- Teams ---
    @Override
    public void saveTeams(Collection<Team> teams) {
        File file = new File(dataFolder, "team_data.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Team team : teams) {
            config.set("teams." + team.getName().toLowerCase(), team.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teams to YAML: " + e.getMessage());
        }
    }

    @Override
    public Collection<Team> loadTeams() {
        File file = new File(dataFolder, "team_data.yml");
        if (!file.exists()) return Collections.emptyList();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("teams");
        if (section == null) return Collections.emptyList();

        List<Team> teams = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection teamSec = section.getConfigurationSection(key);
            if (teamSec != null) {
                teams.add(Team.deserialize(key, teamSec.getValues(false)));
            }
        }
        return teams;
    }

    // --- New Systems (Custom separate YAML files) ---

    // Economy Balance storage (balances.yml)
    @Override
    public void saveBalances(Map<UUID, Double> balances) {
        File file = new File(dataFolder, "balances.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save balances to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Double> loadBalances() {
        File file = new File(dataFolder, "balances.yml");
        Map<UUID, Double> loaded = new ConcurrentHashMap<>();
        if (!file.exists()) return loaded;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("balances");
        if (section == null) return loaded;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double balance = config.getDouble("balances." + key);
                loaded.put(uuid, balance);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load balance for: " + key);
            }
        }
        return loaded;
    }

    // Jails / Jailed players storage (jails.yml)
    @Override
    public void saveJails(Map<String, Location> jails) {
        File file = new File(dataFolder, "jails.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("jails", null);
        for (Map.Entry<String, Location> entry : jails.entrySet()) {
            config.set("jails." + entry.getKey().toLowerCase(), entry.getValue().serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save jails to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Location> loadJails() {
        File file = new File(dataFolder, "jails.yml");
        Map<String, Location> loaded = new ConcurrentHashMap<>();
        if (!file.exists()) return loaded;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("jails");
        if (section == null) return loaded;

        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection locSec = section.getConfigurationSection(key);
                if (locSec != null) {
                    loaded.put(key.toLowerCase(), Location.deserialize(locSec.getValues(false)));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load jail location: " + key);
            }
        }
        return loaded;
    }

    @Override
    public void saveJailedPlayers(Map<UUID, JailedPlayer> jailedPlayers) {
        File file = new File(dataFolder, "jails.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("jailed_players", null);
        for (Map.Entry<UUID, JailedPlayer> entry : jailedPlayers.entrySet()) {
            config.set("jailed_players." + entry.getKey().toString(), entry.getValue().serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save jailed players to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, JailedPlayer> loadJailedPlayers() {
        File file = new File(dataFolder, "jails.yml");
        Map<UUID, JailedPlayer> loaded = new ConcurrentHashMap<>();
        if (!file.exists()) return loaded;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("jailed_players");
        if (section == null) return loaded;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection playerSec = section.getConfigurationSection(key);
                if (playerSec != null) {
                    loaded.put(uuid, JailedPlayer.deserialize(playerSec.getValues(false)));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load jailed player data: " + key);
            }
        }
        return loaded;
    }

    // Ignore lists storage (ignores.yml)
    @Override
    public void saveIgnores(Map<UUID, Set<UUID>> ignores) {
        File file = new File(dataFolder, "ignores.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> entry : ignores.entrySet()) {
            List<String> ignoredStrings = new ArrayList<>();
            for (UUID uuid : entry.getValue()) {
                ignoredStrings.add(uuid.toString());
            }
            config.set("ignores." + entry.getKey().toString(), ignoredStrings);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save ignores to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Set<UUID>> loadIgnores() {
        File file = new File(dataFolder, "ignores.yml");
        Map<UUID, Set<UUID>> loaded = new ConcurrentHashMap<>();
        if (!file.exists()) return loaded;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("ignores");
        if (section == null) return loaded;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<String> list = config.getStringList("ignores." + key);
                Set<UUID> set = ConcurrentHashMap.newKeySet();
                for (String s : list) {
                    set.add(UUID.fromString(s));
                }
                loaded.put(uuid, set);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load ignores for player: " + key);
            }
        }
        return loaded;
    }

    // Powertools storage (powertools.yml)
    @Override
    public void savePowertools(Map<UUID, Map<Material, String>> powertools) {
        File file = new File(dataFolder, "powertools.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<Material, String>> entry : powertools.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<Material, String> ptEntry : entry.getValue().entrySet()) {
                config.set("powertools." + uuidStr + "." + ptEntry.getKey().name(), ptEntry.getValue());
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save powertools to YAML: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Map<Material, String>> loadPowertools() {
        File file = new File(dataFolder, "powertools.yml");
        Map<UUID, Map<Material, String>> loaded = new ConcurrentHashMap<>();
        if (!file.exists()) return loaded;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("powertools");
        if (section == null) return loaded;

        for (String uuidStr : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection ptSec = section.getConfigurationSection(uuidStr);
                if (ptSec != null) {
                    Map<Material, String> userTools = new ConcurrentHashMap<>();
                    for (String matName : ptSec.getKeys(false)) {
                        try {
                            Material material = Material.valueOf(matName.toUpperCase());
                            String cmd = ptSec.getString(matName);
                            if (cmd != null) {
                                userTools.put(material, cmd);
                            }
                        } catch (Exception ignored) {}
                    }
                    loaded.put(uuid, userTools);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load powertools for UUID: " + uuidStr);
            }
        }
        return loaded;
    }
}
