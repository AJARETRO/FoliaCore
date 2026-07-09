package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public IgnoreCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.ignore")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /ignore <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is offline.");
            return true;
        }

        if (target.equals(player)) {
            plugin.getMessenger().sendError(player, "You cannot ignore yourself.");
            return true;
        }

        boolean ignored = plugin.getIgnoreManager().ignorePlayer(player.getUniqueId(), target.getUniqueId());
        if (ignored) {
            plugin.getMessenger().sendSuccess(player, "You are now ignoring " + target.getName() + ".");
        } else {
            plugin.getMessenger().sendError(player, "You are already ignoring " + target.getName() + ".");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.ignore")) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
