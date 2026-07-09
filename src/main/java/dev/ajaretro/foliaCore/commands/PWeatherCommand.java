package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PWeatherCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public PWeatherCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore.pweather")) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(player, "Usage: /pweather <reset|sun|rain>");
            return true;
        }

        String arg = args[0].toLowerCase();
        if (arg.equals("reset") || arg.equals("normal")) {
            player.resetPlayerWeather();
            plugin.getMessenger().sendSuccess(player, "Your weather has been reset to synchronize with the server.");
            return true;
        }

        if (arg.equals("sun") || arg.equals("clear")) {
            player.setPlayerWeather(WeatherType.CLEAR);
            plugin.getMessenger().sendSuccess(player, "Your weather has been set to: SUN");
            return true;
        }

        if (arg.equals("rain") || arg.equals("storm") || arg.equals("downfall")) {
            player.setPlayerWeather(WeatherType.DOWNFALL);
            plugin.getMessenger().sendSuccess(player, "Your weather has been set to: RAIN");
            return true;
        }

        plugin.getMessenger().sendError(player, "Invalid weather type. Use reset, sun, or rain.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("foliacore.pweather")) {
            return List.of("reset", "sun", "rain");
        }
        return Collections.emptyList();
    }
}
