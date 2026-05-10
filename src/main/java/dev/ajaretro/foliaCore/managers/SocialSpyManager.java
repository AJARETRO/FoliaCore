package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages social spy state for staff members.
 * Allows staff to monitor private messages and chat.
 */
public class SocialSpyManager {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Boolean> spyToggle = new ConcurrentHashMap<>();

    public SocialSpyManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void toggleSocialSpy(UUID playerUUID) {
        boolean current = spyToggle.getOrDefault(playerUUID, false);
        spyToggle.put(playerUUID, !current);
    }

    public boolean hasSocialSpy(UUID playerUUID) {
        return spyToggle.getOrDefault(playerUUID, false);
    }

    public void setSocialSpy(UUID playerUUID, boolean enabled) {
        if (enabled) {
            spyToggle.put(playerUUID, true);
        } else {
            spyToggle.remove(playerUUID);
        }
    }

    public void handlePlayerQuit(UUID playerUUID) {
        spyToggle.remove(playerUUID);
    }
}
