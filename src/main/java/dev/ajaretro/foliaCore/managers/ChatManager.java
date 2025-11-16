package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Mail;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private static ChatManager instance;
    private final FoliaCore plugin;

    private final ConcurrentHashMap<UUID, Long> mutes;
    private final ConcurrentHashMap<UUID, UUID> replyTargets;
    private final ConcurrentHashMap<UUID, Set<UUID>> blockedPlayers;
    private final ConcurrentHashMap<UUID, List<Mail>> mailboxes;

    private File dataFile;
    private FileConfiguration dataConfig;

    public ChatManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.mutes = new ConcurrentHashMap<>();
        this.replyTargets = new ConcurrentHashMap<>();
        this.blockedPlayers = new ConcurrentHashMap<>();
        this.mailboxes = new ConcurrentHashMap<>();
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

        loadMutes();
        loadBlockedPlayers();
        loadMail();
    }

    private void loadMutes() {
        ConfigurationSection mutesSection = dataConfig.getConfigurationSection("mutes");
        if (mutesSection == null) return;

        for (String key : mutesSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long expiration = dataConfig.getLong("mutes." + key);
                mutes.put(uuid, expiration);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load mute for invalid UUID: " + key);
            }
        }
    }

    private void loadBlockedPlayers() {
        ConfigurationSection blockedSection = dataConfig.getConfigurationSection("blocked");
        if (blockedSection == null) return;

        for (String key : blockedSection.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(key);
                List<String> blockedUUIDs = dataConfig.getStringList("blocked." + key);
                Set<UUID> blockedSet = blockedUUIDs.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());
                blockedPlayers.put(playerUUID, ConcurrentHashMap.newKeySet(blockedSet.size()));
                blockedPlayers.get(playerUUID).addAll(blockedSet);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load block list for invalid UUID: " + key);
            }
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
                        UUID sender = UUID.fromString((String) map.get("sender"));
                        long timestamp = (long) map.get("timestamp");
                        String message = (String) map.get("message");
                        loadedMail.add(new Mail(sender, timestamp, message));
                    }
                }
                // BUGFIX 1: Wrap the loaded list to make it thread-safe
                mailboxes.put(playerUUID, Collections.synchronizedList(loadedMail));
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load mail for invalid UUID: " + key);
            }
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
                List<String> blockedList = blockedPlayers.get(uuid).stream()
                        .map(UUID::toString)
                        .collect(Collectors.toList());
                dataConfig.set("blocked." + uuid.toString(), blockedList);
            }

            dataConfig.set("mail", null);
            for (UUID uuid : mailboxes.keySet()) {
                List<Map<String, Object>> serializedMail = new ArrayList<>();
                List<Mail> mailList = mailboxes.get(uuid);

                // We must synchronize on the list when iterating to prevent errors
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

            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save chat data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveData();
        });
    }

    public boolean isMuted(UUID uuid) {
        if (!mutes.containsKey(uuid)) {
            return false;
        }

        long expiration = mutes.get(uuid);
        if (expiration == -1) {
            return true;
        }

        if (System.currentTimeMillis() < expiration) {
            return true;
        } else {
            mutes.remove(uuid);
            saveDataAsync();
            return false;
        }
    }

    public void mutePlayer(UUID uuid, long durationMillis) {
        long expirationTime = (durationMillis == -1) ? -1 : System.currentTimeMillis() + durationMillis;
        mutes.put(uuid, expirationTime);
        saveDataAsync();
    }

    public void unmutePlayer(UUID uuid) {
        mutes.remove(uuid);
        saveDataAsync();
    }

    public UUID getReplyTarget(UUID uuid) {
        return replyTargets.get(uuid);
    }

    public void setReplyTarget(UUID player, UUID target) {
        replyTargets.put(player, target);
    }

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
        if (blockers != null) {
            blockers.remove(blocker);
        }
        saveDataAsync();
    }

    public void sendMail(UUID sender, UUID target, String message) {
        Mail mail = new Mail(sender, message);
        // BUGFIX 2: Ensure the new list created is thread-safe
        List<Mail> mailList = mailboxes.computeIfAbsent(target, k -> Collections.synchronizedList(new ArrayList<>()));
        mailList.add(mail);
        saveDataAsync();
    }

    public List<Mail> getMail(UUID player) {
        return mailboxes.get(player);
    }

    public void clearMail(UUID player) {
        mailboxes.remove(player);
        saveDataAsync();
    }
}