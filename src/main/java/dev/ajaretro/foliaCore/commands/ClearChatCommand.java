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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Clears the global chat.
 */
public class ClearChatCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public ClearChatCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.clearchat")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        String clearedMessage = ChatColor.GRAY + "[Chat cleared by " + sender.getName() + "]";
        String clearLine = "\n".repeat(100);

        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("foliacore.clearchat.bypass")) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(clearedMessage));
            }
        }

        plugin.getMessenger().sendSuccess(sender, "Chat cleared for all players without bypass.");

        return true;
    }
}
