package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnblockCommand implements BasicCommand {

    private final FoliaCore plugin;

    public UnblockCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!sender.hasPermission("foliacore.unblock")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /unblock <player>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return;
        }

        Player player = (Player) sender;
        plugin.getChatManager().unblockPlayer(player.getUniqueId(), target.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have unblocked " + target.getName() + ".");

        return;
    }
}