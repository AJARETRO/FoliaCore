package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class KickCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public KickCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.kick")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /kick <player> [reason]");
            return true;
        }

        String playerName = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Kicked by an administrator";

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return true;
        }

        if (target.hasPermission("foliacore.kick.exempt")) {
            plugin.getMessenger().sendError(sender, "You cannot kick this player.");
            return true;
        }

        String kickMessage = ChatColor.GOLD + "Kicked from server\n" + ChatColor.WHITE + "Reason: " + ChatColor.RED + reason;
        target.kick(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage));

        plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " has been kicked. Reason: " + ChatColor.WHITE + reason);
        Bukkit.broadcast(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.GOLD + target.getName() + ChatColor.RED + " has been kicked from the server."));

        return true;
    }
}
