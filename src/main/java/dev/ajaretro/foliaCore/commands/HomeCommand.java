package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Home;
import dev.ajaretro.foliaCore.managers.TeleportManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements BasicCommand {

    private final FoliaCore plugin;
    private final TeleportManager tm;

    public HomeCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeleportManager();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (!player.hasPermission("foliacore.home")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return;
        }

        String homeName = (args.length == 0) ? "home" : args[0];

        Home home = tm.getHome(player.getUniqueId(), homeName);
        if (home == null) {
            plugin.getMessenger().sendError(player, "Home '" + ChatColor.GOLD + homeName + ChatColor.RED + "' not found.");
            return;
        }

        Location location = home.toLocation();
        if (location == null) {
            plugin.getMessenger().sendError(player, "The world for this home is not loaded or does not exist!");
            return;
        }

        String successMessage = "Teleported to '" + ChatColor.GOLD + homeName + ChatColor.GREEN + "'.";
        tm.startTeleport(player, location, successMessage);
        return;
    }
}