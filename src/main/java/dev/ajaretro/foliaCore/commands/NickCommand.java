package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public NickCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "Only players can set a nickname.");
            return true;
        }

        if (!player.hasPermission("foliacore.nick")) {
            plugin.getMessenger().sendError(player, "You do not have permission to change your nickname.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /nick <name|off>");
            return true;
        }

        String input = args[0];

        if (input.equalsIgnoreCase("off") || input.equalsIgnoreCase("reset")) {
            plugin.getChatManager().removeNickname(player.getUniqueId());
            player.displayName(Component.text(player.getName()));
            if (player.hasPermission("foliacore.nick.color")) {
                player.playerListName(Component.text(player.getName()));
            }
            plugin.getMessenger().sendSuccess(player, "Nickname reset.");
            return true;
        }

        if (!player.hasPermission("foliacore.nick.color")) {
            input = input.replaceAll("&[0-9a-fk-or]", "");
        }

        String formattedNick = LegacyComponentSerializer.legacyAmpersand().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(input)
        );

        plugin.getChatManager().setNickname(player.getUniqueId(), formattedNick);

        Component nickComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(formattedNick);
        player.displayName(nickComponent);
        player.playerListName(nickComponent);

        plugin.getMessenger().sendSuccess(player, "Your nickname is now: " + formattedNick);
        return true;
    }
}