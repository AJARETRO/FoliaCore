/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RulesCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public RulesCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.rules")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        List<String> rules = plugin.getConfigManager().getConfig().getStringList("rules");
        if (rules == null || rules.isEmpty()) {
            plugin.getMessenger().sendError(sender, "No rules have been configured.");
            return true;
        }

        sender.sendMessage("§8§m══════════════§r §6§lSERVER RULES §8§m══════════════");
        for (String rule : rules) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(rule));
        }
        sender.sendMessage("§8§m═════════════════════════════════════════");
        return true;
    }
}
