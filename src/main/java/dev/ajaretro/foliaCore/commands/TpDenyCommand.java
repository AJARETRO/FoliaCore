package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public TpDenyCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.tpdeny")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        TeleportManager.TeleportRequest request = tm.getTpaRequest(player.getUniqueId());
        if (request == null) {
            plugin.getMessenger().sendError(player, "You have no pending teleport requests.");
            return true;
        }

        tm.removeTpaRequest(player.getUniqueId());

        plugin.getMessenger().sendSuccess(player, "Teleport request denied.");

        Player requester = Bukkit.getPlayer(request.requester());
        if (requester != null && requester.isOnline()) {
            requester.getScheduler().run(plugin, (task) -> {
                plugin.getMessenger().sendError(requester, player.getName() + " denied your teleport request.");
            }, null);
        }

        return true;
    }
}