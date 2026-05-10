package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class EnderchestCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public EnderchestCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessenger().sendError(sender, "Usage: /enderchest [player]");
                return true;
            }
            player = (Player) sender;
            if (!player.hasPermission("foliacore.enderchest")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
        } else {
            if (!sender.hasPermission("foliacore.enderchest.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
            if (player == null || !player.isOnline()) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
                return true;
            }
        }

        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            senderPlayer.openInventory(player.getEnderChest());
            if (player.equals(senderPlayer)) {
                plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Opened your enderchest.");
            } else {
                plugin.getMessenger().sendSuccess(sender, "Viewing " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + "'s enderchest.");
            }
        }

        return true;
    }
}
