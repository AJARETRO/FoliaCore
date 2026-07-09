package dev.ajaretro.foliaCore.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ajaretro.foliaCore.FoliaCore;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * Lightweight built-in Discord client utilizing Java HTTP Client, WebSockets, and GSON.
 * Handles chat linking, message gateways, and verification codes in a thread-safe manner.
 */
public class DiscordManager {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, String> mcToDiscord = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> discordToMc = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> pendingCodes = new ConcurrentHashMap<>();
    
    private String botToken;
    private String channelId;
    private boolean enabled;
    
    private WebSocket webSocket;
    private ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatTask;
    private Integer lastSequence = null;
    private final File dataFile;

    public DiscordManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "discord_links.yml");
    }

    public void load() {
        var config = plugin.getConfigManager().getConfig();
        this.botToken = config.getString("discord.bot-token", "YOUR_DISCORD_BOT_TOKEN_HERE");
        this.channelId = config.getString("discord.channel-id", "YOUR_DISCORD_CHANNEL_ID_HERE");
        this.enabled = config.getBoolean("discord.enabled", false);

        loadLinks();

        if (enabled && !botToken.equals("YOUR_DISCORD_BOT_TOKEN_HERE")) {
            verifyTokenAsync().thenAccept(valid -> {
                if (valid) {
                    plugin.getLogger().info("Discord token verified successfully! Connecting to gateway...");
                    connectToGateway();
                } else {
                    plugin.getLogger().severe("Invalid Discord bot token provided! Gateway disabled.");
                }
            });
        }
    }

    public void shutdown() {
        disconnect();
        saveLinks();
    }

    public boolean isEnabled() {
        return enabled;
    }

    private CompletableFuture<Boolean> verifyTokenAsync() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://discord.com/api/v10/users/@me"))
                .header("Authorization", "Bot " + botToken)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> future.complete(response.statusCode() == 200))
                .exceptionally(ex -> {
                    future.complete(false);
                    return null;
                });
        return future;
    }

    private void connectToGateway() {
        disconnect();
        
        HttpClient.newHttpClient()
            .sendAsync(
                HttpRequest.newBuilder()
                    .uri(URI.create("https://discord.com/api/v10/gateway"))
                    .header("Authorization", "Bot " + botToken)
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            )
            .thenAccept(response -> {
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String wsUrl = json.get("url").getAsString() + "/?v=10&encoding=json";
                    
                    HttpClient.newHttpClient().newWebSocketBuilder()
                        .buildAsync(URI.create(wsUrl), new DiscordWebSocketListener())
                        .thenAccept(ws -> {
                            this.webSocket = ws;
                            plugin.getLogger().info("Discord WebSocket connection established.");
                        })
                        .exceptionally(ex -> {
                            plugin.getLogger().severe("Failed to establish Discord WebSocket: " + ex.getMessage());
                            return null;
                        });
                }
            });
    }

    private void disconnect() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Shutting down");
            webSocket = null;
        }
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
            heartbeatTask = null;
        }
        if (heartbeatExecutor != null) {
            heartbeatExecutor.shutdownNow();
            heartbeatExecutor = null;
        }
    }

    public String generateLinkCode(UUID uuid) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        pendingCodes.put(code, uuid);
        return code;
    }

    public boolean unlink(UUID uuid) {
        String discId = mcToDiscord.remove(uuid);
        if (discId != null) {
            discordToMc.remove(discId);
            saveLinksAsync();
            return true;
        }
        return false;
    }

    public boolean isLinked(UUID uuid) {
        return mcToDiscord.containsKey(uuid);
    }

    public String getDiscordId(UUID uuid) {
        return mcToDiscord.get(uuid);
    }

    public UUID getMcUUID(String discordId) {
        return discordToMc.get(discordId);
    }

    public void sendToDiscord(String message) {
        if (!enabled || botToken.equals("YOUR_DISCORD_BOT_TOKEN_HERE") || channelId.equals("YOUR_DISCORD_CHANNEL_ID_HERE")) {
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("content", message);
        String body = new Gson().toJson(payload);

        HttpClient.newHttpClient().sendAsync(
            HttpRequest.newBuilder()
                .uri(URI.create("https://discord.com/api/v10/channels/" + channelId + "/messages"))
                .header("Authorization", "Bot " + botToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.discarding()
        );
    }

    private void loadLinks() {
        if (!dataFile.exists()) return;
        try {
            List<String> lines = Files.readAllLines(dataFile.toPath());
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    UUID uuid = UUID.fromString(parts[0]);
                    String discordId = parts[1];
                    mcToDiscord.put(uuid, discordId);
                    discordToMc.put(discordId, uuid);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load discord links: " + e.getMessage());
        }
    }

    private void saveLinks() {
        try {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<UUID, String> entry : mcToDiscord.entrySet()) {
                lines.add(entry.getKey().toString() + ":" + entry.getValue());
            }
            Files.write(dataFile.toPath(), lines);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save discord links: " + e.getMessage());
        }
    }

    private void saveLinksAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> saveLinks());
    }

    // WebSocket Gateway Message Handler
    private class DiscordWebSocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                JsonObject json = JsonParser.parseString(data.toString()).getAsJsonObject();
                int op = json.get("op").getAsInt();

                if (json.has("s") && !json.get("s").isJsonNull()) {
                    lastSequence = json.get("s").getAsInt();
                }

                switch (op) {
                    case 10: // Hello
                        int interval = json.getAsJsonObject("d").get("heartbeat_interval").getAsInt();
                        startHeartbeat(webSocket, interval);
                        sendIdentify(webSocket);
                        break;
                    case 11: // Heartbeat ACK
                        // Heartbeat acknowledged
                        break;
                    case 0: // Dispatch
                        String event = json.get("t").getAsString();
                        if (event.equals("MESSAGE_CREATE")) {
                            handleMessageCreate(json.getAsJsonObject("d"));
                        }
                        break;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error parsing Discord WS message: " + e.getMessage());
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public java.util.concurrent.CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            plugin.getLogger().warning("Discord WS closed with code: " + statusCode + " / Reason: " + reason);
            if (enabled) {
                // Attempt reconnect
                plugin.getLogger().info("Reconnecting to Discord gateway in 5 seconds...");
                Bukkit.getAsyncScheduler().runDelayed(plugin, task -> connectToGateway(), 5, TimeUnit.SECONDS);
            }
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            plugin.getLogger().severe("Discord WS error: " + error.getMessage());
        }

        private void startHeartbeat(WebSocket ws, int interval) {
            heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
            heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(() -> {
                JsonObject hb = new JsonObject();
                hb.addProperty("op", 1);
                hb.add("d", lastSequence == null ? null : new Gson().toJsonTree(lastSequence));
                ws.sendText(new Gson().toJson(hb), true);
            }, interval, interval, TimeUnit.MILLISECONDS);
        }

        private void sendIdentify(WebSocket ws) {
            JsonObject identify = new JsonObject();
            identify.addProperty("op", 2);
            JsonObject d = new JsonObject();
            d.addProperty("token", botToken);
            d.addProperty("intents", 33281); // GUILDS (1) | GUILD_MESSAGES (512) | MESSAGE_CONTENT (32768)
            
            JsonObject properties = new JsonObject();
            properties.addProperty("os", "linux");
            properties.addProperty("browser", "FoliaCore");
            properties.addProperty("device", "FoliaCore");
            d.add("properties", properties);
            
            identify.add("d", d);
            ws.sendText(new Gson().toJson(identify), true);
        }

        private void handleMessageCreate(JsonObject d) {
            if (d.has("author")) {
                JsonObject author = d.getAsJsonObject("author");
                if (author.has("bot") && author.get("bot").getAsBoolean()) return;
                
                String authorId = author.get("id").getAsString();
                String username = author.get("username").getAsString();
                String content = d.get("content").getAsString().trim();
                String chanId = d.get("channel_id").getAsString();

                // 1. Linking logic via message verification codes
                if (pendingCodes.containsKey(content)) {
                    UUID mcUUID = pendingCodes.remove(content);
                    mcToDiscord.put(mcUUID, authorId);
                    discordToMc.put(authorId, mcUUID);
                    saveLinksAsync();
                    
                    // Notify Discord
                    sendToDiscord("Successfully linked Discord account **" + username + "** with Minecraft account!");
                    
                    // Notify Minecraft
                    Player player = Bukkit.getPlayer(mcUUID);
                    if (player != null) {
                        player.sendMessage("§aYour account has been successfully linked to Discord user: §e" + username);
                    }
                    return;
                }

                // 2. Gateway logic
                if (chanId.equals(channelId)) {
                    String mcMessage = "§8[§bDiscord§8] §e" + username + "§f: " + content;
                    // Broadcast to Minecraft (safely using Folia regional or global scheduler)
                    Bukkit.getGlobalRegionScheduler().run(plugin, task -> {
                        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(mcMessage));
                    });
                }
            }
        }
    }
}
