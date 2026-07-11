/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public PayCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            plugin.getMessenger().sendError(sender, "Economy module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.pay")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /pay <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is offline.");
            return true;
        }

        if (target.equals(player)) {
            plugin.getMessenger().sendError(player, "You cannot pay yourself.");
            return true;
        }

        var eco = plugin.getEconomyManager();
        if (eco.isPaymentsDisabled(target.getUniqueId())) {
            plugin.getMessenger().sendError(player, target.getName() + " is not accepting payments.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Invalid amount.");
            return true;
        }

        if (amount <= 0.01) {
            plugin.getMessenger().sendError(player, "Amount must be at least $0.01.");
            return true;
        }

        if (eco.isPayConfirmEnabled(player.getUniqueId())) {
            if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
                player.sendMessage("§ePlease confirm you want to pay " + target.getName() + " " + eco.format(amount) + " by typing: §b/pay " + target.getName() + " " + amount + " confirm");
                return true;
            }
        }

        if (!eco.has(player, amount)) {
            plugin.getMessenger().sendError(player, "Insufficient funds.");
            return true;
        }

        EconomyResponse withdrawRep = eco.withdrawPlayer(player, amount);
        if (withdrawRep.transactionSuccess()) {
            EconomyResponse depositRep = eco.depositPlayer(target, amount);
            if (depositRep.transactionSuccess()) {
                plugin.getMessenger().sendSuccess(player, "You sent " + eco.format(amount) + " to " + target.getName() + ".");
                // Since target might be on a different region thread in Folia, run a thread-safe message send or just use target.sendMessage directly
                target.sendMessage("§aYou received " + eco.format(amount) + " from " + player.getName() + ".");
            } else {
                // Refund
                eco.depositPlayer(player, amount);
                plugin.getMessenger().sendError(player, "Failed to send money. Transaction cancelled.");
            }
        } else {
            plugin.getMessenger().sendError(player, "Transaction failed.");
        }

        return true;
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
