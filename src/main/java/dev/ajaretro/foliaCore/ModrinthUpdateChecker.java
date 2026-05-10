package dev.ajaretro.foliaCore;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ModrinthUpdateChecker {

    private static final String PROJECT_SLUG = "foliacore";
    private static final String MODRINTH_URL = "https://api.modrinth.com/v2/project/" + PROJECT_SLUG + "/version";
    private static final String PROJECT_LINK = "https://modrinth.com/plugin/foliacore";
    private static final String USER_AGENT = "ajaretro/FoliaCore/2.5 (contact@ajaretro.dev)";

    private final Plugin plugin;
    private final HttpClient httpClient;
    private final String currentVersion;
    private final Set<UUID> pendingOpNotifications = ConcurrentHashMap.newKeySet();

    private volatile boolean checked;
    private volatile boolean updateAvailable;
    private volatile String latestVersion;

    public ModrinthUpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newHttpClient();
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdates() {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MODRINTH_URL))
                        .header("User-Agent", USER_AGENT)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    plugin.getLogger().warning("Modrinth update check failed: HTTP " + response.statusCode());
                    return;
                }

                JsonElement parsed = JsonParser.parseString(response.body());
                if (!parsed.isJsonArray()) {
                    plugin.getLogger().warning("Modrinth update check failed: unexpected response payload.");
                    return;
                }

                JsonArray versions = parsed.getAsJsonArray();
                if (versions.isEmpty()) {
                    plugin.getLogger().warning("Modrinth update check failed: no versions returned.");
                    return;
                }

                JsonObject latest = versions.get(0).getAsJsonObject();
                String fetchedVersion = latest.has("version_number") ? latest.get("version_number").getAsString() : null;
                this.latestVersion = fetchedVersion;
                this.checked = true;
                this.updateAvailable = fetchedVersion != null && !fetchedVersion.equalsIgnoreCase(currentVersion);

                if (!updateAvailable) {
                    plugin.getLogger().info("FoliaCore is up to date.");
                    pendingOpNotifications.clear();
                    return;
                }

                plugin.getLogger().warning("A new FoliaCore version is available: " + fetchedVersion + " (current: " + currentVersion + "). See " + PROJECT_LINK);
                notifyQueuedOps();
            } catch (IOException | InterruptedException | RuntimeException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                plugin.getLogger().warning("Modrinth update check failed: " + ex.getMessage());
            }
        });
    }

    public void queueOpNotification(Player player) {
        if (player == null || !player.isOp()) {
            return;
        }

        if (checked && updateAvailable) {
            sendOpNotification(player);
            return;
        }

        pendingOpNotifications.add(player.getUniqueId());
    }

    public boolean hasUpdateAvailable() {
        return checked && updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getProjectLink() {
        return PROJECT_LINK;
    }

    private void notifyQueuedOps() {
        if (pendingOpNotifications.isEmpty()) {
            return;
        }

        for (UUID playerId : pendingOpNotifications) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline() && player.isOp()) {
                sendOpNotification(player);
            }
        }

        pendingOpNotifications.clear();
    }

    private void sendOpNotification(Player player) {
        player.getScheduler().run(plugin, task -> player.sendMessage(
            LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&6&lFoliaCore Update &8» &fA new release is available. &7See: &b" + PROJECT_LINK +
                    " &8| &7Current: &f" + getCurrentVersion() +
                    " &8| &7Latest: &f" + (latestVersion == null ? "unknown" : latestVersion)
            )
        ), null);
    }
}