package dev.ajaretro.foliaCore.gui;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.managers.KitManager;
import dev.ajaretro.foliaCore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey; // <-- New Import
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType; // <-- New Import

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KitGUI {

    private final FoliaCore plugin;
    private final KitManager kitManager;
    private final Player player;
    private Inventory gui;
    public static final String GUI_TITLE = ChatColor.DARK_BLUE + "Select a Kit";

    public KitGUI(FoliaCore plugin, Player player) {
        this.plugin = plugin;
        this.kitManager = plugin.getKitManager();
        this.player = player;
    }

    public void openGUI() {
        Collection<Kit> kits = kitManager.getAllKits();
        int size = (int) Math.ceil(kits.size() / 9.0) * 9;
        gui = Bukkit.createInventory(null, Math.max(9, size), GUI_TITLE);

        // Define our hidden key
        NamespacedKey key = new NamespacedKey(plugin, "kit_key");

        for (Kit kit : kits) {
            ItemStack item = new ItemStack(kit.displayMaterial());
            ItemMeta meta = item.getItemMeta();

            // 1. Set Visible Name
            meta.setDisplayName(ChatColor.GOLD + kit.name());

            // 2. Set Hidden Data (The Fix)
            // We store the exact kit name inside the item's code.
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, kit.name());

            List<String> lore = new ArrayList<>();
            if (player.hasPermission(kit.permission())) {
                if (kitManager.isOnCooldown(player.getUniqueId(), kit)) {
                    long remaining = kitManager.getRemainingCooldown(player.getUniqueId(), kit);
                    lore.add(ChatColor.RED + "On Cooldown!");
                    lore.add(ChatColor.GRAY + "Time left: " + TimeUtil.formatDuration(remaining));
                } else {
                    lore.add(ChatColor.GREEN + "Click to redeem!");
                }
            } else {
                lore.add(ChatColor.DARK_RED + "Locked");
                lore.add(ChatColor.GRAY + "Requires permission:");
                lore.add(ChatColor.GRAY + kit.permission());
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.addItem(item);
        }

        player.openInventory(gui);
    }
}