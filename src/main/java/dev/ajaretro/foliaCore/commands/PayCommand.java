package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.EconomyManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final EconomyManager eco;

    public PayCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.eco = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!eco.hasEconomy()) {
            plugin.getMessenger().sendError(sender, "Economy features are disabled.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.pay")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /pay <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You cannot pay yourself.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Invalid amount.");
            return true;
        }

        if (amount <= 0) {
            plugin.getMessenger().sendError(player, "Amount must be greater than zero.");
            return true;
        }

        if (!eco.has(player, amount)) {
            plugin.getMessenger().sendError(player, "You do not have enough money.");
            return true;
        }

        EconomyResponse r = eco.withdraw(player, amount);
        if (r.transactionSuccess()) {
            eco.deposit(target, amount);
            String formattedAmount = eco.format(amount);
            plugin.getMessenger().sendSuccess(player, "You sent " + ChatColor.GOLD + formattedAmount + ChatColor.GREEN + " to " + target.getName() + ".");

            Player onlineTarget = target.getPlayer();
            if (onlineTarget != null) {
                onlineTarget.getScheduler().run(plugin, (task) -> {
                    plugin.getMessenger().sendMessage(onlineTarget, ChatColor.GOLD + player.getName() + ChatColor.WHITE + " sent you " + ChatColor.GOLD + formattedAmount);
                }, null);
            }
        } else {
            plugin.getMessenger().sendError(player, "An error occurred: " + r.errorMessage);
        }
        return true;
    }
}