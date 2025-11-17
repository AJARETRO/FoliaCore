package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.utils.ItemUtil;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager {

    private static KitManager instance;
    private final FoliaCore plugin;

    private final Map<String, Kit> kits;
    private final Map<UUID, Map<String, Long>> kitCooldowns;

    private File dataFile;
    private FileConfiguration dataConfig;

    public KitManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.kits = new ConcurrentHashMap<>();
        this.kitCooldowns = new ConcurrentHashMap<>();
    }

    public static KitManager getInstance() {
        return instance;
    }

    public void load() {
        instance = this;

        dataFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("kits.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadKits();
    }

    private void loadKits() {
        ConfigurationSection kitsSection = dataConfig.getConfigurationSection("kits");
        if (kitsSection == null) {
            plugin.getLogger().warning("No 'kits' section found in kits.yml!");
            return;
        }

        for (String kitName : kitsSection.getKeys(false)) {
            try {
                ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
                if (kitSection == null) continue;
                Map<String, Object> kitData = kitSection.getValues(false);

                Kit kit = Kit.deserialize(kitName, kitData);
                kits.put(kitName.toLowerCase(), kit);
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load kit: " + kitName);
                e.printStackTrace();
            }
        }
    }

    public void saveData() {
        try {
            dataConfig.set("kits", null);
            for (Map.Entry<String, Kit> entry : kits.entrySet()) {
                dataConfig.set("kits." + entry.getKey(), entry.getValue().serialize());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save kit data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveData();
        });
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public Collection<Kit> getAllKits() {
        return kits.values();
    }

    public boolean isKit(String name) {
        return kits.containsKey(name.toLowerCase());
    }

    public void createKit(String name, long cooldown, String permission, Material displayMaterial, ItemStack[] items) {
        String itemsBase64 = ItemUtil.serializeItems(items);
        Kit kit = new Kit(name, cooldown, permission, displayMaterial, itemsBase64);
        kits.put(name.toLowerCase(), kit);
        saveDataAsync();
    }

    public void deleteKit(String name) {
        kits.remove(name.toLowerCase());
        saveDataAsync();
    }

    public boolean isOnCooldown(UUID playerUUID, Kit kit) {
        Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
        if (cooldowns == null) {
            return false;
        }

        Long expiration = cooldowns.get(kit.name().toLowerCase());
        if (expiration == null) {
            return false;
        }

        if (System.currentTimeMillis() < expiration) {
            return true;
        } else {
            cooldowns.remove(kit.name().toLowerCase());
            return false;
        }
    }

    public void setOnCooldown(UUID playerUUID, Kit kit) {
        long expirationTime = System.currentTimeMillis() + (kit.cooldown() * 1000);
        kitCooldowns.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>()).put(kit.name().toLowerCase(), expirationTime);
    }

    public long getRemainingCooldown(UUID playerUUID, Kit kit) {
        Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
        if (cooldowns == null) return 0;

        Long expiration = cooldowns.get(kit.name().toLowerCase());
        if (expiration == null) return 0;

        long remaining = expiration - System.currentTimeMillis();
        return (remaining > 0) ? remaining : 0;
    }

    public boolean giveKit(Player player, Kit kit) {
        if (!player.hasPermission(kit.permission())) {
            FoliaCore.getInstance().getMessenger().sendError(player, "You do not have permission to use this kit.");
            return false;
        }

        if (isOnCooldown(player.getUniqueId(), kit)) {
            long remaining = getRemainingCooldown(player.getUniqueId(), kit);
            String formattedTime = TimeUtil.formatDuration(remaining);
            FoliaCore.getInstance().getMessenger().sendError(player, "You must wait " + ChatColor.GOLD + formattedTime + ChatColor.RED + " before using this kit again.");
            return false;
        }

        for (ItemStack item : kit.getItems()) {
            if (item == null) continue;
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
        }

        setOnCooldown(player.getUniqueId(), kit);
        FoliaCore.getInstance().getMessenger().sendSuccess(player, "You have redeemed the " + ChatColor.GOLD + kit.name() + ChatColor.GREEN + " kit.");
        return true;
    }
}