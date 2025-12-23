package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.economy.FoliaEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EconomyManager {

    private final FoliaCore plugin;
    private Economy econ = null;
    private File dataFile;
    private FileConfiguration dataConfig;

    public EconomyManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!setupEconomy()) {
            plugin.getLogger().severe(ChatColor.DARK_RED + "===================================================");
            plugin.getLogger().severe(ChatColor.DARK_RED + "VAULT NOT FOUND! Economy features will be disabled.");
            plugin.getLogger().severe(ChatColor.DARK_RED + "Please install Vault.");
            plugin.getLogger().severe(ChatColor.DARK_RED + "===================================================");
            return;
        }

        if (econ instanceof FoliaEconomy) {
            loadLocalData();
        }
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void loadLocalData() {
        dataFile = new File(plugin.getDataFolder(), "economy.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("economy.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        Map<String, Double> loadedBalances = new HashMap<>();
        if (dataConfig.contains("balances")) {
            for (String uuid : dataConfig.getConfigurationSection("balances").getKeys(false)) {
                double amount = dataConfig.getDouble("balances." + uuid);
                loadedBalances.put(uuid, amount);
            }
        }

        ((FoliaEconomy) econ).setBalances(loadedBalances);
        plugin.getLogger().info("Loaded " + loadedBalances.size() + " accounts into FoliaEconomy.");
    }

    public void saveData() {
        if (!(econ instanceof FoliaEconomy)) {
            return;
        }

        if (dataFile == null || dataConfig == null) return;

        Map<String, Double> balances = ((FoliaEconomy) econ).getBalances();

        dataConfig.set("balances", null);
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            dataConfig.set("balances." + entry.getKey(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save economy data!");
            e.printStackTrace();
        }
    }

    public boolean hasEconomy() {
        return econ != null;
    }

    public String format(double amount) {
        return econ.format(amount);
    }

    public double getBalance(OfflinePlayer player) {
        return econ.getBalance(player);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return econ.has(player, amount);
    }

    public EconomyResponse withdraw(OfflinePlayer player, double amount) {
        EconomyResponse response = econ.withdrawPlayer(player, amount);
        if (econ instanceof FoliaEconomy && response.transactionSuccess()) {
            triggerSave();
        }
        return response;
    }

    public EconomyResponse deposit(OfflinePlayer player, double amount) {
        EconomyResponse response = econ.depositPlayer(player, amount);
        if (econ instanceof FoliaEconomy && response.transactionSuccess()) {
            triggerSave();
        }
        return response;
    }

    private void triggerSave() {
        if (!plugin.isEnabled()) {
            saveData();
            return;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> saveData());
    }
}