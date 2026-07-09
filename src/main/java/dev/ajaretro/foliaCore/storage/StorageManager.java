package dev.ajaretro.foliaCore.storage;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages plugin data storage initialization and provider resolution.
 */
public class StorageManager {
    private final FoliaCore plugin;
    private StorageProvider provider;

    public StorageManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes the storage provider based on configuration.
     */
    public void init() {
        FileConfiguration config = plugin.getConfigManager().getConfig();

        // Ensure storage configurations are set in memory/file defaults
        if (!config.isSet("storage.type")) {
            config.set("storage.type", "yaml");
            config.set("storage.mysql.host", "localhost");
            config.set("storage.mysql.port", 3306);
            config.set("storage.mysql.database", "minecraft");
            config.set("storage.mysql.username", "root");
            config.set("storage.mysql.password", "");
            config.set("storage.mysql.ssl", false);
            plugin.getConfigManager().save();
        }

        String type = config.getString("storage.type", "yaml").toLowerCase();

        if (type.equals("mysql")) {
            String host = config.getString("storage.mysql.host", "localhost");
            int port = config.getInt("storage.mysql.port", 3306);
            String database = config.getString("storage.mysql.database", "minecraft");
            String username = config.getString("storage.mysql.username", "root");
            String password = config.getString("storage.mysql.password", "");
            boolean ssl = config.getBoolean("storage.mysql.ssl", false);

            plugin.getLogger().info("Initializing MySQL connection pool...");
            this.provider = new MySqlStorageProvider(plugin, host, port, database, username, password, ssl);
        } else {
            plugin.getLogger().info("Initializing YAML flat-file storage...");
            this.provider = new YamlStorageProvider(plugin);
        }

        try {
            this.provider.init();
            plugin.getLogger().info("Unified storage provider (" + type.toUpperCase() + ") initialized successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize storage provider " + type.toUpperCase() + "! Falling back to YAML.");
            e.printStackTrace();
            this.provider = new YamlStorageProvider(plugin);
            try {
                this.provider.init();
            } catch (Exception ex) {
                plugin.getLogger().severe("Failed to initialize fallback YAML storage!");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Shuts down connection pools or active resources.
     */
    public void shutdown() {
        if (this.provider != null) {
            try {
                this.provider.shutdown();
                plugin.getLogger().info("Storage provider shut down successfully.");
            } catch (Exception e) {
                plugin.getLogger().severe("Error while shutting down storage provider:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the active storage provider.
     * @return storage provider instance.
     */
    public StorageProvider getProvider() {
        return provider;
    }
}
