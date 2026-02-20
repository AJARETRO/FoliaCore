package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements BasicCommand {

    private final FoliaCore plugin;

    public ReplyCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!sender.hasPermission("foliacore.reply")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID targetUUID = plugin.getChatManager().getReplyTarget(player.getUniqueId());

        if (targetUUID == null) {
            plugin.getMessenger().sendError(sender, "You have no one to reply to.");
            return;
        }

        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "The player you are replying to is no longer online.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /reply <message...>");
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
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
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