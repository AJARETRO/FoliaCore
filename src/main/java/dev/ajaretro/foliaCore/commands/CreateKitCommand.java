/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.KitManager;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;

public class CreateKitCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final KitManager kitManager;
    private static final Pattern KIT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public CreateKitCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.kitManager = plugin.getKitManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.kit.admin")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /createkit <name> <cooldown>");
            plugin.getMessenger().sendError(player, "Example: /createkit starter 1d");
            return true;
        }

        String kitName = args[0];
        String timeString = args[1];

        if (!KIT_NAME_PATTERN.matcher(kitName).matches()) {
            plugin.getMessenger().sendError(player, "Kit name must be 3-20 characters and only contain letters, numbers, and underscores.");
            return true;
        }

        if (kitManager.isKit(kitName)) {
            plugin.getMessenger().sendError(player, "A kit with that name already exists. Use /delkit first.");
            return true;
        }

        long cooldownSeconds = TimeUtil.parseTime(timeString) / 1000;
        if (cooldownSeconds <= 0) {
            plugin.getMessenger().sendError(player, "Invalid time format. Use: 10s, 5m, 1h, 3d");
            return true;
        }

        ItemStack displayItem = player.getInventory().getItemInMainHand();
        if (displayItem.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must be holding an item to use as the kit's display icon.");
            return true;
        }

        String permission = "foliacore.kit." + kitName.toLowerCase();
        ItemStack[] items = player.getInventory().getContents();

        kitManager.createKit(kitName, cooldownSeconds, permission, displayItem.getType(), items);
        plugin.getMessenger().sendSuccess(player, "Kit '" + ChatColor.GOLD + kitName + ChatColor.GREEN + "' created with a " + TimeUtil.formatDuration(cooldownSeconds * 1000) + " cooldown.");
        return true;
    }
}