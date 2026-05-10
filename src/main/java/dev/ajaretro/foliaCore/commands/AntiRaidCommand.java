package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/**
 * Admin command for managing anti-raid features.
 * Subcommands:
 * - /antiraid pause <player> <duration>
 * - /antiraid reset <player> (console-only)
 * - /antiraid status <player>
 */
public class AntiRaidCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public AntiRaidCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.admin.antiraid")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("pause")) {
            handlePause(sender, args);
        } else if (subcommand.equals("reset")) {
            handleReset(sender, args);
        } else if (subcommand.equals("status")) {
            handleStatus(sender, args);
        } else {
            sendHelp(sender);
        }

        return true;
    }

    private void handlePause(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessenger().sendError(sender, "Usage: /antiraid pause <player> <duration>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return;
        }

        long durationMs = TimeUtil.parseTime(args[2]);
        if (durationMs <= 0) {
            plugin.getMessenger().sendError(sender, "Invalid time format. Use: 1d, 2h, 30m, 1d12h");
            return;
        }

        plugin.getAntiRaidManager().pausePlayer(target.getUniqueId(), durationMs);
        
        String duration = TimeUtil.formatDuration(durationMs);
        plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " can now build freely for " + ChatColor.GOLD + duration);
        plugin.getMessenger().sendSuccess(target, "You have been granted " + ChatColor.GOLD + duration + ChatColor.GREEN + " of unrestricted building.");
    }

    private void handleReset(CommandSender sender, String[] args) {
        // CONSOLE-ONLY command
        if (!(sender instanceof ConsoleCommandSender)) {
            plugin.getMessenger().sendError(sender, "This command can only be executed from the console.");
            return;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /antiraid reset <player>");
            return;
        }

        try {
            java.util.UUID targetUUID = java.util.UUID.fromString(args[1]);
            if (plugin.getAntiRaidManager().isLocked(targetUUID)) {
                plugin.getAntiRaidManager().resetLockdown(targetUUID);
                sender.sendMessage(ChatColor.GREEN + "Lockdown reset for UUID: " + targetUUID);
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Player is not locked down.");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid UUID format.");
        }
    }

    private void handleStatus(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /antiraid status <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null && args[1].length() == 36) {
            // Try UUID lookup
            try {
                java.util.UUID uuid = java.util.UUID.fromString(args[1]);
                if (plugin.getAntiRaidManager().isLocked(uuid)) {
                    var state = plugin.getAntiRaidManager().getLockdownState(uuid);
                    plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + state.playerName + ChatColor.WHITE + " is LOCKED DOWN.");
                    plugin.getMessenger().sendMessage(sender, "Requires console reset: " + (state.requiresConsoleReset ? "YES" : "NO"));
                } else {
                    plugin.getMessenger().sendMessage(sender, "Player UUID is not locked down.");
                }
                return;
            } catch (IllegalArgumentException ignored) {}
        }

        if (target == null) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return;
        }

        if (plugin.getAntiRaidManager().isLocked(target.getUniqueId())) {
            plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + target.getName() + ChatColor.RED + " is LOCKED DOWN.");
        } else {
            plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " is not locked down.");
        }
    }

    private void sendHelp(CommandSender sender) {
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== Anti-Raid Commands ===");
        plugin.getMessenger().sendMessage(sender, "/antiraid pause <player> <duration> - Allow building for a time");
        plugin.getMessenger().sendMessage(sender, "/antiraid reset <player/uuid> - Reset lockdown (Console only)");
        plugin.getMessenger().sendMessage(sender, "/antiraid status <player> - Check lockdown status");
    }
}
