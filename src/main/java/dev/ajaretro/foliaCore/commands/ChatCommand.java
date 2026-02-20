package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.ChatMode;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements BasicCommand {

    private final FoliaCore plugin;

    public ChatCommand(FoliaCore plugin) {
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
            ChatMode currentMode = plugin.getChatManager().getPlayerChatMode(player.getUniqueId());
            plugin.getMessenger().sendMessage(player, "Your current chat mode is: " + ChatColor.GOLD + currentMode.name());
            plugin.getMessenger().sendMessage(player, "Usage: /chat <global|world|regional>");
            return;
        }

        String modeString = args[0].toLowerCase();
        ChatMode mode;

        switch (modeString) {
            case "g":
            case "global":
                if (!player.hasPermission("foliacore.chat.global")) {
                    plugin.getMessenger().sendError(player, "You do not have permission to use global chat.");
                    return;
                }
                mode = ChatMode.GLOBAL;
                break;
            case "w":
            case "world":
                if (!player.hasPermission("foliacore.chat.world")) {
                    plugin.getMessenger().sendError(player, "You do not have permission to use world chat.");
                    return;
                }
                mode = ChatMode.WORLD;
                break;
            case "r":
            case "regional":
            case "local":
                if (!player.hasPermission("foliacore.chat.regional")) {
                    plugin.getMessenger().sendError(player, "You do not have permission to use regional chat.");
                    return;
                }
                mode = ChatMode.REGIONAL;
                break;
            default:
                plugin.getMessenger().sendError(player, "Unknown chat mode. Use: global, world, or regional.");
                return;
        }

        plugin.getChatManager().setPlayerChatMode(player.getUniqueId(), mode);
        plugin.getMessenger().sendSuccess(player, "Your chat mode has been set to " + ChatColor.GOLD + mode.name());
    }
}