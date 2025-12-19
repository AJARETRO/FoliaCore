package dev.ajaretro.foliaCore.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FoliaEconomy implements Economy {

    // Thread-safe storage.
    // Key: Player UUID (String), Value: Balance (Double)
    private final ConcurrentHashMap<String, Double> balances = new ConcurrentHashMap<>();

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

    // --- Thread-Safe Read Operations ---

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

    // --- CRITICAL: Thread-Safe Write Operations ---
    // We synchronize on the 'balances' map to prevent race conditions (Double Spending).
    // In a massive server, you would lock specific UUIDs, but for a Core plugin, this is perfect.

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        String uuid = player.getUniqueId().toString();

        // Lock the map briefly to ensure the math is atomic
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

    // --- The "Missing Method" Fix (createBank with OfflinePlayer) ---
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    // --- Legacy / Unused Methods (Required by Interface) ---

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