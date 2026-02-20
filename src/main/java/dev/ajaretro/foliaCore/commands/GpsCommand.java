package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Marker;
import dev.ajaretro.foliaCore.managers.MarkerManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GpsCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final MarkerManager markerManager;

    public GpsCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.markerManager = plugin.getMarkerManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!player.hasPermission("foliacore.gps")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use the GPS.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /gps <marker_name> | /gps off");
            return;
        }

        String arg = args[0].toLowerCase();

        if (arg.equals("off") || arg.equals("stop")) {
            if (markerManager.isGpsActive(player.getUniqueId())) {
                markerManager.stopGps(player);
            } else {
                plugin.getMessenger().sendError(player, "GPS is not active.");
            }
            return;
        }

        Marker marker = markerManager.getMarker(player.getUniqueId(), arg);
        if (marker == null) {
            plugin.getMessenger().sendError(player, "Marker '" + ChatColor.GOLD + args[0] + ChatColor.RED + "' not found.");
            return;
        }

        markerManager.startGps(player, marker);
        return;
    }
}