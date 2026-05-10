package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempbanCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TempbanCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.tempban")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /tempban <player> <time> [reason]");
            return true;
        }

        String playerName = args[0];
        String timeStr = args[1];
        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return true;
        }

        if (target.hasPermission("foliacore.ban.exempt")) {
            plugin.getMessenger().sendError(sender, "You cannot ban this player.");
            return true;
        }

        long durationMs = TimeUtil.parseTime(timeStr);
        if (durationMs <= 0) {
            plugin.getMessenger().sendError(sender, "Invalid time format. Use formats like: 1d, 2h, 30m, 1d12h");
            return true;
        }

        long expiryTime = System.currentTimeMillis() + durationMs;
        plugin.getBanManager().banPlayer(target.getUniqueId(), target.getName(), reason, false, expiryTime);

        String formattedTime = TimeUtil.formatDuration(durationMs);
        String kickMessage = ChatColor.RED + "You have been temporarily banned for " + formattedTime + "\n" + ChatColor.WHITE + "Reason: " + ChatColor.GOLD + reason;
        target.kick(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage));

        plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " has been temporarily banned for " + ChatColor.GOLD + formattedTime);
        Bukkit.broadcast(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.YELLOW + "[Tempban] " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has been banned for " + formattedTime + "."));

        return true;
    }
}
