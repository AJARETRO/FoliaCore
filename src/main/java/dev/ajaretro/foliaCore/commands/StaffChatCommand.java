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
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Staff-only chat command for confidential communication.
 */
public class StaffChatCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public StaffChatCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("foliacore.staffchat")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            // Toggle staff chat mode
            plugin.getVanishManager().toggleStaffChat(player.getUniqueId());
            
            if (plugin.getVanishManager().isInStaffChatMode(player.getUniqueId())) {
                plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Staff Chat mode " + ChatColor.GREEN + "enabled. Messages go to staff only.");
            } else {
                plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Staff Chat mode " + ChatColor.RED + "disabled.");
            }
        } else {
            // Send a one-time staff message
            String message = String.join(" ", args);
            sendStaffMessage(player, message);
        }

        return true;
    }

    private void sendStaffMessage(Player sender, String message) {
        String formattedMessage = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "STAFF" + ChatColor.DARK_GRAY + "] " +
                ChatColor.YELLOW + sender.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("foliacore.staffchat")) {
                onlinePlayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(formattedMessage));
            }
        }

        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(formattedMessage));
    }
}
