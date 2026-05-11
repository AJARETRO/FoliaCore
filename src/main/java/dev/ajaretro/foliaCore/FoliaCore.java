package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.tasks.EntityCleanupTask;
import dev.ajaretro.foliaCore.tasks.AutoBroadcasterTask;
import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.*;
import dev.ajaretro.foliaCore.managers.*;
import dev.ajaretro.foliaCore.utils.Messenger;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.charts.SimplePie;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FoliaCore: A multi-threaded plugin suite for Paper/Folia servers.
 * 
 * This plugin provides essential gameplay features optimized for Folia's 
 * multi-threaded architecture, including chat management, teleportation,
 * teams, kits, warps, and more.
 * 
 * Key Features:
 * - Async command handling and scheduling
 * - Thread-safe data management with ConcurrentHashMap
 * - QoL and admin utility toolkit
 * - bStats metrics integration
 * - Modular configuration system
 * - Staff utilities and performance monitoring
 * 
 * @author AJARETRO
 * @version v-3.2 Blue Nightingale
 */
public final class FoliaCore extends JavaPlugin {

    private static FoliaCore instance;

    // Core managers
    private ConfigManager configManager;
    private Messenger messenger;
    private DisplayManager displayManager;

    // Feature managers (conditionally loaded)
    private ChatManager chatManager;
    private TeleportManager teleportManager;
    private TeamManager teamManager;
    private KitManager kitManager;
    private WarpManager warpManager;
    private MarkerManager markerManager;
    private BanManager banManager;

    // Staff & system managers
    private VanishManager vanishManager;
    private SocialSpyManager socialSpyManager;
    private SpawnManager spawnManager;
    private AntiRaidManager antiRaidManager;

    // God mode state
    private final ConcurrentHashMap<UUID, Boolean> godModePlayers = new ConcurrentHashMap<>();

    // Tasks
    private EntityCleanupTask entityCleanupTask;
    private AutoBroadcasterTask autoBroadcasterTask;
    private ModrinthUpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;

        // Load config FIRST
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.messenger = new Messenger("&l[ &4AJA_RETRO/&3FoliaCore&f ]");
        this.updateChecker = new ModrinthUpdateChecker(this);
        this.displayManager = new DisplayManager(this);

        // Setup metrics
        int pluginId = 28430;
        try {
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new SimplePie("chart_id", () -> "My Value"));
            getLogger().info("bStats metrics enabled.");
        } catch (Exception e) {
            getLogger().warning("Failed to enable bStats metrics.");
        }

        // Verify Folia support
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            getLogger().severe("This plugin requires Folia! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize core managers (always available)
        this.vanishManager = new VanishManager(this);
        this.socialSpyManager = new SocialSpyManager(this);
        this.spawnManager = new SpawnManager(this);
        this.banManager = new BanManager(this);
        this.antiRaidManager = new AntiRaidManager(this);

        // Initialize feature managers conditionally
        if (configManager.chatEnabled) {
            this.chatManager = new ChatManager(this);
        }
        if (configManager.teleportEnabled) {
            this.teleportManager = new TeleportManager(this);
        }
        if (configManager.teamsEnabled) {
            this.teamManager = new TeamManager(this);
        }
        if (configManager.kitsEnabled) {
            this.kitManager = new KitManager(this);
        }
        if (configManager.utilityEnabled) {
            this.warpManager = new WarpManager(this);
            this.markerManager = new MarkerManager(this);
        }

        loadSubsystems();
        registerListeners();
        registerCommands();

        // Start performance tasks if enabled
        if (configManager.systemEnabled) {
            this.entityCleanupTask = new EntityCleanupTask(this);
            this.entityCleanupTask.start();
            
            this.autoBroadcasterTask = new AutoBroadcasterTask(this);
            this.autoBroadcasterTask.start();
            
            getLogger().info("System tasks started.");
        }

        if ((configManager.isTabEnabled() || configManager.isSidebarEnabled()) && displayManager != null) {
            displayManager.start();
        }

        printStartupBanner();

        if (this.updateChecker != null) {
            this.updateChecker.checkForUpdates();
        }

        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&l&4[FoliaCore] &aPlugin initialized successfully! &7(Backend: REGIONIZED)")
        );
    }

    @Override
    public void onDisable() {
        if (configManager.chatEnabled && chatManager != null) chatManager.saveData();
        if (configManager.teleportEnabled && teleportManager != null) teleportManager.saveData();
        if (configManager.teamsEnabled && teamManager != null) teamManager.saveData();
        if (configManager.kitsEnabled && kitManager != null) kitManager.saveData();
        if (configManager.utilityEnabled) {
            if (warpManager != null) warpManager.saveData();
            if (markerManager != null) markerManager.saveData();
        }
        if (banManager != null) banManager.saveData();
        if (antiRaidManager != null) antiRaidManager.saveData();
        if (spawnManager != null) spawnManager.saveData();

        getLogger().info("FoliaCore shutdown sequence completed.");
    }

    private void loadSubsystems() {
        if (configManager.chatEnabled && chatManager != null) chatManager.load();
        if (configManager.teleportEnabled && teleportManager != null) teleportManager.load();
        if (configManager.teamsEnabled && teamManager != null) teamManager.load();
        if (configManager.kitsEnabled && kitManager != null) kitManager.load();
        if (configManager.utilityEnabled) {
            if (warpManager != null) warpManager.load();
            if (markerManager != null) markerManager.load();
        }
        if (antiRaidManager != null) antiRaidManager.load();
        if (banManager != null) banManager.load();
        if (spawnManager != null) spawnManager.load();
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        if (configManager.chatEnabled) {
            pm.registerEvents(new ChatListener(this), this);
            pm.registerEvents(new MailListener(this), this);
        }

        if (configManager.teleportEnabled) {
            pm.registerEvents(new PlayerMoveListener(this), this);
            pm.registerEvents(new SpawnListener(this), this);
        }

        if (configManager.kitsEnabled) {
            pm.registerEvents(new KitGUIListener(this), this);
        }

        if (configManager.utilityEnabled) {
            pm.registerEvents(new PlayerGpsListener(this), this);
        }

        // ConnectionListener handles update notifications and display; register always but guard internally
        pm.registerEvents(new ConnectionListener(this), this);

        if (configManager.antiRaidEnabled) {
            pm.registerEvents(new BlockChangeListener(this), this);
        }
        if (configManager.securityEnabled) {
            pm.registerEvents(new SecurityListener(this), this);
        }
        pm.registerEvents(new GodModeListener(this), this);
        pm.registerEvents(new MaintenanceListener(this), this);
    }

    private void registerCommands() {
        if (configManager.chatEnabled) {
            registerCommandSafe("mute", new MuteCommand(this));
            registerCommandSafe("unmute", new UnmuteCommand(this));
            registerCommandSafe("msg", new MsgCommand(this));
            registerCommandSafe("reply", new ReplyCommand(this));
            registerCommandSafe("block", new BlockCommand(this));
            registerCommandSafe("unblock", new UnblockCommand(this));
            registerCommandSafe("mail", new MailCommand(this));
            registerCommandSafe("chat", new ChatCommand(this));
        }

        if (configManager.teleportEnabled) {
            registerCommandSafe("sethome", new SetHomeCommand(this));
            registerCommandSafe("home", new HomeCommand(this));
            registerCommandSafe("delhome", new DelHomeCommand(this));
            registerCommandSafe("homes", new HomesCommand(this));
            registerCommandSafe("tpa", new TpaCommand(this));
            registerCommandSafe("tpahere", new TpaHereCommand(this));
            registerCommandSafe("tpaccept", new TpAcceptCommand(this));
            registerCommandSafe("tpdeny", new TpDenyCommand(this));
            registerCommandSafe("setspawn", new SetSpawnCommand(this));
            registerCommandSafe("spawn", new SpawnCommand(this));
            registerCommandSafe("tp", new TpCommand(this));
            registerCommandSafe("tphere", new TphereCommand(this));
            registerCommandSafe("back", new BackCommand(this));
            registerCommandSafe("setfirstspawn", new SetFirstSpawnCommand(this));
        }

        if (configManager.teamsEnabled) {
            registerCommandSafe("team", new TeamCommand(this));
        }

        if (configManager.kitsEnabled) {
            registerCommandSafe("kit", new KitCommand(this));
            registerCommandSafe("createkit", new CreateKitCommand(this));
            registerCommandSafe("delkit", new DeleteKitCommand(this));
        }

        if (configManager.utilityEnabled) {
            registerCommandSafe("marker", new MarkerCommand(this));
            registerCommandSafe("gps", new GpsCommand(this));
            registerCommandSafe("setwarp", new SetWarpCommand(this));
            registerCommandSafe("delwarp", new DelWarpCommand(this));
            registerCommandSafe("warp", new WarpCommand(this));
            registerCommandSafe("warps", new WarpsCommand(this));
        }

        registerCommandSafe("nick", new NickCommand(this));
        registerCommandSafe("realname", new RealNameCommand(this));
        registerCommandSafe("ban", new BanCommand(this));
        registerCommandSafe("tempban", new TempbanCommand(this));
        registerCommandSafe("unban", new UnbanCommand(this));
        registerCommandSafe("kick", new KickCommand(this));
        registerCommandSafe("fly", new FlyCommand(this));
        registerCommandSafe("heal", new HealCommand(this));
        registerCommandSafe("feed", new FeedCommand(this));
        registerCommandSafe("god", new GodCommand(this));
        registerCommandSafe("gamemode", new GamemodeCommand(this));
        registerCommandSafe("gms", new GamemodeCommand(this));
        registerCommandSafe("gmc", new GamemodeCommand(this));
        registerCommandSafe("gma", new GamemodeCommand(this));
        registerCommandSafe("gmsp", new GamemodeCommand(this));
        registerCommandSafe("give", new GiveCommand(this));
        registerCommandSafe("clear", new ClearCommand(this));
        registerCommandSafe("invsee", new InvseeCommand(this));
        registerCommandSafe("enderchest", new EnderchestCommand(this));
        registerCommandSafe("ec", new EnderchestCommand(this));
        registerCommandSafe("workbench", new WorkbenchCommand(this));
        registerCommandSafe("wb", new WorkbenchCommand(this));
        registerCommandSafe("trash", new TrashCommand(this));
        registerCommandSafe("dispose", new TrashCommand(this));
        registerCommandSafe("repair", new RepairCommand(this));
        registerCommandSafe("hat", new HatCommand(this));
        registerCommandSafe("broadcast", new BroadcastCommand(this));
        registerCommandSafe("time", new TimeCommand(this));
        registerCommandSafe("weather", new WeatherCommand(this));
        registerCommandSafe("calc", new CalcCommand(this));

        if (configManager.systemEnabled) {
            registerCommandSafe("status", new StatusCommand(this));
            registerCommandSafe("ping", new PingCommand(this));
            registerCommandSafe("clearchat", new ClearChatCommand(this));
        }

        if (configManager.antiRaidEnabled) {
            registerCommandSafe("antiraid", new AntiRaidCommand(this));
        }

        if (configManager.staffEnabled) {
            registerCommandSafe("vanish", new VanishCommand(this));
            registerCommandSafe("socialspy", new SocialSpyCommand(this));
            registerCommandSafe("staffchat", new StaffChatCommand(this));
            registerCommandSafe("sc", new StaffChatCommand(this));
        }
    }

    private void registerCommandSafe(String name, org.bukkit.command.CommandExecutor executor) {
        // Modern Paper API: use registerCommand() with BasicCommand wrapper
        try {
            final org.bukkit.command.Command bridgeCommand = new org.bukkit.command.Command(name) {
                @Override
                public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                    return executor.onCommand(sender, this, commandLabel, args);
                }
            };
            var basicCmd = new io.papermc.paper.command.brigadier.BasicCommand() {
                @Override
                public void execute(io.papermc.paper.command.brigadier.CommandSourceStack commandSourceStack, 
                                   String[] args) {
                    executor.onCommand(commandSourceStack.getSender(), bridgeCommand, name, args);
                }
            };
            this.registerCommand(name, basicCmd);
        } catch (Exception e) {
            getLogger().warning("Failed to register command '" + name + "': " + e.getMessage());
        }
    }

    private void printStartupBanner() {
        if (!configManager.isStartupBannerEnabled()) {
            return;
        }

        String owner = configManager.getStartupOwnerDisplay();
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8&m════════════════════════════════════════════════════════════════════════════════════")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("")
        );
        Bukkit.getConsoleSender().sendMessage(
            LegacyComponentSerializer.legacyAmpersand().deserialize("&l&6   ✦ &b&lFOLIACORE &3v3.2&b&l BLUE NIGHTINGALE &6✦")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&f   Folia-Native Essentials Suite")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&7   ⟶ &aRegionalized ThreadPool &7| &aModular Architecture &7| &aReal-time Telemetry")
        );
        Bukkit.getConsoleSender().sendMessage(
            LegacyComponentSerializer.legacyAmpersand().deserialize("&7   ⟶ &aQoL Utilities &7| &aAdmin Control Suite &7| &abStats Metrics")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8┌────────────────────────────────────────────────────────────────────────────────────┐")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8│ &b&lAJA&f&lRETRO &8│ " + owner + " &8│")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8└────────────────────────────────────────────────────────────────────────────────────┘")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8&m════════════════════════════════════════════════════════════════════════════════════")
        );
        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&8")
        );
    }

    public static FoliaCore getInstance() { return instance; }
    public AntiRaidManager getAntiRaidManager() { return antiRaidManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public ChatManager getChatManager() { return chatManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public KitManager getKitManager() { return kitManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public MarkerManager getMarkerManager() { return markerManager; }
    public Messenger getMessenger() { return messenger; }
    public DisplayManager getDisplayManager() { return displayManager; }
    public BanManager getBanManager() { return banManager; }
    public VanishManager getVanishManager() { return vanishManager; }
    public SocialSpyManager getSocialSpyManager() { return socialSpyManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public ModrinthUpdateChecker getUpdateChecker() { return updateChecker; }

    public boolean toggleGodMode(UUID playerUUID) {
        return godModePlayers.compute(playerUUID, (k, v) -> v == null || !v);
    }

    public boolean isInGodMode(UUID playerUUID) {
        return godModePlayers.getOrDefault(playerUUID, false);
    }

    public void setGodMode(UUID playerUUID, boolean enabled) {
        if (enabled) {
            godModePlayers.put(playerUUID, true);
        } else {
            godModePlayers.remove(playerUUID);
        }
    }
}