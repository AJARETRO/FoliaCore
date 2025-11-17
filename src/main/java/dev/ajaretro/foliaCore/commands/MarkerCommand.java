package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Marker;
import dev.ajaretro.foliaCore.managers.MarkerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MarkerCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final MarkerManager markerManager;
    private static final Pattern MARKER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public MarkerCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.markerManager = plugin.getMarkerManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "set":
                handleSet(player, args);
                break;
            case "del":
            case "delete":
                handleDelete(player, args);
                break;
            case "list":
                handleList(player);
                break;
            default:
                sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "--- FoliaCore Marker Help ---");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/marker set <name>" + ChatColor.WHITE + " - Save your current location.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/marker del <name>" + ChatColor.WHITE + " - Delete a saved marker.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/marker list" + ChatColor.WHITE + " - List all your markers.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/gps <name>" + ChatColor.WHITE + " - Start navigation to a marker.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/gps off" + ChatColor.WHITE + " - Stop navigation.");
    }

    private void handleSet(Player player, String[] args) {
        if (!player.hasPermission("foliacore.marker.set")) {
            plugin.getMessenger().sendError(player, "You do not have permission to set markers.");
            return;
        }
        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /marker set <name>");
            return;
        }
        String name = args[1];
        if (!MARKER_NAME_PATTERN.matcher(name).matches()) {
            plugin.getMessenger().sendError(player, "Marker name must be 3-20 characters (letters, numbers, underscore).");
            return;
        }

        markerManager.setMarker(player.getUniqueId(), name, player.getLocation());
        plugin.getMessenger().sendSuccess(player, "Marker '" + ChatColor.GOLD + name + ChatColor.GREEN + "' has been saved!");
    }

    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("foliacore.marker.delete")) {
            plugin.getMessenger().sendError(player, "You do not have permission to delete markers.");
            return;
        }
        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /marker del <name>");
            return;
        }
        String name = args[1];
        if (markerManager.getMarker(player.getUniqueId(), name) == null) {
            plugin.getMessenger().sendError(player, "A marker with that name was not found.");
            return;
        }

        markerManager.deleteMarker(player.getUniqueId(), name);
        plugin.getMessenger().sendSuccess(player, "Marker '" + ChatColor.GOLD + name + ChatColor.GREEN + "' has been deleted.");
    }

    private void handleList(Player player) {
        if (!player.hasPermission("foliacore.marker.list")) {
            plugin.getMessenger().sendError(player, "You do not have permission to list markers.");
            return;
        }
        Map<String, Marker> markers = markerManager.getMarkers(player.getUniqueId());
        if (markers.isEmpty()) {
            plugin.getMessenger().sendMessage(player, "You have no markers set.");
            return;
        }
        String list = markers.values().stream()
                .map(Marker::name)
                .sorted()
                .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.WHITE));

        plugin.getMessenger().sendMessage(player, "Your Markers: " + ChatColor.WHITE + list);
    }
}