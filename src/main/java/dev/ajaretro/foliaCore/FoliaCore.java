package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.*;
import dev.ajaretro.foliaCore.managers.*;
import dev.ajaretro.foliaCore.utils.Messenger;
import dev.ajaretro.foliaCore.economy.FoliaEconomy;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.charts.SimplePie;

public final class FoliaCore extends JavaPlugin {

    private static FoliaCore instance;

    private ChatManager chatManager;
    private TeleportManager teleportManager;
    private TeamManager teamManager;
    private KitManager kitManager;
    private WarpManager warpManager;
    private MarkerManager markerManager;
    private EconomyManager economyManager;

    private Messenger messenger;

    @Override
    public void onEnable() {
        instance = this;

        this.messenger = new Messenger("&l[ &4AJA_RETRO/&3FoliaCore&f ]");

        int pluginId = 28430;
        try {
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new SimplePie("chart_id", () -> "My Value"));
            getLogger().info("bStats metrics enabled.");
        } catch (Exception e) {
            getLogger().warning("Failed to enable bStats metrics.");
        }

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            getLogger().severe("This plugin requires Folia! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getServicesManager().register(
                    Economy.class,
                    new FoliaEconomy(),
                    this,
                    ServicePriority.Highest
            );
            Bukkit.getConsoleSender().sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize("&l&4[FoliaCore] &aVault found! Economy Provider registered.")
            );
        } else {
            getLogger().warning("Vault NOT found! Economy features will be disabled.");
        }

        this.chatManager = new ChatManager(this);
        this.teleportManager = new TeleportManager(this);
        this.teamManager = new TeamManager(this);
        this.kitManager = new KitManager(this);
        this.warpManager = new WarpManager(this);
        this.markerManager = new MarkerManager(this);
        this.economyManager = new EconomyManager(this);

        loadSubsystems();
        registerListeners();

        registerCommands();

        Bukkit.getConsoleSender().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize("&l&4[FoliaCore] &aPlugin initialized successfully! &7(Backend: REGIONIZED)")
        );
    }

    @Override
    public void onDisable() {
        if (chatManager != null) chatManager.saveData();
        if (teleportManager != null) teleportManager.saveData();
        if (teamManager != null) teamManager.saveData();
        if (kitManager != null) kitManager.saveData();
        if (warpManager != null) warpManager.saveData();
        if (markerManager != null) markerManager.saveData();
        if (economyManager != null) economyManager.saveData();

        getLogger().info("FoliaCore shutdown sequence completed.");
    }

    private void loadSubsystems() {
        if (chatManager != null) chatManager.load();
        if (teleportManager != null) teleportManager.load();
        if (teamManager != null) teamManager.load();
        if (kitManager != null) kitManager.load();
        if (warpManager != null) warpManager.load();
        if (markerManager != null) markerManager.load();
        if (economyManager != null) economyManager.load();
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new MailListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new KitGUIListener(this), this);
        pm.registerEvents(new PlayerGpsListener(this), this);
        pm.registerEvents(new ConnectionListener(this), this);
    }

    private void registerCommands() {
        registerCommand("mute",new MuteCommand(this));
        registerCommand("unmute",new UnmuteCommand(this));
        registerCommand("msg",new MsgCommand(this));
        registerCommand("reply",new ReplyCommand(this));
        registerCommand("block",new BlockCommand(this));
        registerCommand("unblock",new UnblockCommand(this));
        registerCommand("mail",new MailCommand(this));
        registerCommand("chat",new ChatCommand(this));

        registerCommand("sethome",new SetHomeCommand(this));
        registerCommand("home",new HomeCommand(this));
        registerCommand("delhome",new DelHomeCommand(this));
        registerCommand("homes",new HomesCommand(this));
        registerCommand("tpa",new TpaCommand(this));
        registerCommand("tpahere",new TpaHereCommand(this));
        registerCommand("tpaccept",new TpAcceptCommand(this));
        registerCommand("tpdeny",new TpDenyCommand(this));
        registerCommand("setspawn",new SetSpawnCommand(this));
        registerCommand("spawn",new SpawnCommand(this));

        registerCommand("team",new TeamCommand(this));
        registerCommand("kit",new KitCommand(this));
        registerCommand("createkit",new CreateKitCommand(this));
        registerCommand("delkit",new DeleteKitCommand(this));
        registerCommand("marker",new MarkerCommand(this));
        registerCommand("gps",new GpsCommand(this));
        registerCommand("setwarp",new SetWarpCommand(this));
        registerCommand("delwarp",new DelWarpCommand(this));
        registerCommand("warp",new WarpCommand(this));
        registerCommand("warps",new WarpsCommand(this));

        registerCommand("balance", new BalanceCommand(this));
        registerCommand("pay",new PayCommand(this));
        registerCommand("eco",new EcoCommand(this));

        registerCommand("nick",new NickCommand(this));
        registerCommand("realname",new RealNameCommand(this));
    }

    public static FoliaCore getInstance() { return instance; }
    public ChatManager getChatManager() { return chatManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public KitManager getKitManager() { return kitManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public MarkerManager getMarkerManager() { return markerManager; }
    public Messenger getMessenger() { return messenger; }
    public EconomyManager getEconomyManager() { return economyManager; }
}