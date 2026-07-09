package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Central configuration manager for FoliaCore.
 * Handles all module toggles and server settings with thread-safe access.
 */
public class ConfigManager {

    private static final String[] REGISTERED_COMMANDS = {
            "foliacore",
            "mute", "unmute", "msg", "reply", "block", "unblock", "mail", "chat",
            "sethome", "home", "delhome", "homes", "tpa", "tpahere", "tpaccept", "tpdeny",
            "setspawn", "spawn", "tp", "tphere", "back", "setfirstspawn",
            "team",
            "kit", "createkit", "delkit",
            "marker", "gps", "setwarp", "delwarp", "warp", "warps",
            "nick", "realname", "ban", "tempban", "unban", "kick", "fly", "heal", "feed",
            "god", "gamemode", "gms", "gmc", "gma", "gmsp", "give", "clear", "invsee",
            "enderchest", "ec", "workbench", "wb", "trash", "dispose", "repair", "hat",
            "broadcast", "time", "weather", "calc",
            "status", "ping", "clearchat",
            "antiraid",
            "vanish", "socialspy", "staffchat", "sc",
            "scoreboard", "sidebar",
            "balance", "bal", "pay", "eco", "sell", "worth",
            "jail", "unjail", "setjail", "deljail", "jails",
            "discord", "link", "unlink", "discordbroadcast",
            "ignore", "unignore", "ignorelist",
            "powertool", "pt",
            "ptime",
            "pweather",
            "rules",
            "antioch", "beezooka", "fireball", "lightning", "nuke", "spawnmob",
            "balancetop", "paytoggle", "payconfirmtoggle", "setworth",
            "msgtoggle", "rtoggle",
            "jailedplayers",
            "afk", "compass", "enchant", "exp", "ext", "firework", "jump", "kickall", "kittycannon", "list", "me", "more", "motd", "near", "tpoffline", "playtime", "potion", "recipe", "remove", "renamehome", "rest", "seen", "settpr", "showkit", "editsign", "skull", "speed", "sudo", "suicide", "tempbanip", "banip", "unbanip", "thunder", "tpall", "tpauto", "tpacancel", "tpo", "tpohere", "tppos", "tpr", "tptoggle", "tree", "unlimited", "warpinfo", "toggleshout", "anvil", "grindstone", "loom", "smithingtable", "stonecutter", "cartographytable"
    };

    private final FoliaCore plugin;
    private File configFile;
    private File securityFile;
    private FileConfiguration config;
    public FileConfiguration securityConfig;

    // Module toggles
    public boolean teleportEnabled;
    public boolean kitsEnabled;
    public boolean chatEnabled;
    public boolean markersEnabled;
    public boolean teamsEnabled;
    public boolean staffEnabled;
    public boolean systemEnabled;
    public boolean utilityEnabled;
    public boolean tabEnabled;
    public boolean sidebarEnabled;
    public boolean antiRaidEnabled;
    public boolean securityEnabled;
    public boolean economyEnabled;
    public boolean jailsEnabled;
    public boolean discordEnabled;

    // System settings
    public boolean maintenanceMode;
    public String maintenanceKickMessage;
    public boolean firstSpawnEnabled;
    public boolean entityCleanupEnabled;
    public int entityCleanupInterval;
    public int minimumTpsThreshold;
    public boolean autoBroadcasterEnabled;
    public int autoBroadcastInterval;
    public boolean startupBannerEnabled;
    public String startupOwnerDisplay;
    public boolean statusShowWorldSummary;
    public boolean statusShowRegionDetails;
    public int statusMaxRegions;
    public int statusRegionChunkSpan;

    public ConfigManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        securityFile = new File(plugin.getDataFolder(), "security.yml");
        
        if (!configFile.exists()) {
            createDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        
        if (!securityFile.exists()) {
            createDefaultSecurityConfig();
        }
        
        securityConfig = YamlConfiguration.loadConfiguration(securityFile);
        
        loadModuleToggles();
        loadSystemSettings();
        ensureCommandDefaults();

        plugin.getLogger().info("ConfigManager loaded successfully.");
    }

    private void createDefaultConfig() {
        try {
            plugin.saveResource("config.yml", false);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save default config.yml");
            createConfigProgrammatically();
        }
    }

    private void createConfigProgrammatically() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            config = new YamlConfiguration();

            // Module toggles
            config.set("modules.teleport", true);
            config.set("modules.kits", true);
            config.set("modules.chat", true);
            config.set("modules.markers", true);
            config.set("modules.teams", true);
            config.set("modules.staff", true);
            config.set("modules.system", true);
            config.set("modules.utility", true);
            config.set("modules.tab", true);
            config.set("modules.sidebar", true);
            config.set("modules.antiraid", true);
            config.set("modules.security", true);

            // System settings
            config.set("system.maintenance-mode", false);
            config.set("system.maintenance-kick-message", "&cServer is in maintenance mode. Admins only.");
            config.set("system.first-spawn-enabled", true);
            config.set("system.entity-cleanup-enabled", true);
            config.set("system.entity-cleanup-interval", 300);
            config.set("system.minimum-tps-threshold", 16);
            config.set("system.auto-broadcaster-enabled", true);
            config.set("system.auto-broadcast-interval", 600);

            // Branding / startup output
            config.set("branding.startup-banner-enabled", true);
            config.set("branding.owner-display", "&4AJA_R3TR0 &8x &bFoliaCore");

            // Status command output tuning
            config.set("status.show-world-summary", true);
            config.set("status.show-region-details", true);
            config.set("status.max-regions", 12);
            config.set("status.region-chunk-span", 8);

            // Per-command toggles and post-command action chains
            createCommandDefaults(config);

                // Animated TAB defaults
                config.set("tab.enabled", true);
                config.set("tab.update-interval-ticks", 20);
                config.set("tab.header-method", "method-1");
                config.set("tab.footer-method", "method-2");
                config.set("tab.methods.method-1", java.util.Arrays.asList(
                    "&6&lFoliaCore &ev3 Nightingale",
                    "&e&lFoliaCore &6v3 Nightingale",
                    "&f&lFoliaCore &ev3 Nightingale"
                ));
                config.set("tab.methods.method-2", java.util.Arrays.asList(
                    "&7Players: &a%online_players%&7/&a%max_players%",
                    "&7TPS: &b%server_tps% &8| &7Ping: &b%player_ping%ms",
                    "&7World: &d%world_name%"
                ));

                // Animated Sidebar defaults
                config.set("sidebar.enabled", true);
                config.set("sidebar.update-interval-ticks", 20);
                config.set("sidebar.title-method", "method-1");
                config.set("sidebar.line-methods", java.util.Arrays.asList(
                    "method-2", "method-3", "method-4", "method-5", "method-6"
                ));
                config.set("sidebar.methods.method-1", java.util.Arrays.asList(
                    "&6&lNightingale",
                    "&e&lNightingale",
                    "&f&lNightingale"
                ));
                config.set("sidebar.methods.method-2", java.util.Collections.singletonList("&7Player: &f%player_name%"));
                config.set("sidebar.methods.method-3", java.util.Collections.singletonList("&7Online: &a%online_players%&7/&a%max_players%"));
                config.set("sidebar.methods.method-4", java.util.Collections.singletonList("&7TPS: &b%server_tps%"));
                config.set("sidebar.methods.method-5", java.util.Collections.singletonList("&7Ping: &b%player_ping%ms"));
                config.set("sidebar.methods.method-6", java.util.Collections.singletonList("&7Coords: &f%x% &7/&f%y% &7/&f%z%"));
            
            // Anti-raid settings
            config.set("antiraid.enabled", true);
            config.set("antiraid.threshold-per-second", 50);
            config.set("antiraid.auto-lockdown", true);
            config.set("antiraid.notify-staff", true);
            
            // Security settings
            config.set("security.enabled", true);
            config.set("security.staff-ip-lock", true);
            config.set("security.require-console-for-unlock", true);

            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create config.yml programmatically!");
            e.printStackTrace();
        }
    }

    private void createDefaultSecurityConfig() {
        try {
            if (!securityFile.exists()) {
                securityFile.createNewFile();
            }

            securityConfig = new YamlConfiguration();
            
            // Trusted IPs for staff members
            securityConfig.set("staff-ip-lock.enabled", true);
            securityConfig.set("trusted_ips.ExampleStaff", java.util.Arrays.asList("127.0.0.1", "192.168.1.100"));
            
            securityConfig.save(securityFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create security.yml!");
        }
    }

    private void loadModuleToggles() {
        teleportEnabled = config.getBoolean("modules.teleport", true);
        kitsEnabled = config.getBoolean("modules.kits", true);
        chatEnabled = config.getBoolean("modules.chat", true);
        markersEnabled = config.getBoolean("modules.markers", true);
        teamsEnabled = config.getBoolean("modules.teams", true);
        staffEnabled = config.getBoolean("modules.staff", true);
        systemEnabled = config.getBoolean("modules.system", true);
        utilityEnabled = config.getBoolean("modules.utility", true);
        tabEnabled = config.getBoolean("modules.tab", true);
        sidebarEnabled = config.getBoolean("modules.sidebar", true);
        antiRaidEnabled = config.getBoolean("modules.antiraid", true);
        securityEnabled = config.getBoolean("modules.security", true);
        economyEnabled = config.getBoolean("modules.economy", true);
        jailsEnabled = config.getBoolean("modules.jails", true);
        discordEnabled = config.getBoolean("modules.discord", true);
    }

    private void loadSystemSettings() {
        maintenanceMode = config.getBoolean("system.maintenance-mode", false);
        maintenanceKickMessage = config.getString("system.maintenance-kick-message", "&cServer is in maintenance mode. Admins only.");
        firstSpawnEnabled = config.getBoolean("system.first-spawn-enabled", true);
        entityCleanupEnabled = config.getBoolean("system.entity-cleanup-enabled", true);
        entityCleanupInterval = config.getInt("system.entity-cleanup-interval", 300);
        minimumTpsThreshold = config.getInt("system.minimum-tps-threshold", 16);
        autoBroadcasterEnabled = config.getBoolean("system.auto-broadcaster-enabled", true);
        autoBroadcastInterval = config.getInt("system.auto-broadcast-interval", 600);
        startupBannerEnabled = config.getBoolean("branding.startup-banner-enabled", true);
        startupOwnerDisplay = config.getString("branding.owner-display", "&4AJA_R3TR0 &8x &bFoliaCore");
        statusShowWorldSummary = config.getBoolean("status.show-world-summary", true);
        statusShowRegionDetails = config.getBoolean("status.show-region-details", true);
        statusMaxRegions = Math.max(1, config.getInt("status.max-regions", 12));
        statusRegionChunkSpan = Math.max(1, config.getInt("status.region-chunk-span", 8));
    }

    private void ensureCommandDefaults() {
        if (config == null) {
            return;
        }

        boolean changed = false;
        if (!config.isConfigurationSection("commands")) {
            config.createSection("commands");
            changed = true;
        }

        for (String commandName : REGISTERED_COMMANDS) {
            String commandPath = commandPath(commandName);
            if (!config.isSet(commandPath + ".enabled")) {
                config.set(commandPath + ".enabled", true);
                changed = true;
            }
            if (!config.isSet(commandPath + ".playerdone")) {
                config.set(commandPath + ".playerdone", List.of());
                changed = true;
            }
            if (!config.isSet(commandPath + ".consoledone")) {
                config.set(commandPath + ".consoledone", List.of());
                changed = true;
            }
        }

        if (changed) {
            save();
        }
    }

    private void createCommandDefaults(FileConfiguration configuration) {
        configuration.createSection("commands");
        for (String commandName : REGISTERED_COMMANDS) {
            String commandPath = commandPath(commandName);
            configuration.set(commandPath + ".enabled", true);
            configuration.set(commandPath + ".playerdone", List.of());
            configuration.set(commandPath + ".consoledone", List.of());
        }
    }

    private String commandPath(String commandName) {
        return "commands." + commandName.toLowerCase(Locale.ROOT);
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config.yml!");
            e.printStackTrace();
        }
    }

    // Access raw config if needed
    public org.bukkit.configuration.file.FileConfiguration getConfig() { return config; }

    // Module toggles
    public boolean isTeleportEnabled() { return teleportEnabled; }
    public boolean isKitsEnabled() { return kitsEnabled; }
    public boolean isChatEnabled() { return chatEnabled; }
    public boolean isMarkersEnabled() { return markersEnabled; }
    public boolean isTeamsEnabled() { return teamsEnabled; }
    public boolean isStaffEnabled() { return staffEnabled; }
    public boolean isSystemEnabled() { return systemEnabled; }
    public boolean isUtilityEnabled() { return utilityEnabled; }
    public boolean isTabEnabled() { return tabEnabled; }
    public boolean isSidebarEnabled() { return sidebarEnabled; }
    public boolean isEconomyEnabled() { return economyEnabled; }
    public boolean isJailsEnabled() { return jailsEnabled; }
    public boolean isDiscordEnabled() { return discordEnabled; }

    // System settings
    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean enabled) {
        this.maintenanceMode = enabled;
        config.set("system.maintenance-mode", enabled);
        save();
    }

    public String getMaintenanceKickMessage() { return maintenanceKickMessage; }
    public boolean isFirstSpawnEnabled() { return firstSpawnEnabled; }
    public boolean isEntityCleanupEnabled() { return entityCleanupEnabled; }
    public int getEntityCleanupInterval() { return entityCleanupInterval; }
    public int getMinimumTpsThreshold() { return minimumTpsThreshold; }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
    public boolean isCommandEnabled(String commandName) {
        return config.getBoolean(commandPath(commandName) + ".enabled", true);
    }

    public List<String> getCommandPlayerDone(String commandName) {
        List<String> commands = config.getStringList(commandPath(commandName) + ".playerdone");
        return commands == null ? List.of() : commands;
    }

    public List<String> getCommandConsoleDone(String commandName) {
        List<String> commands = config.getStringList(commandPath(commandName) + ".consoledone");
        return commands == null ? List.of() : commands;
    }
    public boolean isAutoBroadcasterEnabled() { return autoBroadcasterEnabled; }
    public int getAutoBroadcastInterval() { return autoBroadcastInterval; }
    public boolean isStartupBannerEnabled() { return startupBannerEnabled; }
    public String getStartupOwnerDisplay() { return startupOwnerDisplay; }
    public boolean isStatusShowWorldSummary() { return statusShowWorldSummary; }
    public boolean isStatusShowRegionDetails() { return statusShowRegionDetails; }
    public int getStatusMaxRegions() { return statusMaxRegions; }
    public int getStatusRegionChunkSpan() { return statusRegionChunkSpan; }
}
