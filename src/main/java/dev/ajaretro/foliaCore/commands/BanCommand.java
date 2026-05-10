package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Ban;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public BanCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.ban")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /ban <player> [reason]");
            return true;
        }

        String playerName = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason provided";

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return true;
        }

        if (target.hasPermission("foliacore.ban.exempt")) {
            plugin.getMessenger().sendError(sender, "You cannot ban this player.");
            return true;
        }

        plugin.getBanManager().banPlayer(target.getUniqueId(), target.getName(), reason, true, -1);

        String kickMessage = ChatColor.RED + "You have been banned.\n" + ChatColor.WHITE + "Reason: " + ChatColor.GOLD + reason;
        target.kick(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(kickMessage));

        plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + target.getName() + ChatColor.GREEN + " has been banned. Reason: " + ChatColor.WHITE + reason);
        Bukkit.broadcast(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.RED + "[Ban] " + ChatColor.GOLD + target.getName() + ChatColor.RED + " has been banned."));

        return true;
    }
}
