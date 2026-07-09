package dev.ajaretro.foliaCore.listeners;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for item interaction to run bound powertool commands.
 */
public class PowertoolListener implements Listener {
    private final FoliaCore plugin;

    public PowertoolListener(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.PHYSICAL) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        String cmd = plugin.getPowertoolManager().getPowertool(player.getUniqueId(), item.getType());
        if (cmd != null && !cmd.isEmpty()) {
            event.setCancelled(true);
            
            // Execute on region-safe player scheduler
            player.getScheduler().run(plugin, task -> {
                player.performCommand(cmd);
            }, null);
        }
    }
}
