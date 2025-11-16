package dev.ajaretro.foliaCore.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger {

    private final String prefix;

    public Messenger(String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix) + " " + ChatColor.RESET;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(prefix + ChatColor.WHITE + message);
    }

    public void sendError(CommandSender sender, String errorMessage) {
        sender.sendMessage(prefix + ChatColor.RED + errorMessage);
    }

    public void sendSuccess(CommandSender sender, String successMessage) {
        sender.sendMessage(prefix + ChatColor.GREEN + successMessage);
    }

    public String getPrefix() {
        return prefix;
    }
}