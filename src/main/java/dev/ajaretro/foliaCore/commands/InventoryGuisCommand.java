package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryGuisCommand implements CommandExecutor {
    private final FoliaCore plugin;

    public InventoryGuisCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        if (!player.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        Location loc = player.getLocation();
        
        // Execute on the player's Folia region thread
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            switch (cmd) {
                case "anvil":
                    player.openAnvil(loc, true);
                    break;
                case "grindstone":
                    player.openGrindstone(loc, true);
                    break;
                case "loom":
                    player.openLoom(loc, true);
                    break;
                case "smithingtable":
                    player.openSmithingTable(loc, true);
                    break;
                case "stonecutter":
                    player.openStonecutter(loc, true);
                    break;
                case "cartographytable":
                    player.openCartographyTable(loc, true);
                    break;
            }
        });

        return true;
    }
}
