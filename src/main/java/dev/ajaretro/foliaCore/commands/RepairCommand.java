package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

public class RepairCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public RepairCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("all"))) {
            if (!(sender instanceof Player player)) {
                plugin.getMessenger().sendError(sender, "Usage: /repair [all] OR /repair <player> [all]");
                return true;
            }

            if (!player.hasPermission("foliacore.repair")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }

            boolean all = args.length == 1 && args[0].equalsIgnoreCase("all");
            if (all) {
                if (!player.hasPermission("foliacore.repair.all")) {
                    plugin.getMessenger().sendError(player, "You do not have permission to repair all inventory items.");
                    return true;
                }
            }

            int repaired = repairTarget(player, all);
            if (repaired <= 0) {
                plugin.getMessenger().sendError(player, all ? "No damaged items found in your inventory." : "You must hold a damaged item in your main hand.");
                return true;
            }

            plugin.getMessenger().sendSuccess(player, all ? "Repaired " + repaired + " item(s) in your inventory." : "Repaired your held item.");
            return true;
        }

        String targetName = args[0];
        boolean all = args.length > 1 && args[1].equalsIgnoreCase("all");

        if (!sender.hasPermission("foliacore.repair.others")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to repair other players' items.");
            return true;
        }

        if (all && !sender.hasPermission("foliacore.repair.others.all")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to repair all inventory items for others.");
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            return true;
        }

        int repaired = repairTarget(target, all);
        if (repaired <= 0) {
            plugin.getMessenger().sendError(sender, all ? "No damaged items found in target inventory." : "Target must hold a damaged item in main hand.");
            return true;
        }

        plugin.getMessenger().sendSuccess(target, all ? "An admin repaired your inventory items." : "An admin repaired your held item.");
        plugin.getMessenger().sendSuccess(sender, all ? "Repaired " + repaired + " item(s) for " + target.getName() + "." : "Repaired held item for " + target.getName() + ".");
        return true;
    }

    private int repairTarget(Player player, boolean all) {
        if (!all) {
            return repairItem(player.getInventory().getItemInMainHand()) ? 1 : 0;
        }

        int repaired = 0;
        PlayerInventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (repairItem(item)) {
                repaired++;
            }
        }

        for (ItemStack armor : inventory.getArmorContents()) {
            if (repairItem(armor)) {
                repaired++;
            }
        }

        if (repairItem(inventory.getItemInOffHand())) {
            repaired++;
        }

        player.updateInventory();
        return repaired;
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        if (!(item.getItemMeta() instanceof Damageable meta)) {
            return false;
        }

        if (meta.getDamage() <= 0) {
            return false;
        }

        meta.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }
}