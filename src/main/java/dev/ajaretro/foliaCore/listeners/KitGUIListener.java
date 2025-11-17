package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Kit;
import dev.ajaretro.foliaCore.gui.KitGUI;
import net.kyori.adventure.text.Component; // <-- Make sure this is imported
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta; // <-- You may need this import

public class KitGUIListener implements Listener {

    private final FoliaCore plugin;

    public KitGUIListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(KitGUI.GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        // --- THIS IS THE FIX ---
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        // 1. Get the display name as a Component
        Component displayNameComponent = meta.displayName();
        if (displayNameComponent == null) {
            return;
        }

        // 2. Convert it to a plain string using our Messenger util
        String kitName = plugin.getMessenger().componentToString(displayNameComponent);
        // --- END OF FIX ---

        Kit kit = plugin.getKitManager().getKit(kitName);

        if (kit == null) {
            // This might happen if the name has extra text.
            // For safety, we'll just ignore the click.
            return;
        }

        player.closeInventory();
        plugin.getKitManager().giveKit(player, kit);
    }
}