package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays regionalized status for Folia.
 * Uses real API metrics and avoids reporting fake zero-values when not available.
 */
public class StatusCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public StatusCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.status")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        displayStatus(sender, args);

        return true;
    }

    private void displayStatus(CommandSender sender, String[] args) {
        boolean compact = args.length > 0 && args[0].equalsIgnoreCase("compact");

        Double tps1m = readServerTps1m();
        Double mspt = readAverageTickTime(Bukkit.getServer());
        List<RegionSnapshot> activeRegions = collectActiveRegions();

        plugin.getMessenger().sendMessage(sender, "");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Regional Status ===");
        plugin.getMessenger().sendMessage(sender,
                ChatColor.GRAY + "Online: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size()
                        + ChatColor.DARK_GRAY + " | "
                        + ChatColor.GRAY + "Worlds: " + ChatColor.WHITE + Bukkit.getWorlds().size());
        plugin.getMessenger().sendMessage(sender,
                ChatColor.GRAY + "TPS (1m): " + colorizeTps(tps1m) + formatMetric(tps1m, "")
                        + ChatColor.DARK_GRAY + " | "
                        + ChatColor.GRAY + "MSPT: " + colorizeMspt(mspt) + formatMetric(mspt, " ms"));

        if (plugin.getConfigManager().isStatusShowWorldSummary()) {
            for (World world : Bukkit.getWorlds()) {
                Double worldMspt = readAverageTickTime(world);
                plugin.getMessenger().sendMessage(sender,
                        ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + world.getName()
                                + ChatColor.DARK_GRAY + " | "
                                + ChatColor.GRAY + "Players: " + ChatColor.WHITE + world.getPlayers().size()
                                + ChatColor.DARK_GRAY + " | "
                                + ChatColor.GRAY + "MSPT: " + colorizeMspt(worldMspt) + formatMetric(worldMspt, " ms"));
            }
        }

        if (plugin.getConfigManager().isStatusShowRegionDetails() && !compact) {
            plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "Active Regions (player-tracked):");
            if (activeRegions.isEmpty()) {
                plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + " - No active player regions currently tracked.");
            } else {
                int max = Math.min(plugin.getConfigManager().getStatusMaxRegions(), activeRegions.size());
                for (int i = 0; i < max; i++) {
                    RegionSnapshot region = activeRegions.get(i);
                    plugin.getMessenger().sendMessage(sender,
                            ChatColor.DARK_AQUA + " #" + (i + 1)
                                    + ChatColor.GRAY + " " + region.worldName
                                    + ChatColor.DARK_GRAY + " | "
                                    + ChatColor.GRAY + "Region: " + ChatColor.WHITE + "(" + region.regionX + ", " + region.regionZ + ")"
                                    + ChatColor.DARK_GRAY + " | "
                                    + ChatColor.GRAY + "Players: " + ChatColor.WHITE + region.players
                                    + ChatColor.DARK_GRAY + " | "
                                    + ChatColor.GRAY + "Center: " + ChatColor.WHITE + region.centerX + ", " + region.centerZ);
                }
            }
            plugin.getMessenger().sendMessage(sender, ChatColor.DARK_GRAY + "Use /status compact for a compact view.");
        }

        plugin.getMessenger().sendMessage(sender, "");
    }

    private List<RegionSnapshot> collectActiveRegions() {
        int span = plugin.getConfigManager().getStatusRegionChunkSpan();
        Map<String, RegionSnapshot> byRegion = new HashMap<>();

        Bukkit.getOnlinePlayers().forEach(player -> {
            int chunkX = player.getLocation().getBlockX() >> 4;
            int chunkZ = player.getLocation().getBlockZ() >> 4;
            int regionX = Math.floorDiv(chunkX, span);
            int regionZ = Math.floorDiv(chunkZ, span);
            String key = player.getWorld().getName() + ":" + regionX + ":" + regionZ;

            RegionSnapshot snapshot = byRegion.computeIfAbsent(key, k -> {
                int centerChunkX = (regionX * span) + (span / 2);
                int centerChunkZ = (regionZ * span) + (span / 2);
                return new RegionSnapshot(
                        player.getWorld().getName(),
                        regionX,
                        regionZ,
                        (centerChunkX * 16) + 8,
                        (centerChunkZ * 16) + 8
                );
            });
            snapshot.players++;
        });

        List<RegionSnapshot> snapshots = new ArrayList<>(byRegion.values());
        snapshots.sort(Comparator.comparingInt((RegionSnapshot r) -> r.players).reversed());
        return snapshots;
    }

    private Double readServerTps1m() {
        try {
            Method getTps = Bukkit.getServer().getClass().getMethod("getTPS");
            Object value = getTps.invoke(Bukkit.getServer());
            if (value instanceof double[] tps && tps.length > 0 && tps[0] > 0) {
                return tps[0];
            }
        } catch (Exception ignored) {
            // If unavailable on this implementation, show N/A instead of fake values.
        }
        return null;
    }

    private Double readAverageTickTime(Object target) {
        try {
            Method msptMethod = target.getClass().getMethod("getAverageTickTime");
            Object value = msptMethod.invoke(target);
            if (value instanceof Number number) {
                double result = number.doubleValue();
                return result > 0 ? result : null;
            }
        } catch (Exception ignored) {
            // If unavailable on this implementation, show N/A instead of fake values.
        }
        return null;
    }

    private String formatMetric(Double value, String suffix) {
        if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
            return ChatColor.DARK_GRAY + "N/A";
        }
        return ChatColor.WHITE + String.format("%.2f", value) + suffix;
    }

    private String colorizeTps(Double tps) {
        if (tps == null) return ChatColor.DARK_GRAY.toString();
        if (tps >= 19.0) return ChatColor.GREEN.toString();
        if (tps >= 15.0) return ChatColor.YELLOW.toString();
        return ChatColor.RED.toString();
    }

    private String colorizeMspt(Double mspt) {
        if (mspt == null) return ChatColor.DARK_GRAY.toString();
        if (mspt <= 50.0) return ChatColor.GREEN.toString();
        if (mspt <= 75.0) return ChatColor.YELLOW.toString();
        return ChatColor.RED.toString();
    }

    private static final class RegionSnapshot {
        private final String worldName;
        private final int regionX;
        private final int regionZ;
        private final int centerX;
        private final int centerZ;
        private int players;

        private RegionSnapshot(String worldName, int regionX, int regionZ, int centerX, int centerZ) {
            this.worldName = worldName;
            this.regionX = regionX;
            this.regionZ = regionZ;
            this.centerX = centerX;
            this.centerZ = centerZ;
        }
    }
}
