package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

/**
 * Toggles social spy to see all private messages.
 */
public class SocialSpyCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public SocialSpyCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.socialspy")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        plugin.getVanishManager().toggleSocialSpy(player.getUniqueId());

        if (plugin.getVanishManager().hasSocialSpy(player.getUniqueId())) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Social Spy " + ChatColor.GREEN + "enabled.");
        } else {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Social Spy " + ChatColor.RED + "disabled.");
        }

        return true;
    }
}
