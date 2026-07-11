/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PowertoolCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public PowertoolCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.powertool")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must hold an item to set a powertool.");
            return true;
        }

        Material material = item.getType();

        if (args.length == 0) {
            // Clear powertool
            String existing = plugin.getPowertoolManager().getPowertool(player.getUniqueId(), material);
            if (existing == null) {
                plugin.getMessenger().sendError(player, "No powertool is bound to " + material.name() + ".");
            } else {
                plugin.getPowertoolManager().removePowertool(player.getUniqueId(), material);
                plugin.getMessenger().sendSuccess(player, "Cleared powertool from " + material.name() + ".");
            }
            return true;
        }

        StringBuilder cmdBuilder = new StringBuilder();
        for (String arg : args) {
            cmdBuilder.append(arg).append(" ");
        }
        String cmd = cmdBuilder.toString().trim();
        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }

        plugin.getPowertoolManager().setPowertool(player.getUniqueId(), material, cmd);
        plugin.getMessenger().sendSuccess(player, "Successfully bound command `" + cmd + "` to " + material.name() + ".");
        return true;
    }
}
