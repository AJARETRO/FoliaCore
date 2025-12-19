package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.gui.KitGUI;
import org.bukkit.NamespacedKey; // <-- New Import
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType; // <-- New Import

public class KitGUIListener implements Listener {

    private final FoliaCore plugin;

    public KitGUIListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 1. Basic Checks
        if (!event.getView().getTitle().equals(KitGUI.GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        ItemMeta meta = clickedItem.getItemMeta();

        if (meta == null) return;

        // 2. The Fix: Read the Hidden Data
        NamespacedKey key = new NamespacedKey(plugin, "kit_key");

        // If the item doesn't have our tag, it's not a kit button.
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        // 3. Get the exact kit name directly
        String kitName = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        Kit kit = plugin.getKitManager().getKit(kitName);

        if (kit == null) {
            plugin.getMessenger().sendError(player, "Error: Kit data not found.");
            return;
        }

        player.closeInventory();
        plugin.getKitManager().giveKit(player, kit);
    }
}