/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorthCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public WorthCommand(FoliaCore plugin) {
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
        if (!player.hasPermission("foliacore.worth")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must hold an item to check its worth.");
            return true;
        }

        double price = plugin.getConfigManager().getConfig().getDouble("economy.prices." + item.getType().name(), -1.0);
        if (price < 0) {
            // Provide some default pricing for common items if not configured
            if (item.getType() == Material.DIAMOND) price = 100.0;
            else if (item.getType() == Material.GOLD_INGOT) price = 50.0;
            else if (item.getType() == Material.IRON_INGOT) price = 10.0;
            else if (item.getType() == Material.COAL) price = 2.0;
            else if (item.getType() == Material.EMERALD) price = 150.0;
            else {
                plugin.getMessenger().sendError(player, "This item has no sell value.");
                return true;
            }
        }

        double total = price * item.getAmount();
        plugin.getMessenger().sendSuccess(player, "Worth of " + item.getAmount() + "x " + item.getType().name() + ": " +
                plugin.getEconomyManager().format(total) + " (" + plugin.getEconomyManager().format(price) + " each)");
        return true;
    }
}
