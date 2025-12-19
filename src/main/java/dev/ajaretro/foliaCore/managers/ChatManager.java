package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.ChatMode;
import dev.ajaretro.foliaCore.data.Mail;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatManager {

    private static ChatManager instance;
    private final FoliaCore plugin;

    private final ConcurrentHashMap<UUID, Long> mutes;
    private final ConcurrentHashMap<UUID, UUID> replyTargets;
    private final ConcurrentHashMap<UUID, Set<UUID>> blockedPlayers;
    private final ConcurrentHashMap<UUID, List<Mail>> mailboxes;
    private final ConcurrentHashMap<UUID, ChatMode> playerChatModes;
    private final ConcurrentHashMap<UUID, String> nicknames;

    private File dataFile;
    private FileConfiguration dataConfig;

    private boolean chatRangesEnabled;
    private ChatMode defaultMode;
    private int regionalRadius;
    private String globalPrefix;
    private String chatFormat;

    public ChatManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.mutes = new ConcurrentHashMap<>();
        this.replyTargets = new ConcurrentHashMap<>();
        this.blockedPlayers = new ConcurrentHashMap<>();
        this.mailboxes = new ConcurrentHashMap<>();
        this.playerChatModes = new ConcurrentHashMap<>();
        this.nicknames = new ConcurrentHashMap<>();
    }

    public static ChatManager getInstance() {
        return instance;
    }

    public void load() {
        instance = this;

        dataFile = new File(plugin.getDataFolder(), "chat_data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("chat_data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadConfigSettings();
        loadMutes();
        loadBlockedPlayers();
        loadMail();
        loadNicknames();
    }

    private void loadConfigSettings() {
        chatRangesEnabled = dataConfig.getBoolean("chat-settings.enabled", true);
        try {
            defaultMode = ChatMode.valueOf(dataConfig.getString("chat-settings.default-mode", "GLOBAL").toUpperCase());
        } catch (IllegalArgumentException e) {
            defaultMode = ChatMode.GLOBAL;
        }
        regionalRadius = dataConfig.getInt("chat-settings.regional-chat-radius", 100);
        globalPrefix = dataConfig.getString("chat-settings.global-chat-prefix", "!");
        chatFormat = dataConfig.getString("chat-settings.format", "<{DISPLAYNAME}> {MESSAGE}");
    }

    private void loadMutes() {
        ConfigurationSection mutesSection = dataConfig.getConfigurationSection("mutes");
        if (mutesSection == null) return;
        for (String key : mutesSection.getKeys(false)) {
            try {
                mutes.put(UUID.fromString(key), dataConfig.getLong("mutes." + key));
            } catch (Exception ignored) {}
        }
    }

    private void loadBlockedPlayers() {
        ConfigurationSection blockedSection = dataConfig.getConfigurationSection("blocked");
        if (blockedSection == null) return;
        for (String key : blockedSection.getKeys(false)) {
            try {
                Set<UUID> blockedSet = dataConfig.getStringList("blocked." + key).stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());
                blockedPlayers.put(UUID.fromString(key), ConcurrentHashMap.newKeySet(blockedSet.size()));
                blockedPlayers.get(UUID.fromString(key)).addAll(blockedSet);
            } catch (Exception ignored) {}
        }
    }

    private void loadMail() {
        ConfigurationSection mailSection = dataConfig.getConfigurationSection("mail");
        if (mailSection == null) return;
        for (String key : mailSection.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(key);
                List<?> mailList = dataConfig.getList("mail." + key);
                List<Mail> loadedMail = new ArrayList<>();
                for (Object obj : mailList) {
                    if (obj instanceof Map<?,?> map) {
                        loadedMail.add(new Mail(
                                UUID.fromString((String) map.get("sender")),
                                (long) map.get("timestamp"),
                                (String) map.get("message")
                        ));
                    }
                }
                mailboxes.put(playerUUID, Collections.synchronizedList(loadedMail));
            } catch (Exception ignored) {}
        }
    }

    private void loadNicknames() {
        ConfigurationSection nickSection = dataConfig.getConfigurationSection("nicknames");
        if (nickSection == null) return;
        for (String key : nickSection.getKeys(false)) {
            try {
                nicknames.put(UUID.fromString(key), dataConfig.getString("nicknames." + key));
            } catch (Exception ignored) {}
        }
    }

    public void saveData() {
        try {
            dataConfig.set("mutes", null);
            for (UUID uuid : mutes.keySet()) {
                dataConfig.set("mutes." + uuid.toString(), mutes.get(uuid));
            }

            dataConfig.set("blocked", null);
            for (UUID uuid : blockedPlayers.keySet()) {
                List<String> blockedList = blockedPlayers.get(uuid).stream().map(UUID::toString).collect(Collectors.toList());
                dataConfig.set("blocked." + uuid.toString(), blockedList);
            }

            dataConfig.set("mail", null);
            for (UUID uuid : mailboxes.keySet()) {
                List<Map<String, Object>> serializedMail = new ArrayList<>();
                List<Mail> mailList = mailboxes.get(uuid);
                synchronized (mailList) {
                    for (Mail mail : mailList) {
                        Map<String, Object> mailMap = new LinkedHashMap<>();
                        mailMap.put("sender", mail.sender().toString());
                        mailMap.put("timestamp", mail.timestamp());
                        mailMap.put("message", mail.message());
                        serializedMail.add(mailMap);
                    }
                }
                dataConfig.set("mail." + uuid.toString(), serializedMail);
            }

            dataConfig.set("nicknames", null);
            for (UUID uuid : nicknames.keySet()) {
                dataConfig.set("nicknames." + uuid.toString(), nicknames.get(uuid));
            }

            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveData());
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public boolean isChatRangesEnabled() { return chatRangesEnabled; }
    public String getGlobalPrefix() { return globalPrefix; }
    public int getRegionalRadius() { return regionalRadius; }
    public ChatMode getPlayerChatMode(UUID uuid) { return playerChatModes.getOrDefault(uuid, defaultMode); }
    public void setPlayerChatMode(UUID uuid, ChatMode mode) { playerChatModes.put(uuid, mode); }

    public boolean isMuted(UUID uuid) {
        if (!mutes.containsKey(uuid)) return false;
        long expiration = mutes.get(uuid);
        if (expiration == -1) return true;
        if (System.currentTimeMillis() < expiration) return true;
        mutes.remove(uuid);
        saveDataAsync();
        return false;
    }

    public void mutePlayer(UUID uuid, long durationMillis) {
        mutes.put(uuid, (durationMillis == -1) ? -1 : System.currentTimeMillis() + durationMillis);
        saveDataAsync();
    }

    public void unmutePlayer(UUID uuid) {
        mutes.remove(uuid);
        saveDataAsync();
    }

    public UUID getReplyTarget(UUID uuid) { return replyTargets.get(uuid); }
    public void setReplyTarget(UUID player, UUID target) { replyTargets.put(player, target); }

    public boolean isBlocked(UUID target, UUID blocker) {
        Set<UUID> blockers = blockedPlayers.get(target);
        return blockers != null && blockers.contains(blocker);
    }

    public void blockPlayer(UUID target, UUID blocker) {
        blockedPlayers.computeIfAbsent(target, k -> ConcurrentHashMap.newKeySet()).add(blocker);
        saveDataAsync();
    }

    public void unblockPlayer(UUID target, UUID blocker) {
        Set<UUID> blockers = blockedPlayers.get(target);
        if (blockers != null) blockers.remove(blocker);
        saveDataAsync();
    }

    public void sendMail(UUID sender, UUID target, String message) {
        List<Mail> mailList = mailboxes.computeIfAbsent(target, k -> Collections.synchronizedList(new ArrayList<>()));
        mailList.add(new Mail(sender, message));
        saveDataAsync();
    }

    public List<Mail> getMail(UUID player) { return mailboxes.get(player); }
    public void clearMail(UUID player) {
        mailboxes.remove(player);
        saveDataAsync();
    }

    public void setNickname(UUID uuid, String nickname) {
        nicknames.put(uuid, nickname);
        saveDataAsync();
    }

    public String getNickname(UUID uuid) {
        return nicknames.get(uuid);
    }

    public void removeNickname(UUID uuid) {
        nicknames.remove(uuid);
        saveDataAsync();
    }

    public Map<UUID, String> getAllNicknames() {
        return nicknames;
    }
}