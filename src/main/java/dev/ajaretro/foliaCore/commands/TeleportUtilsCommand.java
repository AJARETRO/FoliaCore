package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class TeleportUtilsCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public TeleportUtilsCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigManager().isTeleportEnabled()) {
            plugin.getMessenger().sendError(sender, "Teleport module is disabled.");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("settpr")) {
            if (!sender.hasPermission("foliacore.settpr")) {
                plugin.getMessenger().sendError(sender, "You do not have permission.");
                return true;
            }
            handleSetTpr(sender, args);
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(player, "You do not have permission.");
            return true;
        }

        switch (cmd) {
            case "tpoffline":
                handleTpOffline(player, args);
                break;
            case "renamehome":
                handleRenameHome(player, args);
                break;
            case "tpall":
                handleTpAll(player);
                break;
            case "tpauto":
                handleTpAuto(player);
                break;
            case "tpacancel":
                handleTpaCancel(player);
                break;
            case "tpo":
                handleTpo(player, args);
                break;
            case "tpohere":
                handleTpoHere(player, args);
                break;
            case "tppos":
                handleTpPos(player, args);
                break;
            case "tpr":
                handleTpr(player);
                break;
            case "tptoggle":
                handleTpToggle(player);
                break;
        }
        return true;
    }

    private void handleSetTpr(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendError(sender, "Usage: /settpr <min> <max>");
            return;
        }
        try {
            int min = Integer.parseInt(args[0]);
            int max = Integer.parseInt(args[1]);
            plugin.getTeleportManager().setMinTprRange(min);
            plugin.getTeleportManager().setMaxTprRange(max);
            plugin.getMessenger().sendSuccess(sender, "TPR ranges set to Min: " + min + ", Max: " + max);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(sender, "Invalid range numbers.");
        }
    }

    private void handleTpOffline(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /tpoffline <player>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }
        Location loc = plugin.getTeleportManager().getLastLocation(target.getUniqueId());
        if (loc == null) {
            plugin.getMessenger().sendError(player, "No saved location found for offline player: " + target.getName());
            return;
        }
        player.teleportAsync(loc).thenAccept(success -> {
            if (success) {
                plugin.getMessenger().sendSuccess(player, "Teleported to offline location of " + target.getName());
            }
        });
    }

    private void handleRenameHome(Player player, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /renamehome <old_name> <new_name>");
            return;
        }
        String oldName = args[0].toLowerCase();
        String newName = args[1].toLowerCase();

        if (plugin.getTeleportManager().getHome(player.getUniqueId(), oldName) == null) {
            plugin.getMessenger().sendError(player, "Home '" + oldName + "' not found.");
            return;
        }

        plugin.getTeleportManager().renameHome(player.getUniqueId(), oldName, newName);
        plugin.getMessenger().sendSuccess(player, "Home '" + oldName + "' renamed to '" + newName + "'.");
    }

    private void handleTpAll(Player player) {
        Location loc = player.getLocation();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;
            FoliaScheduler.runAtEntity(plugin, p, () -> {
                p.teleportAsync(loc);
            });
        }
        plugin.getMessenger().sendSuccess(player, "Teleporting all players to you.");
    }

    private void handleTpAuto(Player player) {
        boolean auto = plugin.getTeleportManager().toggleAutoAccept(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Teleport auto-accept is now " + (auto ? "§aENABLED" : "§cDISABLED") + "§a.");
    }

    private void handleTpaCancel(Player player) {
        plugin.getTeleportManager().cleanupPlayer(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Cancelled pending TPA requests.");
    }

    private void handleTpo(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /tpo <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }
        player.teleportAsync(target.getLocation());
    }

    private void handleTpoHere(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /tpohere <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }
        Location loc = player.getLocation();
        FoliaScheduler.runAtEntity(plugin, target, () -> {
            target.teleportAsync(loc);
        });
    }

    private void handleTpPos(Player player, String[] args) {
        if (args.length < 3) {
            plugin.getMessenger().sendError(player, "Usage: /tppos <x> <y> <z> [world]");
            return;
        }
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            World w = player.getWorld();
            if (args.length >= 4) {
                w = Bukkit.getWorld(args[3]);
                if (w == null) {
                    plugin.getMessenger().sendError(player, "World not found.");
                    return;
                }
            }
            Location loc = new Location(w, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleportAsync(loc);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Invalid coordinates.");
        }
    }

    private void handleTpr(Player player) {
        int min = plugin.getTeleportManager().getMinTprRange();
        int max = plugin.getTeleportManager().getMaxTprRange();
        Random r = new Random();
        int dx = r.nextInt(max - min) + min;
        int dz = r.nextInt(max - min) + min;
        if (r.nextBoolean()) dx = -dx;
        if (r.nextBoolean()) dz = -dz;

        Location target = player.getLocation().add(dx, 0, dz);
        FoliaScheduler.runAtChunk(plugin, target.getWorld(), target.getBlockX() >> 4, target.getBlockZ() >> 4, () -> {
            int y = target.getWorld().getHighestBlockYAt(target);
            Location finalLoc = new Location(target.getWorld(), target.getX(), y + 1, target.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleportAsync(finalLoc).thenAccept(success -> {
                if (success) {
                    player.sendMessage("§aRandomly teleported!");
                }
            });
        });
    }

    private void handleTpToggle(Player player) {
        boolean disabled = plugin.getTeleportManager().toggleTpToggle(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "Teleportations to you are now " + (disabled ? "§cBLOCKED" : "§aALLOWED") + "§a.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
