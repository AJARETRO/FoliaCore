package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class TimeCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TimeCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.time")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /time <day|night|number>");
            return true;
        }

        long time;
        String timeStr = args[0].toLowerCase();

        switch (timeStr) {
            case "day":
                time = 0L;
                break;
            case "night":
                time = 13000L;
                break;
            default:
                try {
                    time = Long.parseLong(timeStr) % 24000L;
                } catch (NumberFormatException e) {
                    plugin.getMessenger().sendError(sender, "Invalid time. Use 'day', 'night', or a number (0-24000).");
                    return true;
                }
        }

        for (World world : Bukkit.getWorlds()) {
            world.setTime(time);
        }

        plugin.getMessenger().sendSuccess(sender, ChatColor.GREEN + "Set time to " + ChatColor.GOLD + time);

        return true;
    }
}
