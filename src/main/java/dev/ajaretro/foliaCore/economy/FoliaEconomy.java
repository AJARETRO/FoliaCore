// File: src/main/java/dev/ajaretro/foliaCore/economy/FoliaEconomy.java

package dev.ajaretro.foliaCore.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FoliaEconomy implements Economy {

    // Thread-safe storage.
    private final ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

    // --- NEW METHODS FOR SAVING/LOADING ---

    // Allow the EconomyManager to get the data to save it
    public Map<String, Double> getBalances() {
        return balances;
    }

    // Allow the EconomyManager to load data from file
    public void setBalances(Map<String, Double> loadedBalances) {
        this.balances.clear();
        this.balances.putAll(loadedBalances);
    }

    // --------------------------------------

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() { return "FoliaCoreEco"; }

    @Override
    public boolean hasBankSupport() { return false; }

    @Override
    public int fractionalDigits() { return 2; }

    @Override
    public String format(double amount) {
        return String.format("$%.2f", amount);
    }

    @Override
    public String currencyNamePlural() { return "Dollars"; }

    @Override
    public String currencyNameSingular() { return "Dollar"; }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return balances.containsKey(player.getUniqueId().toString());
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return balances.getOrDefault(player.getUniqueId().toString(), 0.0);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        String uuid = player.getUniqueId().toString();

        synchronized (balances) {
            double current = balances.getOrDefault(uuid, 0.0);

            if (current < amount) {
                return new EconomyResponse(0, current, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            }

            double newBalance = current - amount;
            balances.put(uuid, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");

        String uuid = player.getUniqueId().toString();

        synchronized (balances) {
            double current = balances.getOrDefault(uuid, 0.0);
            double newBalance = current + amount;
            balances.put(uuid, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        balances.putIfAbsent(player.getUniqueId().toString(), 0.0);
        return true;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    // Legacy / Unused Methods
    @Override public boolean hasAccount(String playerName) { return false; }
    @Override public double getBalance(String playerName) { return 0; }
    @Override public boolean has(String playerName, double amount) { return false; }
    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Use UUIDs"); }
    @Override public EconomyResponse depositPlayer(String playerName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Use UUIDs"); }
    @Override public EconomyResponse createBank(String name, String player) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse deleteBank(String name) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse bankBalance(String name) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse bankHas(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks"); }
    @Override public List<String> getBanks() { return Collections.emptyList(); }
    @Override public boolean createPlayerAccount(String playerName) { return false; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return createPlayerAccount(playerName); }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return createPlayerAccount(player); }
    @Override public boolean hasAccount(String playerName, String worldName) { return hasAccount(playerName); }
    @Override public boolean hasAccount(OfflinePlayer player, String worldName) { return hasAccount(player); }
    @Override public double getBalance(String playerName, String world) { return getBalance(playerName); }
    @Override public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }
    @Override public boolean has(String playerName, String worldName, double amount) { return has(playerName, amount); }
    @Override public boolean has(OfflinePlayer player, String worldName, double amount) { return has(player, amount); }
    @Override public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return withdrawPlayer(playerName, amount); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) { return withdrawPlayer(player, amount); }
    @Override public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return depositPlayer(playerName, amount); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) { return depositPlayer(player, amount); }
}