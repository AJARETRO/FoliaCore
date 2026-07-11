/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.JailedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class JailSecondaryCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public JailSecondaryCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.jailedplayers")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        Map<UUID, JailedPlayer> jailed = plugin.getJailManager().getJailedPlayers();
        if (jailed.isEmpty()) {
            plugin.getMessenger().sendMessage(sender, "§aThere are no jailed players.");
            return true;
        }

        sender.sendMessage("§8§m═════════════§r §6§lJAILED PLAYERS §8§m═════════════");
        for (JailedPlayer jp : jailed.values()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(jp.getUuid());
            String name = op.getName() != null ? op.getName() : jp.getUuid().toString();
            String duration = jp.getExpiryTime() == -1 ? "Permanent" : String.format("%.1fm remaining", (jp.getExpiryTime() - System.currentTimeMillis()) / 60000.0);
            sender.sendMessage("§e- §f" + name + " §7(Jail: §e" + jp.getJailName() + "§7) | §c" + duration + " | Reason: §7" + jp.getReason());
        }
        sender.sendMessage("§8§m═════════════════════════════════════════");
        return true;
    }
}
