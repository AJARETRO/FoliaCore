package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public MuteCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.mute")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /mute <player> <time|permanent>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        String timeString = args[1];
        long duration;
        String durationFormatted;

        if (timeString.equalsIgnoreCase("perm") || timeString.equalsIgnoreCase("permanent")) {
            duration = -1;
            durationFormatted = "permanently";
        } else {
            duration = TimeUtil.parseTime(timeString);
            if (duration <= 0) {
                plugin.getMessenger().sendError(sender, "Invalid time format. Use: 10s, 5m, 1h, 3d");
                return true;
            }
            durationFormatted = "for " + TimeUtil.formatDuration(duration);
        }

        plugin.getChatManager().mutePlayer(target.getUniqueId(), duration);

        plugin.getMessenger().sendSuccess(sender, target.getName() + " has been " + durationFormatted + " muted.");

        Player onlineTarget = target.getPlayer();
        if (onlineTarget != null) {
            plugin.getMessenger().sendError(onlineTarget, "You have been " + durationFormatted + " muted.");
        }

        return true;
    }
}