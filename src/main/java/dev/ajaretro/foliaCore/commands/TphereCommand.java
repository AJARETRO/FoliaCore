package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TphereCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TphereCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.tphere")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /tphere <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is not online.");
            return true;
        }

        if (target.equals(player)) {
            plugin.getMessenger().sendError(player, "You cannot teleport yourself to yourself.");
            return true;
        }

        plugin.getTeleportManager().setLastLocation(target.getUniqueId(), target.getLocation());
        plugin.getTeleportManager().startTeleport(target, player.getLocation(), "You have been teleported to " + ChatColor.GOLD + player.getName());
        plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " has been teleported to you.");

        return true;
    }
}
