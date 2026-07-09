package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JailCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public JailCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.jail")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /jail <player> <jail_name> [duration_seconds] [reason]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "Player not found or offline.");
            return true;
        }

        String jailName = args[1].toLowerCase();
        if (plugin.getJailManager().getJail(jailName) == null) {
            plugin.getMessenger().sendError(sender, "Jail '" + jailName + "' does not exist.");
            return true;
        }

        long duration = -1;
        if (args.length >= 3) {
            try {
                duration = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                plugin.getMessenger().sendError(sender, "Invalid duration (must be a number of seconds).");
                return true;
            }
        }

        StringBuilder reasonBuilder = new StringBuilder();
        if (args.length >= 4) {
            for (int i = 3; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
        }
        String reason = reasonBuilder.toString().trim();
        if (reason.isEmpty()) {
            reason = "No reason specified";
        }

        // Execute jail
        boolean jailed = plugin.getJailManager().jailPlayer(target.getUniqueId(), jailName, duration, reason, target.getLocation());
        if (jailed) {
            plugin.getMessenger().sendSuccess(sender, "Successfully jailed " + target.getName() + " in '" + jailName + "'.");
        } else {
            plugin.getMessenger().sendError(sender, "Could not jail player.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("foliacore.jail")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            for (String jailName : plugin.getJailManager().getJails().keySet()) {
                if (jailName.startsWith(args[1].toLowerCase())) {
                    list.add(jailName);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
