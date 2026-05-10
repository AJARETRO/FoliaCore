package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;

/**
 * Manages spawn locations including first join spawn.
 */
public class SpawnManager {

    private final FoliaCore plugin;
    private volatile Location spawnLocation;
    private volatile Location firstSpawnLocation;
    private File dataFile;

    public SpawnManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spawn_data.yml");
    }

    public void load() {
        // Load from dataFile or use default spawn
        if (!dataFile.exists()) {
            plugin.getLogger().info("No spawn data found. Using world spawn.");
        } else {
            try {
                org.bukkit.configuration.file.YamlConfiguration config = 
                    org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(dataFile);
                
                if (config.contains("spawn")) {
                    spawnLocation = config.getLocation("spawn");
                }
                if (config.contains("first_spawn")) {
                    firstSpawnLocation = config.getLocation("first_spawn");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load spawn data: " + e.getMessage());
            }
        }
    }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        saveData();
    }

    public void setFirstSpawn(Location location) {
        this.firstSpawnLocation = location;
        saveData();
    }

    public Location getSpawn() {
        if (spawnLocation != null) {
            return spawnLocation;
        }
        World world = Bukkit.getWorlds().get(0);
        return world != null ? world.getSpawnLocation() : null;
    }

    public Location getFirstSpawn() {
        return firstSpawnLocation != null ? firstSpawnLocation : getSpawn();
    }

    public void saveData() {
        if (!plugin.isEnabled()) {
            saveDataSync();
            return;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveDataSync());
    }

    private void saveDataSync() {
        try {
            org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
            if (spawnLocation != null) {
                config.set("spawn", spawnLocation.serialize());
            }
            if (firstSpawnLocation != null) {
                config.set("first_spawn", firstSpawnLocation.serialize());
            }
            config.save(dataFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save spawn data: " + e.getMessage());
        }
    }

    /**
     * Checks if a location is safe for teleportation (AIR above, AIR at level, SOLID below).
     */
    public boolean isSafeLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;

        Location above = loc.clone().add(0, 1, 0);
        Location below = loc.clone().add(0, -1, 0);

        Material blockMat = loc.getBlock().getType();
        Material aboveMat = above.getBlock().getType();
        Material belowMat = below.getBlock().getType();

        return blockMat.isAir() && aboveMat.isAir() && !belowMat.isAir() && belowMat.isSolid();
    }

    /**
     * Returns whether players should be teleported to spawn on respawn.
     */
    public boolean teleportOnRespawn() {
        return plugin.getConfigManager().getBoolean("teleport.spawn-on-respawn", false);
    }
}
