/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EconomyUtilsCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public EconomyUtilsCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            plugin.getMessenger().sendError(sender, "Economy module is disabled.");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("balancetop")) {
            handleBalanceTop(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (cmd.equals("paytoggle")) {
            if (!player.hasPermission("foliacore.paytoggle")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            boolean disabled = plugin.getEconomyManager().togglePayments(player.getUniqueId());
            plugin.getMessenger().sendSuccess(player, "Payments are now " + (disabled ? "§cDISABLED" : "§aENABLED") + "§a.");
            return true;
        }

        if (cmd.equals("payconfirmtoggle")) {
            if (!player.hasPermission("foliacore.payconfirmtoggle")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            boolean enabled = plugin.getEconomyManager().togglePayConfirm(player.getUniqueId());
            plugin.getMessenger().sendSuccess(player, "Payment confirmations are now " + (enabled ? "§aENABLED" : "§cDISABLED") + "§a.");
            return true;
        }

        if (cmd.equals("setworth")) {
            if (!player.hasPermission("foliacore.setworth")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            handleSetWorth(player, args);
            return true;
        }

        return true;
    }

    private void handleBalanceTop(CommandSender sender) {
        var eco = plugin.getEconomyManager();
        List<Map.Entry<UUID, Double>> list = new ArrayList<>(eco.getBalances().entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        sender.sendMessage("§8§m═════════════§r §6§lTOP BALANCES §8§m═════════════");
        int max = Math.min(list.size(), 10);
        if (max == 0) {
            sender.sendMessage("§7No balances registered yet.");
        } else {
            for (int i = 0; i < max; i++) {
                Map.Entry<UUID, Double> entry = list.get(i);
                OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey());
                String name = op.getName() != null ? op.getName() : entry.getKey().toString();
                sender.sendMessage("§e" + (i + 1) + ". §f" + name + "§7: §a" + eco.format(entry.getValue()));
            }
        }
        sender.sendMessage("§8§m═════════════════════════════════════════");
    }

    private void handleSetWorth(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /setworth <price>");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Invalid price.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must hold an item to set its worth.");
            return;
        }

        Material mat = item.getType();
        plugin.getConfigManager().getConfig().set("economy.prices." + mat.name(), price);
        plugin.getConfigManager().save();

        plugin.getMessenger().sendSuccess(player, "Set worth of " + mat.name() + " to " + plugin.getEconomyManager().format(price));
    }
}
