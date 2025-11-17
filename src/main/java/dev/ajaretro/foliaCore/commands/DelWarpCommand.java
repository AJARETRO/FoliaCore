package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DelWarpCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public DelWarpCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.delwarp")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /delwarp <name>");
            return true;
        }

        String warpName = args[0];
        if (!plugin.getWarpManager().isWarp(warpName)) {
            plugin.getMessenger().sendError(sender, "Warp '" + ChatColor.GOLD + warpName + ChatColor.RED + "' not found.");
            return true;
        }

        plugin.getWarpManager().deleteWarp(warpName);
        plugin.getMessenger().sendSuccess(sender, "Warp '" + ChatColor.GOLD + warpName + ChatColor.GREEN + "' has been deleted.");
        return true;
    }
}