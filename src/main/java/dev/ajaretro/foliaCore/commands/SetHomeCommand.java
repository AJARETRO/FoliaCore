package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class SetHomeCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeleportManager tm;
    private static final Pattern HOME_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public SetHomeCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.sethome")) {
            plugin.getMessenger().sendError(player, "You do not have permission to set a home.");
            return true;
        }

        String homeName;
        int maxHomes = tm.getMaxHomes(player);

        if (args.length == 0) {
            if (maxHomes == 1) {
                homeName = "home";
            } else {
                plugin.getMessenger().sendError(player, "You have access to multiple homes. Please specify a name: /sethome <name>");
                return true;
            }
        } else {
            homeName = args[0];
        }

        if (!HOME_NAME_PATTERN.matcher(homeName).matches()) {
            plugin.getMessenger().sendError(player, "Home name must be 3-20 characters and only contain letters, numbers, and underscores.");
            return true;
        }

        boolean isOverwrite = tm.getHome(player.getUniqueId(), homeName) != null;
        int currentHomes = tm.getHomeCount(player.getUniqueId());

        if (!isOverwrite && currentHomes >= maxHomes) {
            plugin.getMessenger().sendError(player, "You have reached your maximum home limit of " + maxHomes + ".");
            return true;
        }

        tm.setHome(player.getUniqueId(), homeName, player.getLocation());
        if (isOverwrite) {
            plugin.getMessenger().sendSuccess(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.GREEN + "' has been updated.");
        } else {
            plugin.getMessenger().sendSuccess(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.GREEN + "' set! (" + (currentHomes + 1) + "/" + (maxHomes == Integer.MAX_VALUE ? "Unlimited" : maxHomes) + ")");
        }

        return true;
    }
}