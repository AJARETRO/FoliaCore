package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.ChatMode;
import dev.ajaretro.foliaCore.managers.ChatManager;
import io.papermc.paper.event.player.AsyncChatEvent; // <-- IMPORT THE NEW EVENT
import net.kyori.adventure.text.Component; // <-- We must use Adventure components
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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
    public void onPlayerChat(AsyncChatEvent event) { // <-- USE THE NEW EVENT
        Player player = event.getPlayer();

        if (chatManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessenger().sendError(player, "You are muted and cannot speak.");
            return;
        }

        if (!chatManager.isChatRangesEnabled()) {
            return; // Not enabled, let the server handle it
        }

        // We get the message as a String from the original message component
        String messageString = plugin.getMessenger().componentToString(event.originalMessage());
        String globalPrefix = chatManager.getGlobalPrefix();
        boolean isGlobal = false;

        // Check for global prefix
        if (messageString.startsWith(globalPrefix)) {
            // Re-set the message component without the prefix
            String messageWithoutPrefix = messageString.substring(globalPrefix.length()).trim();
            event.message(Component.text(messageWithoutPrefix));
            isGlobal = true; // This is now a global message
        }

        // Get the player's mode
        ChatMode mode = chatManager.getPlayerChatMode(player.getUniqueId());

        // If the mode is GLOBAL or they used the prefix, don't change viewers
        if (mode == ChatMode.GLOBAL || isGlobal) {
            return; // Let everyone see it
        }

        // --- At this point, it's a ranged chat (WORLD or REGIONAL) ---

        // Get the collection of viewers (audiences)
        Collection<Player> viewers = event.viewers().stream()
                .filter(audience -> audience instanceof Player)
                .map(audience -> (Player) audience)
                .collect(Collectors.toList());

        // Clear the viewers
        event.viewers().clear();

        if (mode == ChatMode.WORLD) {
            // Add only players in the same world
            for (Player viewer : viewers) {
                if (viewer.getWorld().equals(player.getWorld())) {
                    event.viewers().add(viewer);
                }
            }
        } else if (mode == ChatMode.REGIONAL) {
            int radius = chatManager.getRegionalRadius();
            // Add only players in the same world AND within the radius
            for (Player viewer : viewers) {
                if (viewer.getWorld().equals(player.getWorld()) &&
                        viewer.getLocation().distanceSquared(player.getLocation()) <= (radius * radius))
                {
                    event.viewers().add(viewer);
                }
            }
        }

        // Always add the player themself so they can see their own message
        event.viewers().add(player);
    }
}