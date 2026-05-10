package dev.ajaretro.foliaCore.tasks;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Auto-broadcaster that sends messages at configurable intervals.
 * Uses global async scheduler for Folia compatibility.
 */
public class AutoBroadcaster {

    private final FoliaCore plugin;
    private List<String> messages;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AutoBroadcaster(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            File autobroadcastFile = new File(plugin.getDataFolder(), "autobroadcasts.yml");
            if (!autobroadcastFile.exists()) {
                createDefaultAutobroadcasts(autobroadcastFile);
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(autobroadcastFile);
            messages = config.getStringList("messages");

            if (messages.isEmpty()) {
                plugin.getLogger().warning("No messages found in autobroadcasts.yml");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load autobroadcasts.yml");
            e.printStackTrace();
        }
    }

    private void createDefaultAutobroadcasts(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileConfiguration config = new YamlConfiguration();
            config.set("messages", List.of(
                    "&eWelcome to the server!",
                    "&eCheck out /warps for teleport locations.",
                    "&eUse /help for a list of commands.",
                    "&eHave fun and be respectful to other players!"
            ));
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create default autobroadcasts.yml");
        }
    }

    public void start() {
        if (messages == null || messages.isEmpty()) {
            plugin.getLogger().info("AutoBroadcaster: No messages to broadcast.");
            return;
        }

        int intervalSeconds = plugin.getConfigManager().getAutoBroadcastInterval();
        long intervalTicks = (long) intervalSeconds * 20L;
        long intervalMillis = intervalTicks * 50L;

        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            broadcast();
        }, intervalMillis, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

        plugin.getLogger().info("AutoBroadcaster started with " + messages.size() + " messages.");
    }

    private void broadcast() {
        if (messages.isEmpty()) return;

        int index = currentIndex.getAndIncrement() % messages.size();
        String message = messages.get(index);

        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
}
