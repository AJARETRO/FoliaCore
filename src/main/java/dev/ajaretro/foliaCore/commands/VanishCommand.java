package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

/**
 * Toggles vanish mode for staff members.
 * Hides the player from non-staff players.
 */
public class VanishCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public VanishCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.vanish")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        plugin.getVanishManager().toggleVanish(player.getUniqueId());

        if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "You are now " + ChatColor.RED + "vanished.");
        } else {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "You are now " + ChatColor.GREEN + "visible.");
        }

        return true;
    }
}
