package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public TpAcceptCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.tpaccept")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        TeleportManager.TeleportRequest request = tm.getTpaRequest(player.getUniqueId());
        if (request == null) {
            plugin.getMessenger().sendError(player, "You have no pending teleport requests.");
            return true;
        }

        tm.removeTpaRequest(player.getUniqueId());

        Player requester = Bukkit.getPlayer(request.requester());
        if (requester == null || !requester.isOnline()) {
            plugin.getMessenger().sendError(player, "The other player is no longer online.");
            return true;
        }

        plugin.getMessenger().sendSuccess(player, "Teleport request accepted.");
        requester.getScheduler().run(plugin, (task) -> {
            plugin.getMessenger().sendSuccess(requester, player.getName() + " accepted your teleport request.");
        }, null);

        if (request.type() == TeleportManager.TpaType.TPA) {
            tm.startTeleport(requester, player.getLocation(), "Teleported to " + ChatColor.GOLD + player.getName());
        } else {
            tm.startTeleport(player, requester.getLocation(), "Teleported " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " to you.");
        }

        return true;
    }
}