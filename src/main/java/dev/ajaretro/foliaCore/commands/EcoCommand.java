package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EcoCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public EcoCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            plugin.getMessenger().sendError(sender, "Economy module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.eco.admin")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /eco <give|take|set|reset> <player> [amount]");
            return true;
        }

        String action = args[0].toLowerCase();
        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        var eco = plugin.getEconomyManager();

        if (action.equals("reset")) {
            double defaultBal = plugin.getConfigManager().getConfig().getDouble("economy.default-balance", 1000.0);
            eco.withdrawPlayer(target, eco.getBalance(target));
            eco.depositPlayer(target, defaultBal);
            plugin.getMessenger().sendSuccess(sender, "Reset " + target.getName() + "'s balance to " + eco.format(defaultBal));
            return true;
        }

        if (args.length < 3) {
            plugin.getMessenger().sendError(sender, "Usage: /eco " + action + " <player> <amount>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(sender, "Invalid amount.");
            return true;
        }

        if (amount < 0) {
            plugin.getMessenger().sendError(sender, "Amount cannot be negative.");
            return true;
        }

        switch (action) {
            case "give":
                eco.depositPlayer(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Gave " + eco.format(amount) + " to " + target.getName() + ".");
                break;
            case "take":
                eco.withdrawPlayer(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Took " + eco.format(amount) + " from " + target.getName() + ".");
                break;
            case "set":
                eco.withdrawPlayer(target, eco.getBalance(target));
                eco.depositPlayer(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Set " + target.getName() + "'s balance to " + eco.format(amount));
                break;
            default:
                plugin.getMessenger().sendError(sender, "Usage: /eco <give|take|set|reset> <player> [amount]");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("foliacore.eco.admin")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return List.of("give", "take", "set", "reset");
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
