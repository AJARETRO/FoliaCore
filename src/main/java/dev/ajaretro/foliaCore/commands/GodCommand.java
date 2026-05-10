package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class GodCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public GodCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /god [player]");
                return true;
            }
            player = (Player) sender;
            if (!player.hasPermission("foliacore.god")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
        } else {
            if (!sender.hasPermission("foliacore.god.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
            if (player == null || !player.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }
        }

        boolean godEnabled = plugin.toggleGodMode(player.getUniqueId());
        
        if (godEnabled) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "God mode is now " + ChatColor.GREEN + "enabled.");
            if (!player.equals(sender)) {
                plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has god mode enabled.");
            }
        } else {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "God mode is now " + ChatColor.RED + "disabled.");
            if (!player.equals(sender)) {
                plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has god mode disabled.");
            }
        }

        return true;
    }
}
