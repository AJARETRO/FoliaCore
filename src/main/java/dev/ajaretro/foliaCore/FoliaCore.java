package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.*;
import dev.ajaretro.foliaCore.managers.*;
import dev.ajaretro.foliaCore.utils.Messenger;
import dev.ajaretro.foliaCore.economy.FoliaEconomy;
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

            // 2. FIX THIS LINE (Remove "Metrics." before SimplePie)
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

        instance = this;

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

        getLogger().info("FoliaCore shutdown sequence completed.");
    }

    private void loadSubsystems() {
        chatManager.load();
        teleportManager.load();
        teamManager.load();
        kitManager.load();
        warpManager.load();
        markerManager.load();
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
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("block").setExecutor(new BlockCommand(this));
        getCommand("unblock").setExecutor(new UnblockCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
        getCommand("chat").setExecutor(new ChatCommand(this));

        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpahere").setExecutor(new TpaHereCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));

        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("createkit").setExecutor(new CreateKitCommand(this));
        getCommand("delkit").setExecutor(new DeleteKitCommand(this));
        getCommand("marker").setExecutor(new MarkerCommand(this));
        getCommand("gps").setExecutor(new GpsCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("warps").setExecutor(new WarpsCommand(this));

        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("nick").setExecutor(new NickCommand(this));
        getCommand("realname").setExecutor(new RealNameCommand(this));
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