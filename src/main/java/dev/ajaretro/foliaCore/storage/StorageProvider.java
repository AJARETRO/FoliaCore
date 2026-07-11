/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.storage;

import dev.ajaretro.foliaCore.data.*;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface declaring methods for loading and saving plugin data.
 */
public interface StorageProvider {
    void init() throws Exception;
    void shutdown() throws Exception;

    // --- Core Systems ---
    // Warps
    void saveWarps(Collection<Warp> warps);
    Collection<Warp> loadWarps();

    // Homes
    void saveHomes(Map<UUID, Map<String, Home>> homes);
    Map<UUID, Map<String, Home>> loadHomes();

    // Spawns
    void saveSpawn(Location spawnLocation);
    Location loadSpawn();
    void saveFirstSpawn(Location firstSpawnLocation);
    Location loadFirstSpawn();

    // Kits
    void saveKits(Collection<Kit> kits);
    Collection<Kit> loadKits();

    // Bans
    void saveBans(Collection<Ban> bans);
    Collection<Ban> loadBans();

    // Mail
    void saveMail(Map<UUID, List<Mail>> mailboxes);
    Map<UUID, List<Mail>> loadMail();

    // Markers
    void saveMarkers(Collection<Marker> markers);
    Collection<Marker> loadMarkers();

    // Teams
    void saveTeams(Collection<Team> teams);
    Collection<Team> loadTeams();

    // --- New Systems ---
    // Economy
    void saveBalances(Map<UUID, Double> balances);
    Map<UUID, Double> loadBalances();

    // Jails
    void saveJails(Map<String, Location> jails);
    Map<String, Location> loadJails();
    void saveJailedPlayers(Map<UUID, JailedPlayer> jailedPlayers);
    Map<UUID, JailedPlayer> loadJailedPlayers();

    // Ignores
    void saveIgnores(Map<UUID, Set<UUID>> ignores);
    Map<UUID, Set<UUID>> loadIgnores();

    // Powertools
    void savePowertools(Map<UUID, Map<Material, String>> powertools);
    Map<UUID, Map<Material, String>> loadPowertools();
}
