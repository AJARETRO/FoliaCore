package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Toggles scoreboard (sidebar) visibility for players.
 * Allows players to hide or show their sidebar display.
 */
public class ScoreboardToggleCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Boolean> scoreboardState = new ConcurrentHashMap<>();

    public ScoreboardToggleCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();

        if (!player.hasPermission("foliacore.scoreboard.toggle")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        // Toggle the state
        boolean newState = !scoreboardState.getOrDefault(playerUuid, true);
        scoreboardState.put(playerUuid, newState);

        if (newState) {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Scoreboard is now " + ChatColor.GREEN + "visible.");
        } else {
            plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Scoreboard is now " + ChatColor.RED + "hidden.");
        }

        return true;
    }

    /**
     * Check if the player has their scoreboard enabled.
     * @param playerUuid the player's UUID
     * @return true if scoreboard is enabled (visible), false if hidden
     */
    public boolean isScoreboardEnabled(UUID playerUuid) {
        return scoreboardState.getOrDefault(playerUuid, true);
    }

    /**
     * Set scoreboard state for a player.
     * @param playerUuid the player's UUID
     * @param enabled true to show, false to hide
     */
    public void setScoreboardEnabled(UUID playerUuid, boolean enabled) {
        scoreboardState.put(playerUuid, enabled);
    }
}
