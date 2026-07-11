/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.ExpressionEvaluator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class CalcCommand implements CommandExecutor {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.############");
    private final FoliaCore plugin;

    public CalcCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.calc")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /calc <expression>");
            return true;
        }

        String expression = String.join("", args);
        try {
            double value = ExpressionEvaluator.evaluate(expression);
            plugin.getMessenger().sendSuccess(sender, "Result: " + FORMAT.format(value));
        } catch (IllegalArgumentException ex) {
            plugin.getMessenger().sendError(sender, ex.getMessage());
        }

        return true;
    }
}
