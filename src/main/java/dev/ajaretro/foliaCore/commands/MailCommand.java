package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Mail;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MailCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");

    public MailCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "send":
                handleSend(player, args);
                break;
            case "read":
                handleRead(player);
                break;
            case "clear":
                handleClear(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "--- FoliaCore Mail Help ---");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/mail send <player> <message...>" + ChatColor.WHITE + " - Send offline mail");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/mail read" + ChatColor.WHITE + " - Read your mail");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/mail clear" + ChatColor.WHITE + " - Clear your mailbox");
    }

    private void handleSend(Player player, String[] args) {
        if (!player.hasPermission("foliacore.mail.send")) {
            plugin.getMessenger().sendError(player, "You do not have permission to send mail.");
            return;
        }

        if (args.length < 3) {
            plugin.getMessenger().sendError(player, "Usage: /mail send <player> <message...>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }

        if (plugin.getChatManager().isBlocked(player.getUniqueId(), target.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You cannot send mail to this player as they have you blocked.");
            return;
        }

        if (plugin.getChatManager().isBlocked(target.getUniqueId(), player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You have this player blocked. Use /unblock to send them mail.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        plugin.getChatManager().sendMail(player.getUniqueId(), target.getUniqueId(), message);
        plugin.getMessenger().sendSuccess(player, "Mail sent to " + target.getName() + ".");

        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            if (onlineTarget != null) {
                onlineTarget.getScheduler().run(plugin, (task) -> {
                    plugin.getMessenger().sendMessage(onlineTarget, ChatColor.GREEN + "You have received new mail from " + player.getName() + "! Type /mail read");
                }, null);
            }
        }
    }

    private void handleRead(Player player) {
        if (!player.hasPermission("foliacore.mail.read")) {
            plugin.getMessenger().sendError(player, "You do not have permission to read mail.");
            return;
        }

        List<Mail> mailbox = plugin.getChatManager().getMail(player.getUniqueId());
        if (mailbox == null || mailbox.isEmpty()) {
            plugin.getMessenger().sendMessage(player, "Your mailbox is empty.");
            return;
        }

        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "--- Your Mailbox ---");
        for (int i = 0; i < mailbox.size(); i++) {
            Mail mail = mailbox.get(i);
            OfflinePlayer sender = Bukkit.getOfflinePlayer(mail.sender());
            String senderName = sender.getName() != null ? sender.getName() : "Unknown";
            String date = dateFormat.format(new Date(mail.timestamp()));

            player.sendMessage(ChatColor.GOLD + "Mail #" + (i + 1) + " From: " + ChatColor.WHITE + senderName + ChatColor.GRAY + " (" + date + ")");
            player.sendMessage(ChatColor.GRAY + "  > " + ChatColor.WHITE + mail.message());
        }
    }

    private void handleClear(Player player) {
        if (!player.hasPermission("foliacore.mail.clear")) {
            plugin.getMessenger().sendError(player, "You do not have permission to clear mail.");
            return;
        }

        List<Mail> mailbox = plugin.getChatManager().getMail(player.getUniqueId());
        if (mailbox == null || mailbox.isEmpty()) {
            plugin.getMessenger().sendMessage(player, "Your mailbox is already empty.");
            return;
        }

        plugin.getChatManager().clearMail(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Your mailbox has been cleared.");
    }
}