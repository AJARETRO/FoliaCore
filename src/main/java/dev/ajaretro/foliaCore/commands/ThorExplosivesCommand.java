/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class ThorExplosivesCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;

    public ThorExplosivesCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        if (!player.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        switch (cmd) {
            case "antioch":
                handleAntioch(player);
                break;
            case "beezooka":
                handleBeezooka(player);
                break;
            case "fireball":
                handleFireball(player, args);
                break;
            case "lightning":
                handleLightning(player, args);
                break;
            case "nuke":
                handleNuke(player);
                break;
            case "spawnmob":
                handleSpawnMob(player, args);
                break;
            case "kittycannon":
                handleKittyCannon(player);
                break;
            case "tree":
                handleTree(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
        }
        return true;
    }

    private void handleAntioch(Player player) {
        Block target = player.getTargetBlockExact(50);
        if (target == null) {
            plugin.getMessenger().sendError(player, "No target block in sight.");
            return;
        }
        Location loc = target.getLocation().add(0, 1, 0);
        
        // Antioch requires TNT spawn at location
        FoliaScheduler.runAtLocation(plugin, loc, () -> {
            TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.TNT);
            tnt.setFuseTicks(40);
            Bukkit.broadcast(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("§6[Antioch] §e*grenade pin pulled*"));
        });
    }

    private void handleBeezooka(Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize().multiply(1.5);
        
        // Spawn bee on player region thread
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            Bee bee = (Bee) player.getWorld().spawnEntity(loc.add(direction), EntityType.BEE);
            bee.setVelocity(direction);
            
            // Periodically check/move the bee until it hits something, then detonate
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
                if (!bee.isValid()) {
                    task.cancel();
                    return;
                }
                
                FoliaScheduler.runAtEntity(plugin, bee, () -> {
                    if (bee.isOnGround() || bee.getLocation().getBlock().getType().isSolid()) {
                        bee.getWorld().createExplosion(bee.getLocation(), 4.0f, false, true);
                        bee.remove();
                    } else {
                        bee.setVelocity(bee.getVelocity().normalize().multiply(1.2));
                    }
                });
            }, 50, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
        });
        player.sendMessage("§aBZZZZZZ! Beezooka fired!");
    }

    private void handleFireball(Player player, String[] args) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();
        double multiplier = 1.0;
        if (args.length > 0) {
            try {
                multiplier = Double.parseDouble(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        final double finalMult = multiplier;

        FoliaScheduler.runAtEntity(plugin, player, () -> {
            Fireball fireball = (Fireball) player.getWorld().spawnEntity(loc.add(direction), EntityType.FIREBALL);
            fireball.setShooter(player);
            fireball.setDirection(direction.multiply(finalMult));
        });
        player.sendMessage("§cFireball launched!");
    }

    private void handleLightning(Player player, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(player, "Player not found.");
                return;
            }
            Location loc = target.getLocation();
            FoliaScheduler.runAtLocation(plugin, loc, () -> {
                loc.getWorld().strikeLightning(loc);
                player.sendMessage("§eLightning struck " + target.getName());
            });
            return;
        }

        Block targetBlock = player.getTargetBlockExact(50);
        if (targetBlock == null) {
            plugin.getMessenger().sendError(player, "No target block in sight.");
            return;
        }
        Location loc = targetBlock.getLocation();
        FoliaScheduler.runAtLocation(plugin, loc, () -> {
            loc.getWorld().strikeLightning(loc);
        });
    }

    private void handleNuke(Player player) {
        Location loc = player.getLocation();
        FoliaScheduler.runAtLocation(plugin, loc, () -> {
            for (int x = -10; x <= 10; x += 5) {
                for (int z = -10; z <= 10; z += 5) {
                    Location tntLoc = loc.clone().add(x, 20, z);
                    TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(tntLoc, EntityType.TNT);
                    tnt.setFuseTicks(80);
                }
            }
            player.sendMessage("§4Tactical Nuke incoming!");
        });
    }

    private void handleSpawnMob(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /spawnmob <mob_type> [amount]");
            return;
        }

        String typeStr = args[0].toUpperCase();
        EntityType type;
        try {
            type = EntityType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            plugin.getMessenger().sendError(player, "Invalid mob type.");
            return;
        }

        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }
        final int count = Math.min(amount, 50);

        Block target = player.getTargetBlockExact(50);
        Location spawnLoc = (target != null) ? target.getLocation().add(0, 1, 0) : player.getLocation();

        FoliaScheduler.runAtLocation(plugin, spawnLoc, () -> {
            for (int i = 0; i < count; i++) {
                spawnLoc.getWorld().spawnEntity(spawnLoc, type);
            }
            player.sendMessage("§aSpawned " + count + "x " + type.name());
        });
    }

    private void handleKittyCannon(Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize().multiply(1.5);
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            Ocelot cat = (Ocelot) player.getWorld().spawnEntity(loc.add(direction), EntityType.OCELOT);
            cat.setVelocity(direction);
            
            // Detonate after 1 second (20 ticks)
            player.getScheduler().runDelayed(plugin, task -> {
                FoliaScheduler.runAtEntity(plugin, cat, () -> {
                    cat.getWorld().createExplosion(cat.getLocation(), 2.0f, false, false);
                    cat.remove();
                });
            }, null, 20L);
        });
    }

    private void handleTree(Player player, String[] args) {
        Block target = player.getTargetBlockExact(50);
        if (target == null) {
            plugin.getMessenger().sendError(player, "No target block in sight.");
            return;
        }
        Location loc = target.getLocation().add(0, 1, 0);

        TreeType type = TreeType.TREE;
        if (args.length > 0) {
            try {
                type = TreeType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        final TreeType finalType = type;

        FoliaScheduler.runAtLocation(plugin, loc, () -> {
            boolean success = loc.getWorld().generateTree(loc, finalType);
            if (success) {
                player.sendMessage("§aTree grown!");
            } else {
                plugin.getMessenger().sendError(player, "Failed to grow tree here.");
            }
        });
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /remove <mobs|drops|all> [radius]");
            return;
        }

        String targetType = args[0].toLowerCase();
        int radius = 20;
        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }
        final int r = radius;

        FoliaScheduler.runAtEntity(plugin, player, () -> {
            List<Entity> list = player.getNearbyEntities(r, r, r);
            int removed = 0;
            for (Entity e : list) {
                if (e instanceof Player) continue;
                
                boolean match = false;
                if (targetType.equals("all")) {
                    match = true;
                } else if (targetType.equals("mobs") && e instanceof Mob) {
                    match = true;
                } else if (targetType.equals("drops") && e instanceof Item) {
                    match = true;
                }
                
                if (match) {
                    e.remove();
                    removed++;
                }
            }
            player.sendMessage("§aRemoved " + removed + " entities.");
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("spawnmob") && args.length == 1) {
            List<String> list = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (type.isSpawnable() && type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(type.name());
                }
            }
            return list;
        }
        if (cmd.equals("tree") && args.length == 1) {
            List<String> list = new ArrayList<>();
            for (TreeType type : TreeType.values()) {
                if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(type.name());
                }
            }
            return list;
        }
        if (cmd.equals("remove") && args.length == 1) {
            return List.of("mobs", "drops", "all");
        }
        return Collections.emptyList();
    }
}
