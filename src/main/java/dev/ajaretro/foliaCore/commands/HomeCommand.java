package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Home;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeleportManager tm;
    private static final long TELEPORT_DELAY_TICKS = 60L;

    public HomeCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.home")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (tm.isTeleporting(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You are already teleporting!");
            return true;
        }

        String homeName = (args.length == 0) ? "home" : args[0];

        Home home = tm.getHome(player.getUniqueId(), homeName);
        if (home == null) {
            plugin.getMessenger().sendError(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.RED + "' not found.");
            return true;
        }

        Location location = home.toLocation();
        if (location == null) {
            plugin.getMessenger().sendError(player, "The world for this home is not loaded or does not exist!");
            return true;
        }

        plugin.getMessenger().sendMessage(player, "Teleporting to '" + ChatColor.GOLD + homeName + ChatColor.WHITE + "'. Don't move for 3 seconds...");

        var task = player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
            tm.completeTeleport(player.getUniqueId());
            player.teleportAsync(location);
            plugin.getMessenger().sendSuccess(player, "Teleported to '" + ChatColor.GOLD + homeName + ChatColor.GREEN + "'.");
        }, null, TELEPORT_DELAY_TICKS);

        tm.startTeleport(player, task);
        return true;
    }
}