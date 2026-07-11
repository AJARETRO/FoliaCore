/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitInfoCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public KitInfoCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isKitsEnabled()) {
            plugin.getMessenger().sendError(sender, "Kits module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.showkit")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /showkit <kit_name>");
            return true;
        }

        String kitName = args[0];
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            plugin.getMessenger().sendError(player, "Kit not found: " + kitName);
            return true;
        }

        // Open a preview GUI of the kit contents
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            ItemStack[] items = kit.getItems();
            int size = (int) Math.ceil(items.length / 9.0) * 9;
            if (size == 0) size = 9;
            
            Inventory preview = Bukkit.createInventory(null, size, "§1Kit Preview: §8" + kit.getName());
            for (ItemStack item : items) {
                if (item != null) {
                    preview.addItem(item.clone());
                }
            }
            player.openInventory(preview);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.showkit")) {
            List<String> list = new ArrayList<>();
            for (Kit kit : plugin.getKitManager().getAllKits()) {
                if (kit.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(kit.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
