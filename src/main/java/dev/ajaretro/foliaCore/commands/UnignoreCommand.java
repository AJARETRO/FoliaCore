package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UnignoreCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public UnignoreCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.unignore")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /unignore <player>");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return true;
        }

        boolean unignored = plugin.getIgnoreManager().unignorePlayer(player.getUniqueId(), target.getUniqueId());
        if (unignored) {
            plugin.getMessenger().sendSuccess(player, "You are no longer ignoring " + target.getName() + ".");
        } else {
            plugin.getMessenger().sendError(player, "You were not ignoring " + target.getName() + ".");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player && sender.hasPermission("foliacore.unignore")) {
            Player player = (Player) sender;
            List<String> list = new ArrayList<>();
            for (UUID ignoredUUID : plugin.getIgnoreManager().getIgnoredPlayers(player.getUniqueId())) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(ignoredUUID);
                if (op.getName() != null && op.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(op.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
