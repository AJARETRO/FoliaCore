package dev.ajaretro.foliaCore.tasks;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

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

        // Run on global async scheduler at fixed rate (interval converted to ms)
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            cleanupRegion();
        }, intervalMillis, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void cleanupRegion() {
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

        plugin.getLogger().info("Low TPS detected (" + String.format("%.2f", tps) + "). Running entity cleanup...");

        for (org.bukkit.World world : Bukkit.getWorlds()) {
            cleanupWorld(world);
        }
    }

    private void cleanupWorld(org.bukkit.World world) {
        int itemsRemoved = 0;
        int mobsRemoved = 0;

        for (Entity entity : world.getEntities()) {
            // Remove ground items
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (item.getTicksLived() > 300) { // 15 seconds
                    item.remove();
                    itemsRemoved++;
                }
            }
            // Remove excess hostile mobs
            else if (entity instanceof Monster) {
                // Keep friendly mobs, remove some hostile ones
                if (world.getEntities().stream().filter(e -> e instanceof Monster).count() > 100) {
                    entity.remove();
                    mobsRemoved++;
                }
            }
        }

        if (itemsRemoved > 0 || mobsRemoved > 0) {
            plugin.getLogger().info("Cleaned " + world.getName() + ": " + itemsRemoved + " items, " + mobsRemoved + " mobs");
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
