package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand implements BasicCommand {

    private final FoliaCore plugin;

    public MsgCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!sender.hasPermission("foliacore.msg")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /msg <player> <message...>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return;
        }

        Player player = (Player) sender;
        if (player.equals(target)) {
            plugin.getMessenger().sendError(sender, "You cannot message yourself.");
            return;
        }

        if (plugin.getChatManager().isBlocked(player.getUniqueId(), target.getUniqueId())) {
            plugin.getMessenger().sendError(sender, "You cannot message this player as they have you blocked.");
            return;
        }

        if (plugin.getChatManager().isBlocked(target.getUniqueId(), player.getUniqueId())) {
            plugin.getMessenger().sendError(sender, "You have this player blocked. Use /unblock to message them.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        String senderFormat = ChatColor.GRAY + "" + ChatColor.ITALIC + "You -> " + target.getName() + ": " + message;
        String targetFormat = ChatColor.GRAY + "" + ChatColor.ITALIC + player.getName() + " -> You: " + message;

        player.sendMessage(senderFormat);

        target.getScheduler().run(plugin, (scheduledTask) -> {
            target.sendMessage(targetFormat);
        }, null);

        plugin.getChatManager().setReplyTarget(player.getUniqueId(), target.getUniqueId());
        plugin.getChatManager().setReplyTarget(target.getUniqueId(), player.getUniqueId());

        return;
    }
}