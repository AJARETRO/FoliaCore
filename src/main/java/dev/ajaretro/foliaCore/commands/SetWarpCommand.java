package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class SetWarpCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private static final Pattern WARP_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public SetWarpCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.setwarp")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /setwarp <name>");
            return true;
        }

        String warpName = args[0];
        if (!WARP_NAME_PATTERN.matcher(warpName).matches()) {
            plugin.getMessenger().sendError(player, "Warp name must be 3-20 characters (letters, numbers, underscore).");
            return true;
        }

        if (plugin.getWarpManager().isWarp(warpName)) {
            plugin.getMessenger().sendError(player, "A warp with that name already exists. Use /delwarp first.");
            return true;
        }

        plugin.getWarpManager().createWarp(warpName, player.getLocation());
        plugin.getMessenger().sendSuccess(player, "Warp '" + ChatColor.GOLD + warpName + ChatColor.GREEN + "' has been set!");
        return true;
    }
}