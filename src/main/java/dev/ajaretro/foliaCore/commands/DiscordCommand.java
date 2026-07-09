package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiscordCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public DiscordCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            plugin.getMessenger().sendError(sender, "Discord module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.discord")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        String invite = plugin.getConfigManager().getConfig().getString("discord.invite-link", "https://discord.gg/ajaretro");
        plugin.getMessenger().sendSuccess(sender, "Join our Discord: §b" + invite);
        return true;
    }
}
