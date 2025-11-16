package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public UnmuteCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.unmute")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /unmute <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        plugin.getChatManager().unmutePlayer(target.getUniqueId());

        plugin.getMessenger().sendSuccess(sender, target.getName() + " has been unmuted.");

        Player onlineTarget = target.getPlayer();
        if (onlineTarget != null) {
            plugin.getMessenger().sendSuccess(onlineTarget, "You have been unmuted.");
        }

        return true;
    }
}