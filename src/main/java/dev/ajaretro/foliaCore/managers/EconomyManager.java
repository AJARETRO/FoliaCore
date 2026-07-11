/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Economy provider implementing the Vault Economy interface.
 * Handles player balances, deposits, and withdrawals thread-safely.
 */
public class EconomyManager implements Economy {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Double> balances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> nameToUuid = new ConcurrentHashMap<>();
    private double defaultBalance = 1000.0;

    public EconomyManager(FoliaCore plugin) {
        this.plugin = plugin;
    }

    public void load() {
        defaultBalance = plugin.getConfigManager().getConfig().getDouble("economy.default-balance", 1000.0);
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            balances.putAll(plugin.getStorageManager().getProvider().loadBalances());
        }

        // Register with Bukkit Services Manager
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServicesManager().register(Economy.class, this, plugin, ServicePriority.Highest);
            plugin.getLogger().info("Successfully registered FoliaCore Economy with Vault!");
        } else {
            plugin.getLogger().warning("Vault not found. Economy commands will still function, but other plugins won't hook into FoliaCore Economy.");
        }
    }

    public void saveData() {
        if (plugin.getStorageManager() != null && plugin.getStorageManager().getProvider() != null) {
            plugin.getStorageManager().getProvider().saveBalances(balances);
        }
    }

    public void cachePlayer(String name, UUID uuid) {
        nameToUuid.put(name.toLowerCase(), uuid);
    }

    private UUID getUuidFromName(String name) {
        if (name == null) return null;
        UUID cached = nameToUuid.get(name.toLowerCase());
        if (cached != null) return cached;
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.getUniqueId() != null) {
            nameToUuid.put(name.toLowerCase(), op.getUniqueId());
            return op.getUniqueId();
        }
        return null;
    }

    // --- Vault Economy Implementation ---

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled() && plugin.getConfigManager().isEconomyEnabled();
    }

    @Override
    public String getName() {
        return "FoliaCoreEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("$%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        UUID uuid = getUuidFromName(playerName);
        return uuid != null && balances.containsKey(uuid);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return player != null && balances.containsKey(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        UUID uuid = getUuidFromName(playerName);
        if (uuid == null) return 0.0;
        return balances.getOrDefault(uuid, defaultBalance);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (player == null) return 0.0;
        return balances.getOrDefault(player.getUniqueId(), defaultBalance);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        UUID uuid = getUuidFromName(playerName);
        if (uuid == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        return withdrawPlayer(uuid, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is null");
        }
        return withdrawPlayer(player.getUniqueId(), amount);
    }

    private EconomyResponse withdrawPlayer(UUID uuid, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative amount");
        }
        double current = balances.getOrDefault(uuid, defaultBalance);
        if (current < amount) {
            return new EconomyResponse(0, current, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        double next = current - amount;
        balances.put(uuid, next);
        saveDataAsync();
        return new EconomyResponse(amount, next, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        UUID uuid = getUuidFromName(playerName);
        if (uuid == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        return depositPlayer(uuid, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is null");
        }
        return depositPlayer(player.getUniqueId(), amount);
    }

    private EconomyResponse depositPlayer(UUID uuid, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative amount");
        }
        double current = balances.getOrDefault(uuid, defaultBalance);
        double next = current + amount;
        balances.put(uuid, next);
        saveDataAsync();
        return new EconomyResponse(amount, next, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> saveData());
    }

    // --- Bank Methods (Not supported) ---

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        UUID uuid = getUuidFromName(playerName);
        if (uuid == null) return false;
        if (!balances.containsKey(uuid)) {
            balances.put(uuid, defaultBalance);
            saveDataAsync();
        }
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (player == null) return false;
        if (!balances.containsKey(player.getUniqueId())) {
            balances.put(player.getUniqueId(), defaultBalance);
            saveDataAsync();
        }
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    private final ConcurrentHashMap<UUID, Boolean> disabledPayments = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> paymentConfirmations = new ConcurrentHashMap<>();

    public boolean isPaymentsDisabled(UUID uuid) {
        return disabledPayments.getOrDefault(uuid, false);
    }

    public boolean togglePayments(UUID uuid) {
        return disabledPayments.compute(uuid, (k, v) -> v == null || !v);
    }

    public boolean isPayConfirmEnabled(UUID uuid) {
        return paymentConfirmations.getOrDefault(uuid, false);
    }

    public boolean togglePayConfirm(UUID uuid) {
        return paymentConfirmations.compute(uuid, (k, v) -> v == null || !v);
    }

    public Map<UUID, Double> getBalances() {
        return balances;
    }
}
