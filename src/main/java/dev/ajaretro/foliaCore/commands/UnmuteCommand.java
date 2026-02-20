package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements BasicCommand {

    private final FoliaCore plugin;

    public UnmuteCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!sender.hasPermission("foliacore.unmute")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /unmute <player>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return;
        }

        plugin.getChatManager().unmutePlayer(target.getUniqueId());

        plugin.getMessenger().sendSuccess(sender, target.getName() + " has been unmuted.");

        Player onlineTarget = target.getPlayer();
        if (onlineTarget != null) {
            onlineTarget.getScheduler().run(plugin, (task) -> {
                plugin.getMessenger().sendSuccess(onlineTarget, "You have been unmuted.");
            }, null);
        }

        return;
    }
}