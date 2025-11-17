package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public SpawnCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (!player.hasPermission("foliacore.spawn")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        Location spawn = plugin.getTeleportManager().getSpawn();
        if (spawn.getWorld() == null) {
            plugin.getMessenger().sendError(player, "The spawn world is not loaded! Please contact an admin.");
            return true;
        }

        plugin.getTeleportManager().startTeleport(player, spawn, "Teleported to spawn.");
        return true;
    }
}