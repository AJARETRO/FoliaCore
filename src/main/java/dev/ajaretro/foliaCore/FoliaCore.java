package dev.ajaretro.foliaCore;

import dev.ajaretro.foliaCore.commands.*;
import dev.ajaretro.foliaCore.listeners.ChatListener;
import dev.ajaretro.foliaCore.listeners.MailListener;
import dev.ajaretro.foliaCore.managers.ChatManager;
import dev.ajaretro.foliaCore.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaCore extends JavaPlugin {

    private static FoliaCore instance;
    private ChatManager chatManager;
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

        this.messenger = new Messenger("&l[ &4AJA_RETRO/&3FoliaCore&f ]");
    }

    private void saveManagers() {
        chatManager.saveData();
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MailListener(this), this);
    }

    private void loadCommands() {
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("block").setExecutor(new BlockCommand(this));
        getCommand("unblock").setExecutor(new UnblockCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
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

    public Messenger getMessenger() {
        return messenger;
    }
}