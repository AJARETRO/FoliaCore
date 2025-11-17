package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.ChatListener;
import dev.ajaretro.foliaCore.listeners.MailListener;
import dev.ajaretro.foliaCore.listeners.PlayerMoveListener;
import dev.ajaretro.foliaCore.managers.ChatManager;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import dev.ajaretro.foliaCore.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaCore extends JavaPlugin {

    private static FoliaCore instance;
    private ChatManager chatManager;
    private TeleportManager teleportManager;
    private Messenger messenger;

    @Override
    public void onEnable() {
        instance = this;

        loadManagers();
        loadListeners();
        loadCommands();

        sendBloodRedMessage("=========================================");
        sendBloodRedMessage(" FoliaCore by AJARETRO is ALIVE! ");
        sendBloodRedMessage(" We are running on Folia!");
        sendBloodRedMessage("=========================================");
    }

    @Override
    public void onDisable() {
        saveManagers();
        sendBloodRedMessage("FoliaCore by AJARETRO is shutting down...");
    }

    private void loadManagers() {
        chatManager = new ChatManager(this);
        chatManager.load();

        teleportManager = new TeleportManager(this);
        teleportManager.load();

        this.messenger = new Messenger("&l[ &4AJA_RETRO/&3FoliaCore&f ]");
    }

    private void saveManagers() {
        chatManager.saveData();
        teleportManager.saveData();
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MailListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    private void loadCommands() {
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
    }

    private void sendBloodRedMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + message);
    }

    public static FoliaCore getInstance() {
        return instance;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public Messenger getMessenger() {
        return messenger;
    }
}