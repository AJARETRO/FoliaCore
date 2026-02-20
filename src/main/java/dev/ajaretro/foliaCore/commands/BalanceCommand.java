package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.EconomyManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final EconomyManager eco;

    public BalanceCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.eco = plugin.getEconomyManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!eco.hasEconomy()) {
            plugin.getMessenger().sendError(sender, "Economy features are disabled. Please install Vault and an economy plugin.");
            return;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                plugin.getMessenger().sendError(sender, "Only players can check their own balance.");
                return;
            }
            if (!player.hasPermission("foliacore.balance.self")) {
                plugin.getMessenger().sendError(player, "You do not have permission to check your balance.");
                return;
            }

            double balance = eco.getBalance(player);
            plugin.getMessenger().sendMessage(player, "Your balance: " + ChatColor.GOLD + eco.format(balance));
        } else {
            if (!sender.hasPermission("foliacore.balance.other")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to check other players' balances.");
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                plugin.getMessenger().sendError(sender, "Player not found.");
                return;
            }

            double balance = eco.getBalance(target);
            plugin.getMessenger().sendMessage(sender, target.getName() + "'s balance: " + ChatColor.GOLD + eco.format(balance));
        }
    }
}