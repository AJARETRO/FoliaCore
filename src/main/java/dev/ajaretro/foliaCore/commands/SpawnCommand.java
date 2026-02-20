package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements BasicCommand {

    private final FoliaCore plugin;

    public SpawnCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!player.hasPermission("foliacore.spawn")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return;
        }

        Location spawn = plugin.getTeleportManager().getSpawn();
        if (spawn.getWorld() == null) {
            plugin.getMessenger().sendError(player, "The spawn world is not loaded! Please contact an admin.");
            return;
        }

        plugin.getTeleportManager().startTeleport(player, spawn, "Teleported to spawn.");
        return;
    }
}