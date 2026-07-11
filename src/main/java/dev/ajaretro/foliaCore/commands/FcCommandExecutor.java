/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * Command proxy executor routing /fc <command> to their respective
 * FoliaCore command execution handlers.
 */
public class FcCommandExecutor implements CommandExecutor, org.bukkit.command.TabCompleter {

    private final FoliaCore plugin;

    public FcCommandExecutor(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Command Proxy ===");
            plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Use " + ChatColor.WHITE + "/" + label + " <command> [args...]" + ChatColor.GRAY + " to execute a subcommand.");
            return true;
        }

        String subCommandName = args[0].toLowerCase(Locale.ROOT);
        CommandExecutor executor = plugin.getCommandExecutor(subCommandName);
        if (executor == null) {
            plugin.getMessenger().sendError(sender, "Unknown subcommand: " + subCommandName);
            return true;
        }

        // Shift arguments
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        // Execute proxy command
        return plugin.executeCommandProxy(subCommandName, executor, sender, subArgs);
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            java.util.List<String> completions = new java.util.ArrayList<>();
            for (String cmd : plugin.getConfigManager().getRegisteredCommands()) {
                if (plugin.getConfigManager().isCommandEnabled(cmd)) {
                    completions.add(cmd);
                }
            }
            java.util.List<String> suggestions = new java.util.ArrayList<>();
            org.bukkit.util.StringUtil.copyPartialMatches(args[0], completions, suggestions);
            java.util.Collections.sort(suggestions);
            return suggestions;
        }
        return java.util.Collections.emptyList();
    }
}
