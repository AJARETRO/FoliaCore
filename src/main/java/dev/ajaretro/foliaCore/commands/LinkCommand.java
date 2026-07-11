/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public LinkCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            plugin.getMessenger().sendError(sender, "Discord module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.link")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (plugin.getDiscordManager().isLinked(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Your account is already linked to Discord!");
            return true;
        }

        String code = plugin.getDiscordManager().generateLinkCode(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Your verification code: §e" + code);
        player.sendMessage("§7Send this 6-digit code to the Discord bot to complete verification.");
        return true;
    }
}
