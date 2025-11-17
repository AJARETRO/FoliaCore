package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.gui.KitGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public KitCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (args.length == 0) {
            KitGUI gui = new KitGUI(plugin, player);
            gui.openGUI();
            return true;
        }

        String kitName = args[0];
        Kit kit = plugin.getKitManager().getKit(kitName);

        if (kit == null) {
            plugin.getMessenger().sendError(player, "A kit with the name '" + ChatColor.GOLD + kitName + ChatColor.RED + "' does not exist.");
            return true;
        }

        plugin.getKitManager().giveKit(player, kit);
        return true;
    }
}