package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GamemodeCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public GamemodeCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        GameMode gameMode;

        // Determine which gamemode to set based on command label or first arg
        if (label.equalsIgnoreCase("gms")) {
            gameMode = GameMode.SURVIVAL;
            player = args.length > 0 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
        } else if (label.equalsIgnoreCase("gmc")) {
            gameMode = GameMode.CREATIVE;
            player = args.length > 0 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
        } else if (label.equalsIgnoreCase("gma")) {
            gameMode = GameMode.ADVENTURE;
            player = args.length > 0 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
        } else if (label.equalsIgnoreCase("gmsp")) {
            gameMode = GameMode.SPECTATOR;
            player = args.length > 0 ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
        } else {
            // /gamemode command
            if (args.length == 0) {
                plugin.getMessenger().sendError(sender, "Usage: /gamemode <survival|creative|adventure|spectator> [player]");
                return true;
            }

            String gameModeStr = args[0].toLowerCase();
            switch (gameModeStr) {
                case "survival":
                case "s":
                case "0":
                    gameMode = GameMode.SURVIVAL;
                    break;
                case "creative":
                case "c":
                case "1":
                    gameMode = GameMode.CREATIVE;
                    break;
                case "adventure":
                case "a":
                case "2":
                    gameMode = GameMode.ADVENTURE;
                    break;
                case "spectator":
                case "sp":
                case "3":
                    gameMode = GameMode.SPECTATOR;
                    break;
                default:
                    gameMode = null;
            }

            if (gameMode == null) {
                plugin.getMessenger().sendError(sender, "Invalid gamemode. Use: survival, creative, adventure, or spectator.");
                return true;
            }

            player = args.length > 1 ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
        }

        if (player == null) {
            if (sender instanceof Player) {
                plugin.getMessenger().sendError(sender, "Player not found or is not online.");
            } else {
                plugin.getMessenger().sendError(sender, "Usage: /gamemode <survival|creative|adventure|spectator> [player]");
            }
            return true;
        }

        // Check permissions
        if (player.equals(sender)) {
            if (!player.hasPermission("foliacore.gamemode")) {
                plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
                return true;
            }
        } else {
            if (!sender.hasPermission("foliacore.gamemode.others")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
        }

        player.setGameMode(gameMode);

        String gmName = gameMode.toString().charAt(0) + gameMode.toString().substring(1).toLowerCase();
        plugin.getMessenger().sendSuccess(player, ChatColor.GOLD + "Your gamemode has been set to " + ChatColor.GREEN + gmName + ChatColor.GREEN + ".");
        if (!player.equals(sender)) {
            plugin.getMessenger().sendSuccess(sender, ChatColor.GOLD + player.getName() + ChatColor.GREEN + "'s gamemode has been set to " + ChatColor.GOLD + gmName + ChatColor.GREEN + ".");
        }

        return true;
    }
}
