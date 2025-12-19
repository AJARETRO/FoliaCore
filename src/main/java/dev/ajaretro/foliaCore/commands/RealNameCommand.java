package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.Map;

public class RealNameCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public RealNameCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.realname")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /realname <nickname>");
            return true;
        }

        String targetNick = args[0].toLowerCase();
        Map<UUID, String> nicks = plugin.getChatManager().getAllNicknames();

        for (Map.Entry<UUID, String> entry : nicks.entrySet()) {
            String cleanNick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', entry.getValue())).toLowerCase();

            if (cleanNick.contains(targetNick)) {
                Player target = Bukkit.getPlayer(entry.getKey());
                String realName = (target != null) ? target.getName() : Bukkit.getOfflinePlayer(entry.getKey()).getName();

                plugin.getMessenger().sendMessage(sender, "Nickname: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', entry.getValue()));
                plugin.getMessenger().sendMessage(sender, "Real Name: " + ChatColor.GOLD + realName);
                return true;
            }
        }

        plugin.getMessenger().sendError(sender, "No player found with that nickname.");
        return true;
    }
}