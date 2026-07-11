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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public BalanceCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            plugin.getMessenger().sendError(sender, "Economy module is disabled.");
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /balance [player]");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("foliacore.balance")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
            double bal = plugin.getEconomyManager().getBalance(player);
            plugin.getMessenger().sendSuccess(player, "Your balance: " + plugin.getEconomyManager().format(bal));
            return true;
        }

        // Check another player
        if (!sender.hasPermission("foliacore.balance.others")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to check other players' balance.");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return true;
        }

        double bal = plugin.getEconomyManager().getBalance(target);
        plugin.getMessenger().sendSuccess(sender, target.getName() + "'s balance: " + plugin.getEconomyManager().format(bal));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.balance.others")) {
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
