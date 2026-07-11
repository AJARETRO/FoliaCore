/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player-to-player ignore lists.
 * Allows players to ignore chat/private messages from others.
 */
public class IgnoreManager {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Set<UUID>> ignores = new ConcurrentHashMap<>();

    public IgnoreManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            ignores.putAll(plugin.getStorageManager().getProvider().loadIgnores());
        }
    }

    public void saveData() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            plugin.getStorageManager().getProvider().saveIgnores(ignores);
        }
    }

    public boolean ignorePlayer(UUID player, UUID target) {
        Set<UUID> set = ignores.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
        boolean added = set.add(target);
        if (added) {
            saveDataAsync();
        }
        return added;
    }

    public boolean unignorePlayer(UUID player, UUID target) {
        Set<UUID> set = ignores.get(player);
        if (set == null) return false;
        boolean removed = set.remove(target);
        if (removed) {
            saveDataAsync();
        }
        return removed;
    }

    public boolean isIgnoring(UUID player, UUID target) {
        Set<UUID> set = ignores.get(player);
        return set != null && set.contains(target);
    }

    public Set<UUID> getIgnoredPlayers(UUID player) {
        return ignores.getOrDefault(player, Collections.emptySet());
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> saveData());
    }
}
