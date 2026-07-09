package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.JailedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages jail locations and jailed players.
 * Integrates with Folia's regionalized scheduling.
 */
public class JailManager {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<String, Location> jails = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, JailedPlayer> jailedPlayers = new ConcurrentHashMap<>();

    public JailManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            jails.putAll(plugin.getStorageManager().getProvider().loadJails());
            jailedPlayers.putAll(plugin.getStorageManager().getProvider().loadJailedPlayers());
        }

        // Start checking for expired jail sessions asynchronously
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
            long now = System.currentTimeMillis();
            for (JailedPlayer jp : jailedPlayers.values()) {
                if (jp.isExpired()) {
                    UUID uuid = jp.getUuid();
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.getScheduler().run(plugin, t -> {
                            unjailPlayer(uuid);
                            player.sendMessage("§aYour jail sentence has expired. You are free!");
                        }, null);
                    } else {
                        unjailPlayer(uuid);
                    }
                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void saveData() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            plugin.getStorageManager().getProvider().saveJails(jails);
            plugin.getStorageManager().getProvider().saveJailedPlayers(jailedPlayers);
        }
    }

    public void setJail(String name, Location loc) {
        jails.put(name.toLowerCase(), loc);
        saveDataAsync();
    }

    public void deleteJail(String name) {
        jails.remove(name.toLowerCase());
        saveDataAsync();
    }

    public Location getJail(String name) {
        return jails.get(name.toLowerCase());
    }

    public Map<String, Location> getJails() {
        return jails;
    }

    public boolean isJailed(UUID uuid) {
        JailedPlayer jp = jailedPlayers.get(uuid);
        if (jp == null) return false;
        if (jp.isExpired()) {
            unjailPlayer(uuid);
            return false;
        }
        return true;
    }

    public JailedPlayer getJailedPlayer(UUID uuid) {
        return jailedPlayers.get(uuid);
    }

    public boolean jailPlayer(UUID uuid, String jailName, long durationSeconds, String reason, Location returnLoc) {
        Location jailLoc = getJail(jailName);
        if (jailLoc == null) return false;

        long expiry = (durationSeconds == -1) ? -1 : System.currentTimeMillis() + (durationSeconds * 1000);
        JailedPlayer jp = new JailedPlayer(uuid, jailName, expiry, reason, System.currentTimeMillis(), returnLoc);
        jailedPlayers.put(uuid, jp);
        saveDataAsync();

        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            p.teleportAsync(jailLoc).thenAccept(success -> {
                if (success) {
                    p.sendMessage("§cYou have been jailed in '" + jailName + "' for: " + reason);
                }
            });
        }
        return true;
    }

    public boolean unjailPlayer(UUID uuid) {
        JailedPlayer jp = jailedPlayers.remove(uuid);
        if (jp == null) return false;
        saveDataAsync();

        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            Location ret = jp.getReturnLocation();
            if (ret == null) {
                ret = p.getWorld().getSpawnLocation();
            }
            p.teleportAsync(ret).thenAccept(success -> {
                p.sendMessage("§aYou have been unjailed!");
            });
        }
        return true;
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> saveData());
    }

    public Map<UUID, JailedPlayer> getJailedPlayers() {
        return jailedPlayers;
    }
}
