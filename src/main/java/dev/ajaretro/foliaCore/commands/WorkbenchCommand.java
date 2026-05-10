package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class WorkbenchCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public WorkbenchCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.workbench")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        player.openWorkbench(null, true);
        plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Opened crafting table.");

        return true;
    }
}
