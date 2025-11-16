package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnblockCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public UnblockCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!sender.hasPermission("foliacore.unblock")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /unblock <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        Player player = (Player) sender;
        plugin.getChatManager().unblockPlayer(player.getUniqueId(), target.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have unblocked " + target.getName() + ".");

        return true;
    }
}