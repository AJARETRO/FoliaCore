package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaHereCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public TpaHereCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.tpahere")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /tpahere <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is not online.");
            return true;
        }

        if (player.equals(target)) {
            plugin.getMessenger().sendError(player, "You cannot send a teleport request to yourself.");
            return true;
        }

        plugin.getTeleportManager().createTpaRequest(player.getUniqueId(), target.getUniqueId(), TeleportManager.TpaType.TPAHERE);
        plugin.getMessenger().sendSuccess(player, "Teleport request sent to " + target.getName() + ".");

        target.getScheduler().run(plugin, (task) -> {
            plugin.getMessenger().sendMessage(target, ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has requested for you to teleport to them.");
            plugin.getMessenger().sendMessage(target, "Type " + ChatColor.GREEN + "/tpaccept" + ChatColor.WHITE + " or " + ChatColor.RED + "/tpdeny");
        }, null);

        return true;
    }
}