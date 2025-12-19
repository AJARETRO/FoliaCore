package dev.ajaretro.foliaCore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Centralized messaging utility.
 * Handles legacy color code serialization and standardizes plugin feedback.
 */
public class Messenger {

    private final String prefix;
    // Legacy serializer required for standard Minecraft color codes (&a, &b, etc.)
    private final LegacyComponentSerializer serializer;

    public Messenger(String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix) + " " + ChatColor.RESET;
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
    }

    public void sendMessage(@NotNull CommandSender sender, String message) {
        sender.sendMessage(prefix + ChatColor.WHITE + message);
    }

    public void sendError(@NotNull CommandSender sender, String errorMessage) {
        sender.sendMessage(prefix + ChatColor.RED + errorMessage);
    }

    public void sendSuccess(@NotNull CommandSender sender, String successMessage) {
        sender.sendMessage(prefix + ChatColor.GREEN + successMessage);
    }

    /**
     * Serializes an Adventure Component into a legacy String.
     * Crucial for compatibility with plugins relying on String-based comparisons (e.g. GUI listeners).
     */
    public String componentToString(Component component) {
        return serializer.serialize(component);
    }
}