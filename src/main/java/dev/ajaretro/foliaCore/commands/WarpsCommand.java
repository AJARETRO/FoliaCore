package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class WarpsCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public WarpsCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.warps.list")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to list warps.");
            return true;
        }

        boolean isAdmin = sender.hasPermission("foliacore.warp.all");

        String warpsList = plugin.getWarpManager().getAllWarps().stream()
                .map(Warp::name)
                .filter(name -> isAdmin || sender.hasPermission("foliacore.warp." + name.toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.WHITE));

        if (warpsList.isEmpty()) {
            plugin.getMessenger().sendMessage(sender, "There are no warps available for you.");
        } else {
            plugin.getMessenger().sendMessage(sender, "Available Warps: " + ChatColor.WHITE + warpsList);
        }
        return true;
    }
}