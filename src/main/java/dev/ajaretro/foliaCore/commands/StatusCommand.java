package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

/**
 * Displays server status including TPS and tick times.
 * Adapted for Folia's region-based threading.
 */
public class StatusCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public StatusCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.status")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        Player player = sender instanceof Player ? (Player) sender : null;

        if (player != null) {
            // Use the player's region scheduler for accurate local metrics
            player.getScheduler().run(plugin, (task) -> {
                displayStatus(sender, player);
            }, null);
        } else {
            // Console can get global metrics
            displayGlobalStatus(sender);
        }

        return true;
    }

    private void displayStatus(CommandSender sender, Player player) {
        long[] tickTimes = Bukkit.getServer().getTickTimes();
        double avgTickTime = calculateAverageTick(tickTimes);
        double tps = Math.min(20.0, 1000.0 / (avgTickTime / 1000000.0));

        int entityCount = player.getWorld().getEntities().size();
        int playerCount = Bukkit.getOnlinePlayers().size();

        plugin.getMessenger().sendMessage(sender, "");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== Server Status ===");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "World: " + ChatColor.WHITE + player.getWorld().getName());
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "TPS: " + getTpsColor(tps) + String.format("%.2f", tps));
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Tick Time: " + ChatColor.WHITE + String.format("%.2f", avgTickTime / 1000000.0) + "ms");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Entities: " + ChatColor.WHITE + entityCount);
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Players: " + ChatColor.WHITE + playerCount);
        plugin.getMessenger().sendMessage(sender, "");
    }

    private void displayGlobalStatus(CommandSender sender) {
        long[] tickTimes = Bukkit.getServer().getTickTimes();
        double avgTickTime = calculateAverageTick(tickTimes);
        double tps = Math.min(20.0, 1000.0 / (avgTickTime / 1000000.0));

        int totalEntities = 0;
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            totalEntities += world.getEntities().size();
        }

        plugin.getMessenger().sendMessage(sender, "");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== Global Server Status ===");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Worlds: " + ChatColor.WHITE + Bukkit.getWorlds().size());
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "TPS: " + getTpsColor(tps) + String.format("%.2f", tps));
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Tick Time: " + ChatColor.WHITE + String.format("%.2f", avgTickTime / 1000000.0) + "ms");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Total Entities: " + ChatColor.WHITE + totalEntities);
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        plugin.getMessenger().sendMessage(sender, "");
    }

    private double calculateAverageTick(long[] tickTimes) {
        if (tickTimes.length == 0) return 0;
        long sum = 0;
        for (long time : tickTimes) {
            sum += time;
        }
        return (double) sum / tickTimes.length;
    }

    private String getTpsColor(double tps) {
        if (tps >= 19.0) return ChatColor.GREEN.toString();
        if (tps >= 15.0) return ChatColor.YELLOW.toString();
        return ChatColor.RED.toString();
    }
}
