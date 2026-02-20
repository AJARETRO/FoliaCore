package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.EconomyManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class EcoCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final EconomyManager eco;

    public EcoCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.eco = plugin.getEconomyManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!eco.hasEconomy()) {
            plugin.getMessenger().sendError(sender, "Economy features are disabled.");
            return;
        }

        if (!sender.hasPermission("foliacore.eco")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length < 3) {
            sendHelp(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(sender, "Invalid amount.");
            return;
        }

        if (amount < 0) {
            plugin.getMessenger().sendError(sender, "Amount cannot be negative.");
            return;
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
        return;
    }

    private void sendHelp(CommandSender sender) {
        plugin.getMessenger().sendMessage(sender, ChatColor.YELLOW + "--- FoliaCore Economy Admin ---");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco give <player> <amount>");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco take <player> <amount>");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "/eco set <player> <amount>");
    }
}