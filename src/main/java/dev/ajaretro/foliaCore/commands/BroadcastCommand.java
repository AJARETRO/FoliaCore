package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class BroadcastCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public BroadcastCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.broadcast")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /broadcast <message...>");
            return true;
        }

        String message = ChatColor.GOLD + "[SERVER] " + ChatColor.WHITE + String.join(" ", args);
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message));

        return true;
    }
}
