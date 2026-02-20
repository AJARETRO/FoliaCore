package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.command.brigadier.BasicCommand;
import net.kyori.adventure.text.Component;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements BasicCommand {

    private final FoliaCore plugin;

    public NickCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "Only players can set a nickname.");
            return;
        }

        if (!player.hasPermission("foliacore.nick")) {
            plugin.getMessenger().sendError(player, "You do not have permission to change your nickname.");
            return;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /nick <name|off>");
            return;
        }

        String input = args[0];

        if (input.equalsIgnoreCase("off") || input.equalsIgnoreCase("reset")) {
            plugin.getChatManager().removeNickname(player.getUniqueId());
            player.displayName(Component.text(player.getName()));
            if (player.hasPermission("foliacore.nick.color")) {
                player.playerListName(Component.text(player.getName()));
            }
            plugin.getMessenger().sendSuccess(player, "Nickname reset.");
            return;
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
        return;
    }
}