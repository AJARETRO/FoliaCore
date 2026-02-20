package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public DelHomeCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!player.hasPermission("foliacore.delhome")) {
            plugin.getMessenger().sendError(player, "You do not have permission to delete a home.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /delhome <name>");
            return;
        }

        String homeName = args[0];
        if (tm.getHome(player.getUniqueId(), homeName) == null) {
            plugin.getMessenger().sendError(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.RED + "' not found.");
            return;
        }

        tm.deleteHome(player.getUniqueId(), homeName);
        plugin.getMessenger().sendSuccess(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.GREEN + "' has been deleted.");
    }
}