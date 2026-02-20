package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DelWarpCommand implements BasicCommand {

    private final FoliaCore plugin;

    public DelWarpCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        
        if (!sender.hasPermission("foliacore.delwarp")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /delwarp <name>");
            return;
        }

        String warpName = args[0];
        if (!plugin.getWarpManager().isWarp(warpName)) {
            plugin.getMessenger().sendError(sender, "Warp '" + ChatColor.GOLD + warpName + ChatColor.RED + "' not found.");
            return;
        }

        plugin.getWarpManager().deleteWarp(warpName);
        plugin.getMessenger().sendSuccess(sender, "Warp '" + ChatColor.GOLD + warpName + ChatColor.GREEN + "' has been deleted.");
    }
}