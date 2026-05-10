package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class WeatherCommand implements CommandExecutor {

    private final FoliaCore plugin;

    public WeatherCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliacore.weather")) {
            plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessenger().sendError(sender, "Usage: /weather <sun|rain|storm>");
            return true;
        }

        WeatherType weather;
        String weatherStr = args[0].toLowerCase();

        if (weatherStr.equals("sun") || weatherStr.equals("clear")) {
            weather = WeatherType.CLEAR;
        } else if (weatherStr.equals("rain")) {
            weather = WeatherType.DOWNFALL;
        } else if (weatherStr.equals("storm") || weatherStr.equals("thunder")) {
            weather = WeatherType.DOWNFALL;
        } else {
            plugin.getMessenger().sendError(sender, "Invalid weather. Use: sun, rain, or storm.");
            return true;
        }

        for (World world : Bukkit.getWorlds()) {
            world.setWeatherDuration(6000);
            world.setStorm(weather == WeatherType.DOWNFALL);
            
            if (weatherStr.equals("storm") || weatherStr.equals("thunder")) {
                world.setThundering(true);
                world.setThunderDuration(6000);
            } else {
                world.setThundering(false);
            }
        }

        plugin.getMessenger().sendSuccess(sender, ChatColor.GREEN + "Set weather to " + ChatColor.GOLD + weatherStr);

        return true;
    }
}
