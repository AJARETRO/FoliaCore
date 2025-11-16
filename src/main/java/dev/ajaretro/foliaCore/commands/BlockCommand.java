package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public BlockCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!sender.hasPermission("foliacore.block")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /block <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        Player player = (Player) sender;
        if (player.getUniqueId().equals(target.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You cannot block yourself.");
            return true;
        }

        plugin.getChatManager().blockPlayer(player.getUniqueId(), target.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have blocked " + target.getName() + ".");

        return true;
    }
}