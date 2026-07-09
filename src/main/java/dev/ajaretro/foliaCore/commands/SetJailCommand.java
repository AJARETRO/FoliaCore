package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetJailCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public SetJailCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.setjail")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /setjail <name>");
            return true;
        }

        String name = args[0].toLowerCase();
        plugin.getJailManager().setJail(name, player.getLocation());
        plugin.getMessenger().sendSuccess(player, "Jail '" + name + "' set at your current location.");
        return true;
    }
}
