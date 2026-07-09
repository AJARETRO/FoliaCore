package dev.ajaretro.foliaCore.tasks;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import java.util.Arrays;

/**
 * Entity cleanup task that runs per-region in Folia.
 * Removes ground items and excess hostile mobs when regional TPS drops below threshold.
 */
public class EntityCleanupTask {

    private final FoliaCore plugin;

    public EntityCleanupTask(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int intervalSeconds = plugin.getConfigManager().getEntityCleanupInterval();
        long intervalTicks = (long) intervalSeconds * 20;
        long intervalMillis = intervalTicks * 50L; // 1 tick = 50ms

        // Run on global async scheduler at fixed rate to check performance
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            cleanupRegionsIfLagging();
        }, intervalMillis, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void cleanupRegionsIfLagging() {
        if (!plugin.getConfigManager().isEntityCleanupEnabled()) {
            return;
        }

        int tpsThreshold = plugin.getConfigManager().getMinimumTpsThreshold();
        long[] tickTimes = Bukkit.getServer().getTickTimes();
        double avgTickTime = calculateAverageTick(tickTimes);
        double tps = Math.min(20.0, 1000.0 / (avgTickTime / 1000000.0));

        // Only cleanup if TPS is low
        if (tps >= tpsThreshold) {
            return;
        }

        plugin.getLogger().warning("Low TPS detected (" + String.format("%.2f", tps) + "). Dispatching region-safe entity cleanups...");

        // Safely dispatch tasks to region threads centered on online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScheduler().run(plugin, (task) -> {
                if (!player.isOnline()) return;

                Location loc = player.getLocation();
                World world = loc.getWorld();
                Chunk centerChunk = loc.getChunk();
                int radius = 3; // Scan 7x7 grid centered around the player's region

                int itemsRemoved = 0;
                int mobsRemoved = 0;

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        int cx = centerChunk.getX() + dx;
                        int cz = centerChunk.getZ() + dz;

                        if (world.isChunkLoaded(cx, cz)) {
                            Chunk targetChunk = world.getChunkAt(cx, cz);
                            for (Entity entity : targetChunk.getEntities()) {
                                if (entity instanceof Item) {
                                    Item item = (Item) entity;
                                    if (item.getTicksLived() > 300) { // 15 seconds
                                        item.remove();
                                        itemsRemoved++;
                                    }
                                } else if (entity instanceof Monster) {
                                    long monsterCount = Arrays.stream(targetChunk.getEntities())
                                            .filter(e -> e instanceof Monster)
                                            .count();
                                    if (monsterCount > 25) { // Prevent entity crowding in active region
                                        entity.remove();
                                        mobsRemoved++;
                                    }
                                }
                            }
                        }
                    }
                }

                if (itemsRemoved > 0 || mobsRemoved > 0) {
                    plugin.getLogger().info("Cleaned region around " + player.getName() + " (" + world.getName() + "): " + itemsRemoved + " items, " + mobsRemoved + " mobs");
                }
            }, null);
        }
    }

    private double calculateAverageTick(long[] tickTimes) {
        if (tickTimes.length == 0) return 0;
        long sum = 0;
        for (long time : tickTimes) {
            sum += time;
        }
        return (double) sum / tickTimes.length;
    }
}
