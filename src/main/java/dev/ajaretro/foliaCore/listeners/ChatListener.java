package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.ChatMode;
import dev.ajaretro.foliaCore.managers.ChatManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    private final FoliaCore plugin;
    private final ChatManager chatManager;

    public ChatListener(FoliaCore plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (chatManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessenger().sendError(player, "You are muted and cannot speak.");
            return;
        }

        String rawFormat = chatManager.getChatFormat();
        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(rawFormat
                        .replace("{DISPLAYNAME}", LegacyComponentSerializer.legacyAmpersand().serialize(sourceDisplayName))
                        .replace("{MESSAGE}", LegacyComponentSerializer.legacyAmpersand().serialize(message)));
            }
        });

        if (!chatManager.isChatRangesEnabled()) {
            return;
        }

        String messageString = plugin.getMessenger().componentToString(event.originalMessage());
        String globalPrefix = chatManager.getGlobalPrefix();
        boolean isGlobal = false;

        if (messageString.startsWith(globalPrefix)) {
            String messageWithoutPrefix = messageString.substring(globalPrefix.length()).trim();
            event.message(Component.text(messageWithoutPrefix));
            isGlobal = true;
        }

        ChatMode mode = chatManager.getPlayerChatMode(player.getUniqueId());

        if (mode == ChatMode.GLOBAL || isGlobal) {
            return;
        }

        Collection<Player> viewers = event.viewers().stream()
                .filter(audience -> audience instanceof Player)
                .map(audience -> (Player) audience)
                .collect(Collectors.toList());

        event.viewers().clear();

        if (mode == ChatMode.WORLD) {
            for (Player viewer : viewers) {
                if (viewer.getWorld().equals(player.getWorld())) {
                    event.viewers().add(viewer);
                }
            }
        } else if (mode == ChatMode.REGIONAL) {
            int radius = chatManager.getRegionalRadius();
            for (Player viewer : viewers) {
                if (viewer.getWorld().equals(player.getWorld()) &&
                        viewer.getLocation().distanceSquared(player.getLocation()) <= (radius * radius))
                {
                    event.viewers().add(viewer);
                }
            }
        }

        event.viewers().add(player);
    }
}