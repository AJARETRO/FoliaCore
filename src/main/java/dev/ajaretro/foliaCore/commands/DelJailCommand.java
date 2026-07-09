package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelJailCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public DelJailCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isJailsEnabled()) {
            plugin.getMessenger().sendError(sender, "Jails module is disabled.");
            return true;
        }

        if (!sender.hasPermission("foliacore.deljail")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendError(sender, "Usage: /deljail <name>");
            return true;
        }

        String name = args[0].toLowerCase();
        if (plugin.getJailManager().getJail(name) == null) {
            plugin.getMessenger().sendError(sender, "Jail '" + name + "' does not exist.");
            return true;
        }

        plugin.getJailManager().deleteJail(name);
        plugin.getMessenger().sendSuccess(sender, "Jail '" + name + "' deleted successfully.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.deljail")) {
            List<String> list = new ArrayList<>();
            for (String jailName : plugin.getJailManager().getJails().keySet()) {
                if (jailName.startsWith(args[0].toLowerCase())) {
                    list.add(jailName);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
