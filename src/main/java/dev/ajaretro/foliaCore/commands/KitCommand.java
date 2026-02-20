package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.gui.KitGUI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements BasicCommand {

    private final FoliaCore plugin;

    public KitCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return;
        }

        if (args.length == 0) {
            KitGUI gui = new KitGUI(plugin, player);
            gui.openGUI();
            return;
        }

        String kitName = args[0];
        Kit kit = plugin.getKitManager().getKit(kitName);

        if (kit == null) {
            plugin.getMessenger().sendError(player, "A kit with the name '" + ChatColor.GOLD + kitName + ChatColor.RED + "' does not exist.");
            return;
        }

        plugin.getKitManager().giveKit(player, kit);
        return;
    }
}