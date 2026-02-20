package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.KitManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteKitCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final KitManager kitManager;

    public DeleteKitCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.kitManager = plugin.getKitManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!sender.hasPermission("foliacore.kit.admin")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /delkit <name>");
            return;
        }

        String kitName = args[0];
        if (!kitManager.isKit(kitName)) {
            plugin.getMessenger().sendError(sender, "A kit with the name '" + ChatColor.GOLD + kitName + ChatColor.RED + "' does not exist.");
            return;
        }

        kitManager.deleteKit(kitName);
        plugin.getMessenger().sendSuccess(sender, "Kit '" + ChatColor.GOLD + kitName + ChatColor.GREEN + "' has been deleted.");
    }
}