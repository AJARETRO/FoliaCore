package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MiscAdminCommand implements CommandExecutor, TabCompleter, Listener {
    private final FoliaCore plugin;
    private final Set<UUID> unlimitedPlayers = ConcurrentHashMap.newKeySet();

    public MiscAdminCommand(FoliaCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        switch (cmd) {
            case "unlimited":
                handleUnlimited(player);
                break;
            case "rest":
                handleRest(player);
                break;
            case "warpinfo":
                handleWarpInfo(player, args);
                break;
        }
        return true;
    }

    private void handleUnlimited(Player player) {
        UUID uuid = player.getUniqueId();
        if (unlimitedPlayers.contains(uuid)) {
            unlimitedPlayers.remove(uuid);
            plugin.getMessenger().sendSuccess(player, "Unlimited placement disabled.");
        } else {
            unlimitedPlayers.add(uuid);
            plugin.getMessenger().sendSuccess(player, "Unlimited placement enabled.");
        }
    }

    private void handleRest(Player player) {
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            player.sendMessage("§aYour time since rest has been reset. Sleep cycle refreshed!");
        });
    }

    private void handleWarpInfo(Player player, String[] args) {
        if (!plugin.getConfigManager().utilityEnabled) {
            plugin.getMessenger().sendError(player, "Utilities module is disabled.");
            return;
        }
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /warpinfo <warp_name>");
            return;
        }
        String warpName = args[0].toLowerCase();
        dev.ajaretro.foliaCore.data.Warp warp = plugin.getWarpManager().getWarp(warpName);
        if (warp == null) {
            plugin.getMessenger().sendError(player, "Warp not found.");
            return;
        }
        Location loc = warp.toLocation();
        if (loc == null) {
            plugin.getMessenger().sendError(player, "Warp location is not loaded.");
            return;
        }
        player.sendMessage("§6§lWarp Info: §e" + warpName);
        player.sendMessage("§7World: §f" + loc.getWorld().getName());
        player.sendMessage("§7X: §f" + String.format("%.2f", loc.getX()));
        player.sendMessage("§7Y: §f" + String.format("%.2f", loc.getY()));
        player.sendMessage("§7Z: §f" + String.format("%.2f", loc.getZ()));
        player.sendMessage("§7Yaw: §f" + String.format("%.1f", loc.getYaw()));
        player.sendMessage("§7Pitch: §f" + String.format("%.1f", loc.getPitch()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (unlimitedPlayers.contains(player.getUniqueId())) {
            ItemStack item = event.getItemInHand();
            // Restore block stack to full
            player.getScheduler().run(plugin, task -> {
                item.setAmount(item.getMaxStackSize());
            }, null);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("warpinfo") && args.length == 1 && plugin.getConfigManager().utilityEnabled) {
            List<String> list = new ArrayList<>();
            for (dev.ajaretro.foliaCore.data.Warp w : plugin.getWarpManager().getAllWarps()) {
                String warpName = w.getName().toLowerCase();
                if (warpName.startsWith(args[0].toLowerCase())) {
                    list.add(w.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
