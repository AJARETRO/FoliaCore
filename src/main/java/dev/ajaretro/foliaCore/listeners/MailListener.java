package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Mail;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class MailListener implements Listener {

    private final FoliaCore plugin;

    public MailListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getScheduler().runDelayed(plugin, (task) -> {
            List<Mail> mailbox = plugin.getChatManager().getMail(player.getUniqueId());
            if (mailbox != null && !mailbox.isEmpty()) {
                plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "You have " + mailbox.size() + " unread mail message(s)! Type /mail read");
            }
        }, null, 20L * 3);
    }
}