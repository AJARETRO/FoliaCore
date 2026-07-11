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
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe vanish manager for staff members.
 * Tracks hidden players and handles visibility updates.
 */
public class VanishManager {

    private final FoliaCore plugin;
    private final Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> socialSpyEnabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> staffChatMode = ConcurrentHashMap.newKeySet();

    public VanishManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void toggleVanish(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        if (vanishedPlayers.contains(playerUUID)) {
            // Unvanish
            vanishedPlayers.remove(playerUUID);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }
        } else {
            // Vanish
            vanishedPlayers.add(playerUUID);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("foliacore.vanish.see")) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }
        }
    }

    public boolean isVanished(UUID playerUUID) {
        return vanishedPlayers.contains(playerUUID);
    }

    public void showToPlayer(Player viewer, Player vanished) {
        if (isVanished(vanished.getUniqueId())) {
            viewer.showPlayer(plugin, vanished);
        }
    }

    public void toggleSocialSpy(UUID playerUUID) {
        if (socialSpyEnabled.contains(playerUUID)) {
            socialSpyEnabled.remove(playerUUID);
        } else {
            socialSpyEnabled.add(playerUUID);
        }
    }

    public boolean hasSocialSpy(UUID playerUUID) {
        return socialSpyEnabled.contains(playerUUID);
    }

    public void toggleStaffChat(UUID playerUUID) {
        if (staffChatMode.contains(playerUUID)) {
            staffChatMode.remove(playerUUID);
        } else {
            staffChatMode.add(playerUUID);
        }
    }

    public boolean isInStaffChatMode(UUID playerUUID) {
        return staffChatMode.contains(playerUUID);
    }

    public void removePlayer(UUID playerUUID) {
        vanishedPlayers.remove(playerUUID);
        socialSpyEnabled.remove(playerUUID);
        staffChatMode.remove(playerUUID);
    }
}
