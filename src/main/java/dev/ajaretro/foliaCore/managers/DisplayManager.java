package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles animated tab header/footer and animated sidebars.
 * Inspired by top TAB plugins while remaining Folia-safe and lightweight.
 */
public class DisplayManager {

    private static final String TAB_METHODS_PATH = "tab.methods";
    private static final String SIDEBAR_METHODS_PATH = "sidebar.methods";

    private final FoliaCore plugin;
    private final Map<String, Integer> frameIndices = new ConcurrentHashMap<>();
    private final Map<UUID, Scoreboard> playerBoards = new ConcurrentHashMap<>();

    private boolean placeholderApiAvailable;
    private Method setPlaceholdersMethod;

    public DisplayManager(FoliaCore plugin) {
        this.plugin = plugin;
        detectPlaceholderApi();
    }

    public void start() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        boolean tabEnabled = plugin.getConfigManager().isTabEnabled() && config.getBoolean("tab.enabled", true);
        boolean sidebarEnabled = plugin.getConfigManager().isSidebarEnabled() && config.getBoolean("sidebar.enabled", true);
        if (!tabEnabled && !sidebarEnabled) {
            return;
        }

        long tabTicks = Math.max(1L, config.getLong("tab.update-interval-ticks", 20L));
        long sidebarTicks = Math.max(1L, config.getLong("sidebar.update-interval-ticks", 20L));
        long updateTicks = tabEnabled && sidebarEnabled ? Math.min(tabTicks, sidebarTicks) : (tabEnabled ? tabTicks : sidebarTicks);
        long updateMillis = updateTicks * 50L;

        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> tickDisplays(), updateMillis, updateMillis, TimeUnit.MILLISECONDS);
        plugin.getLogger().info("Display manager started. Tab/sidebar refresh every " + updateTicks + " tick(s).");
    }

    public void onPlayerJoin(Player player) {
        updateForPlayer(player);
    }

    public void onPlayerQuit(UUID playerId) {
        playerBoards.remove(playerId);
    }

    private void tickDisplays() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScheduler().run(plugin, task -> updateForPlayer(player), null);
        }
    }

    private void updateForPlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }

        updateTab(player);
        updateSidebar(player);
    }

    private void updateTab(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        if (!plugin.getConfigManager().isTabEnabled() || !config.getBoolean("tab.enabled", true)) {
            player.sendPlayerListHeaderAndFooter(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(""),
                    LegacyComponentSerializer.legacyAmpersand().deserialize("")
            );
            return;
        }

        String headerMethod = config.getString("tab.header-method", "method-1");
        String footerMethod = config.getString("tab.footer-method", "method-2");

        String header = resolveFrame(TAB_METHODS_PATH, headerMethod);
        String footer = resolveFrame(TAB_METHODS_PATH, footerMethod);

        header = applyPlaceholders(player, header);
        footer = applyPlaceholders(player, footer);

        player.sendPlayerListHeaderAndFooter(
                LegacyComponentSerializer.legacyAmpersand().deserialize(header),
                LegacyComponentSerializer.legacyAmpersand().deserialize(footer)
        );
    }

    private void updateSidebar(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        if (!plugin.getConfigManager().isSidebarEnabled() || !config.getBoolean("sidebar.enabled", true)) {
            playerBoards.remove(player.getUniqueId());
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }

        Scoreboard board = playerBoards.computeIfAbsent(player.getUniqueId(), id -> Bukkit.getScoreboardManager().getNewScoreboard());
        Objective objective = board.getObjective("foliaSidebar");

        if (objective == null) {
            objective = board.registerNewObjective("foliaSidebar", "dummy", "FoliaCore");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        String titleMethod = config.getString("sidebar.title-method", "method-1");
        String title = applyPlaceholders(player, resolveFrame(SIDEBAR_METHODS_PATH, titleMethod));
        objective.setDisplayName(color(title));

        clearSidebarTeams(board);

        List<String> lineMethods = config.getStringList("sidebar.line-methods");
        if (lineMethods == null || lineMethods.isEmpty()) {
            lineMethods = List.of("method-1", "method-2", "method-3");
        }

        int score = Math.min(15, lineMethods.size());
        int entryIndex = 0;
        for (String lineMethod : lineMethods) {
            if (score <= 0) {
                break;
            }

            String line = applyPlaceholders(player, resolveFrame(SIDEBAR_METHODS_PATH, lineMethod));
            String entry = uniqueEntry(entryIndex++);
            Team lineTeam = board.registerNewTeam("line_" + entryIndex);
            lineTeam.addEntry(entry);
            lineTeam.prefix(LegacyComponentSerializer.legacyAmpersand().deserialize(truncate(line, 64)));
            objective.getScore(entry).setScore(score--);
        }

        player.setScoreboard(board);
    }

    private void clearSidebarTeams(Scoreboard board) {
        for (String entry : new ArrayList<>(board.getEntries())) {
            board.resetScores(entry);
        }

        for (Team team : new ArrayList<>(board.getTeams())) {
            if (team.getName().startsWith("line_")) {
                team.unregister();
            }
        }
    }

    private String resolveFrame(String methodsPath, String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return "";
        }

        FileConfiguration config = plugin.getConfigManager().getConfig();
        List<String> frames = config.getStringList(methodsPath + "." + methodName);
        if (frames == null || frames.isEmpty()) {
            return "";
        }

        int frame = frameIndices.getOrDefault(methodsPath + ":" + methodName, 0);
        String value = frames.get(frame % frames.size());
        frameIndices.put(methodsPath + ":" + methodName, (frame + 1) % frames.size());
        return value;
    }

    private void detectPlaceholderApi() {
        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            setPlaceholdersMethod = placeholderApiClass.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class);
            placeholderApiAvailable = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        } catch (Exception ignored) {
            placeholderApiAvailable = false;
            setPlaceholdersMethod = null;
        }
    }

    private String applyPlaceholders(Player player, String text) {
        String value = text == null ? "" : text;

        value = value.replace("%player_name%", player.getName())
                .replace("%world_name%", player.getWorld().getName())
                .replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                .replace("%player_ping%", String.valueOf(player.getPing()))
                .replace("%x%", String.valueOf(player.getLocation().getBlockX()))
                .replace("%y%", String.valueOf(player.getLocation().getBlockY()))
                .replace("%z%", String.valueOf(player.getLocation().getBlockZ()));

        double tps = Bukkit.getTPS().length > 0 ? Bukkit.getTPS()[0] : 20.0;
        value = value.replace("%server_tps%", String.format("%.2f", tps));

        if (placeholderApiAvailable && setPlaceholdersMethod != null) {
            try {
                value = (String) setPlaceholdersMethod.invoke(null, player, value);
            } catch (Exception ignored) {
                // If PlaceholderAPI fails, keep built-in placeholders only.
            }
        }

        return value;
    }

    private String uniqueEntry(int index) {
        ChatColor[] colors = ChatColor.values();
        ChatColor color = colors[index % colors.length];
        return color.toString() + ChatColor.RESET;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
