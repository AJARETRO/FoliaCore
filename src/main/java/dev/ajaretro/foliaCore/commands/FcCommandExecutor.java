package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

/**
 * Command proxy executor routing /fc <command> and /cmi <command> to their respective
 * FoliaCore command execution handlers.
 */
public class FcCommandExecutor implements CommandExecutor {

    private final FoliaCore plugin;

    public FcCommandExecutor(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Command Proxy ===");
            plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Use " + ChatColor.WHITE + "/" + label + " <command> [args...]" + ChatColor.GRAY + " to execute a subcommand.");
            return true;
        }

        String subCommandName = args[0].toLowerCase(Locale.ROOT);
        CommandExecutor executor = plugin.getCommandExecutor(subCommandName);
        if (executor == null) {
            plugin.getMessenger().sendError(sender, "Unknown subcommand: " + subCommandName);
            return true;
        }

        // Shift arguments
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        // Execute proxy command
        return plugin.executeCommandProxy(subCommandName, executor, sender, subArgs);
    }
}
