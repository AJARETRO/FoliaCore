package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.ChatMode;
import dev.ajaretro.foliaCore.managers.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final FoliaCore plugin;
    private final ChatManager chatManager;

    public ChatListener(FoliaCore plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (chatManager.isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessenger().sendError(player, "You are muted and cannot speak.");
            return;
        }

        if (!chatManager.isChatRangesEnabled()) {
            return;
        }

        String message = event.getMessage();
        String globalPrefix = chatManager.getGlobalPrefix();

        if (message.startsWith(globalPrefix)) {
            event.setMessage(message.substring(globalPrefix.length()).trim());
            return;
        }

        ChatMode mode = chatManager.getPlayerChatMode(player.getUniqueId());
        if (mode == ChatMode.GLOBAL) {
            return;
        }

        event.getRecipients().clear();

        if (mode == ChatMode.WORLD) {
            event.getRecipients().addAll(player.getWorld().getPlayers());
        } else if (mode == ChatMode.REGIONAL) {
            int radius = chatManager.getRegionalRadius();
            event.getRecipients().addAll(player.getWorld().getNearbyPlayers(player.getLocation(), radius));
            event.getRecipients().add(player);
        }
    }
}