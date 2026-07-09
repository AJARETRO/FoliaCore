package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player powertools.
 * Binds specific commands to materials that are executed on click.
 */
public class PowertoolManager {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Map<Material, String>> powertools = new ConcurrentHashMap<>();

    public PowertoolManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            powertools.putAll(plugin.getStorageManager().getProvider().loadPowertools());
        }
    }

    public void saveData() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            plugin.getStorageManager().getProvider().savePowertools(powertools);
        }
    }

    public void setPowertool(UUID uuid, Material material, String command) {
        powertools.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(material, command);
        saveDataAsync();
    }

    public void removePowertool(UUID uuid, Material material) {
        Map<Material, String> tools = powertools.get(uuid);
        if (tools != null) {
            tools.remove(material);
            saveDataAsync();
        }
    }

    public String getPowertool(UUID uuid, Material material) {
        Map<Material, String> tools = powertools.get(uuid);
        return tools == null ? null : tools.get(material);
    }

    public Map<Material, String> getPowertools(UUID uuid) {
        return powertools.getOrDefault(uuid, Collections.emptyMap());
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> saveData());
    }
}
