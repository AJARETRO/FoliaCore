package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Warp;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class WarpsCommand implements BasicCommand {

    private final FoliaCore plugin;

    public WarpsCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!sender.hasPermission("foliacore.warps.list")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to list warps.");
            return;
        }

        boolean isAdmin = sender.hasPermission("foliacore.warp.all");

        String warpsList = plugin.getWarpManager().getAllWarps().stream()
                .map(Warp::name)
                .filter(name -> isAdmin || sender.hasPermission("foliacore.warp." + name.toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.WHITE));

        if (warpsList.isEmpty()) {
            plugin.getMessenger().sendMessage(sender, "There are no warps available for you.");
        } else {
            plugin.getMessenger().sendMessage(sender, "Available Warps: " + ChatColor.WHITE + warpsList);
        }
        return;
    }
}