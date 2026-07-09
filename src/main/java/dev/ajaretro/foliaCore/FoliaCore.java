package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.tasks.EntityCleanupTask;
import dev.ajaretro.foliaCore.tasks.AutoBroadcaster;
import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.*;
import dev.ajaretro.foliaCore.managers.*;
import dev.ajaretro.foliaCore.utils.Messenger;
import dev.ajaretro.foliaCore.storage.StorageManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.charts.SimplePie;

import java.util.List;
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
 * @version v5.8 BloodyMary
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

    // Storage and New systems
    private StorageManager storageManager;
    private EconomyManager economyManager;
    private JailManager jailManager;
    private DiscordManager discordManager;
    private IgnoreManager ignoreManager;
    private PowertoolManager powertoolManager;

    // God mode state
    private final ConcurrentHashMap<UUID, Boolean> godModePlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, org.bukkit.command.CommandExecutor> commandExecutors = new ConcurrentHashMap<>();

    // Tasks
    private EntityCleanupTask entityCleanupTask;
    private AutoBroadcaster autoBroadcaster;
    private ModrinthUpdateChecker updateChecker;

    private final ThreadLocal<Integer> commandActionDepth = ThreadLocal.withInitial(() -> 0);

    @Override
    public void onEnable() {
        instance = this;

        // Load config FIRST
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.storageManager = new StorageManager(this);
        this.storageManager.init();

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

        this.economyManager = new EconomyManager(this);
        this.jailManager = new JailManager(this);
        this.discordManager = new DiscordManager(this);
        this.ignoreManager = new IgnoreManager(this);
        this.powertoolManager = new PowertoolManager(this);

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
            
            this.autoBroadcaster = new AutoBroadcaster(this);
            this.autoBroadcaster.load();
            this.autoBroadcaster.start();
            
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

        if (configManager.isEconomyEnabled() && economyManager != null) economyManager.saveData();
        if (configManager.isJailsEnabled() && jailManager != null) jailManager.saveData();
        if (configManager.isDiscordEnabled() && discordManager != null) discordManager.shutdown();
        if (ignoreManager != null) ignoreManager.saveData();
        if (powertoolManager != null) powertoolManager.saveData();
        if (storageManager != null) storageManager.shutdown();

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

        if (configManager.isEconomyEnabled() && economyManager != null) economyManager.load();
        if (configManager.isJailsEnabled() && jailManager != null) jailManager.load();
        if (configManager.isDiscordEnabled() && discordManager != null) discordManager.load();
        if (ignoreManager != null) ignoreManager.load();
        if (powertoolManager != null) powertoolManager.load();
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
        pm.registerEvents(new PowertoolListener(this), this);
        pm.registerEvents(new JailListener(this), this);
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

        registerCommandSafe("foliacore", new FoliaCoreCommand(this));
        registerCommandSafe("scoreboard", new ScoreboardToggleCommand(this));
        registerCommandSafe("sidebar", new ScoreboardToggleCommand(this));

        if (configManager.isEconomyEnabled()) {
            registerCommandSafe("balance", new BalanceCommand(this));
            registerCommandSafe("bal", new BalanceCommand(this));
            registerCommandSafe("pay", new PayCommand(this));
            registerCommandSafe("eco", new EcoCommand(this));
            registerCommandSafe("sell", new SellCommand(this));
            registerCommandSafe("worth", new WorthCommand(this));
        }

        if (configManager.isJailsEnabled()) {
            registerCommandSafe("jail", new JailCommand(this));
            registerCommandSafe("unjail", new UnjailCommand(this));
            registerCommandSafe("setjail", new SetJailCommand(this));
            registerCommandSafe("deljail", new DelJailCommand(this));
            registerCommandSafe("jails", new JailsCommand(this));
        }

        if (configManager.isDiscordEnabled()) {
            registerCommandSafe("discord", new DiscordCommand(this));
            registerCommandSafe("link", new LinkCommand(this));
            registerCommandSafe("unlink", new UnlinkCommand(this));
            registerCommandSafe("discordbroadcast", new DiscordBroadcastCommand(this));
        }

        registerCommandSafe("ignore", new IgnoreCommand(this));
        registerCommandSafe("unignore", new UnignoreCommand(this));
        registerCommandSafe("ignorelist", new IgnoreListCommand(this));
        registerCommandSafe("powertool", new PowertoolCommand(this));
        registerCommandSafe("pt", new PowertoolCommand(this));
        registerCommandSafe("ptime", new PTimeCommand(this));
        registerCommandSafe("pweather", new PWeatherCommand(this));
        registerCommandSafe("rules", new RulesCommand(this));

        // Thor / Explosives / Projectiles
        ThorExplosivesCommand thorCmd = new ThorExplosivesCommand(this);
        registerCommandSafe("antioch", thorCmd);
        registerCommandSafe("beezooka", thorCmd);
        registerCommandSafe("fireball", thorCmd);
        registerCommandSafe("lightning", thorCmd);
        registerCommandSafe("nuke", thorCmd);
        registerCommandSafe("spawnmob", thorCmd);
        registerCommandSafe("kittycannon", thorCmd);
        registerCommandSafe("tree", thorCmd);
        registerCommandSafe("remove", thorCmd);

        // Economy Utilities
        EconomyUtilsCommand ecoUtils = new EconomyUtilsCommand(this);
        registerCommandSafe("balancetop", ecoUtils);
        registerCommandSafe("paytoggle", ecoUtils);
        registerCommandSafe("payconfirmtoggle", ecoUtils);
        registerCommandSafe("setworth", ecoUtils);

        // Ignore & Reply Filters
        IgnoreReplyFiltersCommand ignoreReplyCmd = new IgnoreReplyFiltersCommand(this);
        registerCommandSafe("msgtoggle", ignoreReplyCmd);
        registerCommandSafe("rtoggle", ignoreReplyCmd);

        // Jail Secondary
        JailSecondaryCommand jailSecCmd = new JailSecondaryCommand(this);
        registerCommandSafe("jailedplayers", jailSecCmd);

        // Client & Server Environment
        ClientServerCommand clientServerCmd = new ClientServerCommand(this);
        registerCommandSafe("afk", clientServerCmd);
        registerCommandSafe("compass", clientServerCmd);
        registerCommandSafe("enchant", clientServerCmd);
        registerCommandSafe("exp", clientServerCmd);
        registerCommandSafe("ext", clientServerCmd);
        registerCommandSafe("firework", clientServerCmd);
        registerCommandSafe("jump", clientServerCmd);
        registerCommandSafe("kickall", clientServerCmd);
        registerCommandSafe("list", clientServerCmd);
        registerCommandSafe("me", clientServerCmd);
        registerCommandSafe("more", clientServerCmd);
        registerCommandSafe("motd", clientServerCmd);
        registerCommandSafe("near", clientServerCmd);
        registerCommandSafe("seen", clientServerCmd);
        registerCommandSafe("skull", clientServerCmd);
        registerCommandSafe("speed", clientServerCmd);
        registerCommandSafe("sudo", clientServerCmd);
        registerCommandSafe("suicide", clientServerCmd);
        registerCommandSafe("editsign", clientServerCmd);
        registerCommandSafe("thunder", clientServerCmd);
        registerCommandSafe("potion", clientServerCmd);
        registerCommandSafe("recipe", clientServerCmd);
        registerCommandSafe("playtime", clientServerCmd);

        // Teleport Utilities
        TeleportUtilsCommand tpUtilsCmd = new TeleportUtilsCommand(this);
        registerCommandSafe("tpoffline", tpUtilsCmd);
        registerCommandSafe("settpr", tpUtilsCmd);
        registerCommandSafe("renamehome", tpUtilsCmd);
        registerCommandSafe("tpall", tpUtilsCmd);
        registerCommandSafe("tpauto", tpUtilsCmd);
        registerCommandSafe("tpacancel", tpUtilsCmd);
        registerCommandSafe("tpo", tpUtilsCmd);
        registerCommandSafe("tpohere", tpUtilsCmd);
        registerCommandSafe("tppos", tpUtilsCmd);
        registerCommandSafe("tpr", tpUtilsCmd);
        registerCommandSafe("tptoggle", tpUtilsCmd);

        // Worktable GUIs
        InventoryGuisCommand invGuisCmd = new InventoryGuisCommand(this);
        registerCommandSafe("anvil", invGuisCmd);
        registerCommandSafe("grindstone", invGuisCmd);
        registerCommandSafe("loom", invGuisCmd);
        registerCommandSafe("smithingtable", invGuisCmd);
        registerCommandSafe("stonecutter", invGuisCmd);
        registerCommandSafe("cartographytable", invGuisCmd);

        // Kit Info
        KitInfoCommand kitInfoCmd = new KitInfoCommand(this);
        registerCommandSafe("showkit", kitInfoCmd);

        // Chat Shout Mode
        ChatShoutCommand shoutCmd = new ChatShoutCommand(this);
        registerCommandSafe("toggleshout", shoutCmd);

        // IP Ban Commands
        IpBanCommand ipBanCmd = new IpBanCommand(this);
        registerCommandSafe("banip", ipBanCmd);
        registerCommandSafe("tempbanip", ipBanCmd);
        registerCommandSafe("unbanip", ipBanCmd);

        // Miscellaneous Admin Commands (instantiates and registers event listener inside constructor)
        MiscAdminCommand miscAdminCmd = new MiscAdminCommand(this);
        registerCommandSafe("unlimited", miscAdminCmd);
        registerCommandSafe("rest", miscAdminCmd);
        registerCommandSafe("warpinfo", miscAdminCmd);

        // Command Proxies (FC & CMI)
        FcCommandExecutor fcExecutor = new FcCommandExecutor(this);
        registerCommandSafe("fc", fcExecutor);
        registerCommandSafe("cmi", fcExecutor);
    }

    private void registerCommandSafe(String name, org.bukkit.command.CommandExecutor executor) {
        commandExecutors.put(name.toLowerCase(java.util.Locale.ROOT), executor);
        // Modern Paper API: use registerCommand() with BasicCommand wrapper
        try {
            final org.bukkit.command.Command bridgeCommand = new org.bukkit.command.Command(name) {
                @Override
                public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                    return executeRegisteredCommand(name, executor, sender, this, commandLabel, args);
                }
            };
            var basicCmd = new io.papermc.paper.command.brigadier.BasicCommand() {
                @Override
                public void execute(io.papermc.paper.command.brigadier.CommandSourceStack commandSourceStack, 
                                   String[] args) {
                    executeRegisteredCommand(name, executor, commandSourceStack.getSender(), bridgeCommand, name, args);
                }
            };
            this.registerCommand(name, basicCmd);
        } catch (Exception e) {
            getLogger().warning("Failed to register command '" + name + "': " + e.getMessage());
        }
    }

    public org.bukkit.command.CommandExecutor getCommandExecutor(String name) {
        return commandExecutors.get(name.toLowerCase(java.util.Locale.ROOT));
    }

    public boolean executeCommandProxy(String commandName, org.bukkit.command.CommandExecutor executor, CommandSender sender, String[] args) {
        org.bukkit.command.Command dummyCommand = new org.bukkit.command.Command(commandName) {
            @Override
            public boolean execute(CommandSender s, String label, String[] subArgs) {
                return false;
            }
        };
        return executeRegisteredCommand(commandName, executor, sender, dummyCommand, commandName, args);
    }

    private boolean executeRegisteredCommand(String commandName,
                                             org.bukkit.command.CommandExecutor executor,
                                             CommandSender sender,
                                             org.bukkit.command.Command command,
                                             String label,
                                             String[] args) {
        if (!configManager.isCommandEnabled(commandName)) {
            messenger.sendError(sender, "This command is disabled by the server.");
            return true;
        }

        boolean handled;
        try {
            handled = executor.onCommand(sender, command, label, args);
        } catch (Exception exception) {
            getLogger().severe("Command '/" + commandName + "' failed: " + exception.getMessage());
            exception.printStackTrace();
            messenger.sendError(sender, "An error occurred while running this command.");
            return true;
        }

        if (handled && commandActionDepth.get() == 0) {
            runCommandActions(commandName, sender, args);
        }

        return handled;
    }

    private void runCommandActions(String commandName, CommandSender sender, String[] args) {
        List<String> playerActions = configManager.getCommandPlayerDone(commandName);
        List<String> consoleActions = configManager.getCommandConsoleDone(commandName);

        if (sender instanceof Player player && !playerActions.isEmpty()) {
            player.getScheduler().run(this, task -> {
                executePlayerActions(player, commandName, playerActions, args);
                if (!consoleActions.isEmpty()) {
                    Bukkit.getGlobalRegionScheduler().run(this, scheduledTask -> executeConsoleActions(sender, commandName, consoleActions, args));
                }
            }, null);
            return;
        }

        if (!consoleActions.isEmpty()) {
            Bukkit.getGlobalRegionScheduler().run(this, task -> executeConsoleActions(sender, commandName, consoleActions, args));
        }
    }

    private void executePlayerActions(Player player, String commandName, List<String> actions, String[] args) {
        runActionBatch(() -> {
            for (String action : actions) {
                dispatchActionCommand(player, commandName, action, args, true);
            }
        });
    }

    private void executeConsoleActions(CommandSender sender, String commandName, List<String> actions, String[] args) {
        runActionBatch(() -> {
            for (String action : actions) {
                dispatchActionCommand(sender, commandName, action, args, false);
            }
        });
    }

    private void dispatchActionCommand(CommandSender sender,
                                       String commandName,
                                       String action,
                                       String[] args,
                                       boolean runAsPlayer) {
        String prepared = prepareActionCommand(action, sender, commandName, args);
        if (prepared.isBlank()) {
            return;
        }

        if (runAsPlayer && sender instanceof Player player) {
            player.performCommand(prepared);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prepared);
    }

    private void runActionBatch(Runnable runnable) {
        commandActionDepth.set(commandActionDepth.get() + 1);
        try {
            runnable.run();
        } finally {
            commandActionDepth.set(Math.max(0, commandActionDepth.get() - 1));
        }
    }

    private String prepareActionCommand(String action,
                                        CommandSender sender,
                                        String commandName,
                                        String[] args) {
        String value = action == null ? "" : action.trim();
        if (value.startsWith("/")) {
            value = value.substring(1);
        }

        Player player = sender instanceof Player ? (Player) sender : null;
        String joinedArgs = args == null || args.length == 0 ? "" : String.join(" ", args);
        value = value.replace("%command%", commandName)
                .replace("%label%", commandName)
                .replace("%args%", joinedArgs)
                .replace("%sender%", sender.getName());

        if (player != null) {
            value = value.replace("%player%", player.getName())
                    .replace("%player_name%", player.getName())
                    .replace("%player_uuid%", player.getUniqueId().toString());
        }

        return value;
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
            LegacyComponentSerializer.legacyAmpersand().deserialize("&l&6   ✦ &b&lFOLIACORE &3v5.8-BloodyMary&b&l BLOODYMARY &6✦")
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
    public StorageManager getStorageManager() { return storageManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public JailManager getJailManager() { return jailManager; }
    public DiscordManager getDiscordManager() { return discordManager; }
    public IgnoreManager getIgnoreManager() { return ignoreManager; }
    public PowertoolManager getPowertoolManager() { return powertoolManager; }

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