package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TrashCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TrashCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.trash")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        Inventory trash = Bukkit.createInventory(player, 54, "Trash Bin");
        player.openInventory(trash);
        plugin.getMessenger().sendMessage(player, "&aTrash opened. &7Anything left inside will be deleted when closed.");
        return true;
    }
}