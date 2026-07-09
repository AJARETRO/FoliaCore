package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlinkCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public UnlinkCommand(FoliaCore plugin) {
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
        if (!player.hasPermission("foliacore.unlink")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (!plugin.getDiscordManager().isLinked(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Your account is not linked to Discord.");
            return true;
        }

        plugin.getDiscordManager().unlink(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Your account has been unlinked from Discord.");
        return true;
    }
}
