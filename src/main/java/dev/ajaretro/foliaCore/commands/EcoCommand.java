package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final EconomyManager eco;

    public EcoCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.eco = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!eco.hasEconomy()) {
            plugin.getMessenger().sendError(sender, "Economy features are disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.eco")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
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

        String formattedAmount = eco.format(amount);

        switch (subCommand) {
            case "give":
                eco.deposit(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Gave " + ChatColor.GOLD + formattedAmount + ChatColor.GREEN + " to " + target.getName());
                break;
            case "take":
                eco.withdraw(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Took " + ChatColor.GOLD + formattedAmount + ChatColor.GREEN + " from " + target.getName());
                break;
            case "set":
                double balance = eco.getBalance(target);
                eco.withdraw(target, balance);
                eco.deposit(target, amount);
                plugin.getMessenger().sendSuccess(sender, "Set " + target.getName() + "'s balance to " + ChatColor.GOLD + formattedAmount);
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        plugin.getMessenger().sendMessage(sender, ChatColor.YELLOW + "--- FoliaCore Economy Admin ---");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco give <player> <amount>");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco take <player> <amount>");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco set <player> <amount>");
    }
}