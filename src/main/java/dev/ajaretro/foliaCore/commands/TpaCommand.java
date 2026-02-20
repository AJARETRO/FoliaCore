package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements BasicCommand {

    private final FoliaCore plugin;

    public TpaCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!player.hasPermission("foliacore.tpa")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /tpa <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is not online.");
            return;
        }

        if (player.equals(target)) {
            plugin.getMessenger().sendError(player, "You cannot send a teleport request to yourself.");
            return;
        }

        plugin.getTeleportManager().createTpaRequest(player.getUniqueId(), target.getUniqueId(), TeleportManager.TpaType.TPA);
        plugin.getMessenger().sendSuccess(player, "Teleport request sent to " + target.getName() + ".");

        target.getScheduler().run(plugin, (task) -> {
            plugin.getMessenger().sendMessage(target, ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has requested to teleport to you.");
            plugin.getMessenger().sendMessage(target, "Type " + ChatColor.GREEN + "/tpaccept" + ChatColor.WHITE + " or " + ChatColor.RED + "/tpdeny");
        }, null);

        return;
    }
}