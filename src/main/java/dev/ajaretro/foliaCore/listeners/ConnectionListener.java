package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {

    private final FoliaCore plugin;

    public ConnectionListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nick = plugin.getChatManager().getNickname(player.getUniqueId());

        if (nick != null) {
            var component = LegacyComponentSerializer.legacyAmpersand().deserialize(nick);
            player.displayName(component);
            player.playerListName(component);
        }
    }
}