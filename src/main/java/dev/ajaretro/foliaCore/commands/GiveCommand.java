package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public GiveCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.give")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /give <player> <material> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return true;
        }

        Material material;
        try {
            material = Material.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getMessenger().sendError(sender, "Unknown material: " + args[1]);
            return true;
        }

        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Math.min(64, Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                plugin.getMessenger().sendError(sender, "Amount must be a number.");
                return true;
            }
        }

        ItemStack item = new ItemStack(material, amount);
        target.getInventory().addItem(item);

        plugin.getMessenger().sendSuccess(sender, ChatColor.GREEN + "Given " + amount + "x " + ChatColor.GOLD + material.toString() + ChatColor.GREEN + " to " + ChatColor.GOLD + target.getName());
        plugin.getMessenger().sendMessage(target, ChatColor.GOLD + sender.getName() + ChatColor.WHITE + " has given you " + amount + "x " + ChatColor.GOLD + material.toString());

        return true;
    }
}
