package dev.ajaretro.foliaCore.tasks;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Auto-broadcaster that sends configured messages to all players periodically.
 * Uses the global async scheduler for Folia compliance.
 */
public class AutoBroadcasterTask {

    private final FoliaCore plugin;
    private List<String> messages;
    private int currentIndex = 0;
    private long intervalTicks;

    public AutoBroadcasterTask(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        messages = config.getStringList("system.broadcast.messages");

        if (messages == null || messages.isEmpty()) {
            plugin.getLogger().warning("No broadcast messages configured.");
            return;
        }

        long intervalSeconds = plugin.getConfigManager().getAutoBroadcastInterval();
        long intervalTicks = intervalSeconds * 20L;
        long intervalMillis = intervalTicks * 50L;

        // Schedule repeating task (convert ticks to milliseconds)
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            if (messages.isEmpty()) return;

            String message = messages.get(currentIndex);
            Bukkit.broadcast(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(message));

            currentIndex = (currentIndex + 1) % messages.size();
        }, intervalMillis, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

        plugin.getLogger().info("Auto-broadcaster started with interval: " + intervalSeconds + " second(s)");
    }
}
