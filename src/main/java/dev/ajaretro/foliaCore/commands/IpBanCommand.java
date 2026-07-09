package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class IpBanCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public IpBanCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (!sender.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (cmd.equals("unbanip")) {
            if (args.length < 1) {
                plugin.getMessenger().sendError(sender, "Usage: /unbanip <ip_address>");
                return true;
            }
            String ip = args[0];
            Bukkit.getBanList(BanList.Type.IP).pardon(ip);
            plugin.getMessenger().sendSuccess(sender, "Pardoned IP: " + ip);
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(sender, "Usage: /" + cmd + " <player/ip> [reason/duration]");
            return true;
        }

        String targetInput = args[0];
        String ipAddress = null;

        if (targetInput.contains(".") || targetInput.contains(":")) {
            ipAddress = targetInput;
        } else {
            Player p = Bukkit.getPlayer(targetInput);
            if (p != null && p.isOnline() && p.getAddress() != null) {
                ipAddress = p.getAddress().getAddress().getHostAddress();
            } else {
                plugin.getMessenger().sendError(sender, "Player not found or offline. Please specify a raw IP address.");
                return true;
            }
        }

        if (cmd.equals("banip")) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = reasonBuilder.toString().trim();
            if (reason.isEmpty()) reason = "Banned by administrator.";

            Bukkit.getBanList(BanList.Type.IP).addBan(ipAddress, reason, null, sender.getName());
            
            // Kick player if online
            kickIfMatchesIp(ipAddress, reason);
            plugin.getMessenger().sendSuccess(sender, "IP " + ipAddress + " has been permanently banned.");
        } else if (cmd.equals("tempbanip")) {
            if (args.length < 2) {
                plugin.getMessenger().sendError(sender, "Usage: /tempbanip <player/ip> <duration_seconds> [reason]");
                return true;
            }
            long seconds;
            try {
                seconds = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                plugin.getMessenger().sendError(sender, "Invalid duration (seconds).");
                return true;
            }

            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = reasonBuilder.toString().trim();
            if (reason.isEmpty()) reason = "Tempbanned by administrator.";

            Date expiry = new Date(System.currentTimeMillis() + (seconds * 1000L));
            Bukkit.getBanList(BanList.Type.IP).addBan(ipAddress, reason, expiry, sender.getName());

            kickIfMatchesIp(ipAddress, reason);
            plugin.getMessenger().sendSuccess(sender, "IP " + ipAddress + " has been temporarily banned for " + seconds + " seconds.");
        }

        return true;
    }

    private void kickIfMatchesIp(String ip, String reason) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getAddress() != null && p.getAddress().getAddress().getHostAddress().equals(ip)) {
                // Run region-safe kick
                p.getScheduler().run(plugin, task -> p.kickPlayer("IP Banned: " + reason), null);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
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
