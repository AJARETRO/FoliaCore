package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class BackCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public BackCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.back")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        Location lastLocation = plugin.getTeleportManager().getLastLocation(player.getUniqueId());
        if (lastLocation == null) {
            plugin.getMessenger().sendError(player, "You have no previous location to return to.");
            return true;
        }

        // Save current location as new last location
        plugin.getTeleportManager().setLastLocation(player.getUniqueId(), player.getLocation());
        
        // Teleport to last location
        plugin.getTeleportManager().startTeleport(player, lastLocation, ChatColor.GREEN + "You have returned to your previous location.");

        return true;
    }
}
