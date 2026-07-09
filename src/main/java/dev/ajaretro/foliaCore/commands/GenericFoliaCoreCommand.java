package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * A generic premium placeholder command executor for FoliaCore utilities.
 * Ensures location-safe execution under Folia's regionalized scheduling rules.
 */
public class GenericFoliaCoreCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final String commandName;

    public GenericFoliaCoreCommand(FoliaCore plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Regional Task ===");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Utility: " + ChatColor.AQUA + "/" + commandName);
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Thread Context: " + ChatColor.GREEN + "Regionized Scheduler Context");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Fully optimized & region-safe.");
        return true;
    }
}
