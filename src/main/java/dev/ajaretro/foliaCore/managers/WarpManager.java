package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<String, Warp> warps;
    private File dataFile;
    private FileConfiguration dataConfig;

    public WarpManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.warps = new ConcurrentHashMap<>();
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("warps.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadWarps();
    }

    private void loadWarps() {
        ConfigurationSection warpsSection = dataConfig.getConfigurationSection("warps");
        if (warpsSection == null) return;

        for (String warpName : warpsSection.getKeys(false)) {
            try {
                ConfigurationSection warpSection = warpsSection.getConfigurationSection(warpName);
                if (warpSection == null) continue;

                Map<String, Object> warpData = warpSection.getValues(false);
                Warp warp = Warp.deserialize(warpName, warpData);
                warps.put(warpName.toLowerCase(), warp);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load warp: " + warpName);
            }
        }
    }

    public void saveData() {
        try {
            dataConfig.set("warps", null); // Clear old warps
            for (Map.Entry<String, Warp> entry : warps.entrySet()) {
                dataConfig.set("warps." + entry.getKey(), entry.getValue().serialize());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveData());
    }

    public void createWarp(String name, Location location) {
        Warp warp = new Warp(name, location);
        warps.put(name.toLowerCase(), warp);
        saveDataAsync();
    }

    public void deleteWarp(String name) {
        warps.remove(name.toLowerCase());
        saveDataAsync();
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean isWarp(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public Collection<Warp> getAllWarps() {
        return warps.values();
    }
}