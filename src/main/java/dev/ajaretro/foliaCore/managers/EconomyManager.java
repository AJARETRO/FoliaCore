package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final FoliaCore plugin;
    private Economy econ = null;

    public EconomyManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!setupEconomy()) {
            plugin.getLogger().severe(ChatColor.DARK_RED + "===================================================");
            plugin.getLogger().severe(ChatColor.DARK_RED + "VAULT NOT FOUND! Economy features will be disabled.");
            plugin.getLogger().severe(ChatColor.DARK_RED + "Please install Vault and an economy plugin (like EssentialsX Eco).");
            plugin.getLogger().severe(ChatColor.DARK_RED + "===================================================");
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
        return econ.withdrawPlayer(player, amount);
    }

    public EconomyResponse deposit(OfflinePlayer player, double amount) {
        return econ.depositPlayer(player, amount);
    }
}