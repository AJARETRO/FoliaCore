package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.utils.FoliaScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientServerCommand implements CommandExecutor, TabCompleter {
    private final FoliaCore plugin;
    private final ConcurrentHashMap<UUID, Boolean> afkPlayers = new ConcurrentHashMap<>();

    public ClientServerCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("motd")) {
            sender.sendMessage("§6§lMOTD: §f" + Bukkit.getMotd());
            return true;
        }

        if (cmd.equals("list")) {
            handleList(sender);
            return true;
        }

        if (cmd.equals("seen")) {
            handleSeen(sender, args);
            return true;
        }

        if (cmd.equals("kickall")) {
            if (!sender.hasPermission("foliacore.kickall")) {
                plugin.getMessenger().sendError(sender, "You do not have permission to use this command.");
                return true;
            }
            handleKickAll(sender, args);
            return true;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessenger().sendError(sender, "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("foliacore." + cmd)) {
            plugin.getMessenger().sendError(player, "You do not have permission to use this command.");
            return true;
        }

        switch (cmd) {
            case "afk":
                handleAfk(player);
                break;
            case "compass":
                handleCompass(player);
                break;
            case "enchant":
                handleEnchant(player, args);
                break;
            case "exp":
                handleExp(player, args);
                break;
            case "ext":
                handleExt(player, args);
                break;
            case "firework":
                handleFirework(player, args);
                break;
            case "jump":
                handleJump(player);
                break;
            case "me":
                handleMe(player, args);
                break;
            case "recipe":
                handleRecipe(player, args);
                break;
            case "more":
                handleMore(player);
                break;
            case "near":
                handleNear(player, args);
                break;
            case "skull":
                handleSkull(player, args);
                break;
            case "speed":
                handleSpeed(player, args);
                break;
            case "sudo":
                handleSudo(player, args);
                break;
            case "suicide":
                handleSuicide(player);
                break;
            case "editsign":
                handleEditSign(player, args);
                break;
            case "thunder":
                handleThunder(player);
                break;
            case "playtime":
                handlePlayTime(player, args);
                break;
            case "potion":
                handlePotion(player, args);
                break;
        }
        return true;
    }

    private void handlePotion(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /potion <effect> [duration_seconds] [amplifier]");
            return;
        }
        org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getByName(args[0].toUpperCase());
        if (type == null) {
            plugin.getMessenger().sendError(player, "Potion effect type not found.");
            return;
        }
        int duration = 30 * 20; // 30 seconds in ticks
        if (args.length >= 2) {
            try {
                duration = Integer.parseInt(args[1]) * 20;
            } catch (NumberFormatException ignored) {}
        }
        int amplifier = 0;
        if (args.length >= 3) {
            try {
                amplifier = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}
        }

        final org.bukkit.potion.PotionEffectType finalType = type;
        final int finalDur = duration;
        final int finalAmp = amplifier;

        FoliaScheduler.runAtEntity(plugin, player, () -> {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(finalType, finalDur, finalAmp));
            player.sendMessage("§aApplied potion effect: " + finalType.getName() + " level " + (finalAmp + 1));
        });
    }

    private void handlePlayTime(Player player, String[] args) {
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(player, "Player not found or is offline.");
                return;
            }
        }

        final Player finalTarget = target;
        FoliaScheduler.runAtEntity(plugin, finalTarget, () -> {
            long ticks = finalTarget.getStatistic(Statistic.PLAY_ONE_MINUTE);
            long seconds = ticks / 20;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            long remHours = hours % 24;
            long remMinutes = minutes % 60;

            String timeStr = days + " days, " + remHours + " hours, " + remMinutes + " minutes";
            player.sendMessage("§aPlaytime for §e" + finalTarget.getName() + "§a: §f" + timeStr);
        });
    }

    private void handleThunder(Player player) {
        FoliaScheduler.runAtLocation(plugin, player.getLocation(), () -> {
            boolean thundering = !player.getWorld().isThundering();
            player.getWorld().setThundering(thundering);
            player.sendMessage("§aThunder is now " + (thundering ? "§aENABLED" : "§cDISABLED") + "§a.");
        });
    }

    private void handleAfk(Player player) {
        boolean afk = afkPlayers.compute(player.getUniqueId(), (k, v) -> v == null || !v);
        String msg = afk ? "§e" + player.getName() + " is now AFK." : "§e" + player.getName() + " is no longer AFK.";
        Bukkit.broadcast(Component.text(msg));
    }

    private void handleCompass(Player player) {
        float yaw = player.getLocation().getYaw();
        String direction;
        if (yaw < 0) yaw += 360;
        yaw %= 360;
        if (yaw >= 337.5 || yaw < 22.5) direction = "SOUTH";
        else if (yaw >= 22.5 && yaw < 67.5) direction = "SOUTH-WEST";
        else if (yaw >= 67.5 && yaw < 112.5) direction = "WEST";
        else if (yaw >= 112.5 && yaw < 157.5) direction = "NORTH-WEST";
        else if (yaw >= 157.5 && yaw < 202.5) direction = "NORTH";
        else if (yaw >= 202.5 && yaw < 247.5) direction = "NORTH-EAST";
        else if (yaw >= 247.5 && yaw < 292.5) direction = "EAST";
        else direction = "SOUTH-EAST";
        
        player.sendMessage("§aCompass: §e" + direction + " §7(" + String.format("%.1f", yaw) + "°)");
    }

    private void handleEnchant(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /enchant <enchantment> [level]");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must hold an item.");
            return;
        }

        String enchantName = args[0].toLowerCase();
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
        if (enchantment == null) {
            plugin.getMessenger().sendError(player, "Enchantment not found.");
            return;
        }

        int level = 1;
        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        final Enchantment finalEnch = enchantment;
        final int finalLevel = level;
        
        // Modify inventory safely on regional thread
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            item.addUnsafeEnchantment(finalEnch, finalLevel);
            player.sendMessage("§aEnchanted item with " + finalEnch.getKey().getKey() + " level " + finalLevel);
        });
    }

    private void handleExp(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§aYour XP Level: §e" + player.getLevel() + " §7(" + player.getTotalExperience() + " total XP)");
            return;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("give") && args.length >= 2) {
            int amount = Integer.parseInt(args[1]);
            player.giveExp(amount);
            player.sendMessage("§aGave §e" + amount + " §aXP.");
        } else if (sub.equals("set") && args.length >= 2) {
            int level = Integer.parseInt(args[1]);
            player.setLevel(level);
            player.sendMessage("§aSet level to §e" + level);
        }
    }

    private void handleExt(Player player, String[] args) {
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                plugin.getMessenger().sendError(player, "Player not found.");
                return;
            }
        }
        final Player finalTarget = target;
        FoliaScheduler.runAtEntity(plugin, finalTarget, () -> {
            finalTarget.setFireTicks(0);
            player.sendMessage("§aExtinguished fire for " + finalTarget.getName());
        });
    }

    private void handleFirework(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.FIREWORK_ROCKET) {
            plugin.getMessenger().sendError(player, "You must hold a firework rocket.");
            return;
        }
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            meta.setPower(2);
            item.setItemMeta(meta);
            player.sendMessage("§aModified firework metadata.");
        });
    }

    private void handleJump(Player player) {
        Block block = player.getTargetBlockExact(50);
        if (block == null) {
            plugin.getMessenger().sendError(player, "No target block in sight.");
            return;
        }
        Location loc = block.getLocation().add(0, 1, 0);
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());
        player.teleportAsync(loc);
    }

    private void handleMe(Player player, String[] args) {
        if (args.length < 1) return;
        String action = String.join(" ", args);
        Bukkit.broadcast(Component.text("§5* " + player.getName() + " " + action));
    }

    private void handleMore(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            plugin.getMessenger().sendError(player, "You must hold an item.");
            return;
        }
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            item.setAmount(item.getMaxStackSize());
            player.sendMessage("§aStack filled to maximum size.");
        });
    }

    private void handleNear(Player player, String[] args) {
        int radius = 100;
        if (args.length > 0) {
            try {
                radius = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        final int r = radius;

        FoliaScheduler.runAtEntity(plugin, player, () -> {
            List<Player> nearby = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(player.getWorld()) && p.getLocation().distanceSquared(player.getLocation()) <= (r * r) && !p.equals(player)) {
                    nearby.add(p);
                }
            }
            if (nearby.isEmpty()) {
                player.sendMessage("§cNo nearby players found.");
            } else {
                player.sendMessage("§aNearby players (within " + r + " blocks):");
                for (Player p : nearby) {
                    player.sendMessage("§e- " + p.getName() + " §7(" + String.format("%.1f", p.getLocation().distance(player.getLocation())) + "m)");
                }
            }
        });
    }

    private void handleSeen(CommandSender sender, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(sender, "Usage: /seen <player>");
            return;
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
        if (op == null) {
            plugin.getMessenger().sendError(sender, "Player not found.");
            return;
        }
        if (op.isOnline()) {
            sender.sendMessage("§e" + op.getName() + " §ais currently §2§lONLINE§a.");
        } else {
            long lastSeen = op.getLastSeen();
            if (lastSeen == 0) {
                sender.sendMessage("§e" + op.getName() + " §chas never logged in.");
            } else {
                Date d = new Date(lastSeen);
                sender.sendMessage("§e" + op.getName() + " §7was last seen on: §f" + d.toString());
            }
        }
    }

    private void handleSkull(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /skull <player_name>");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.PLAYER_HEAD) {
            plugin.getMessenger().sendError(player, "You must hold a player head.");
            return;
        }
        final String owner = args[0];
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            item.setItemMeta(meta);
            player.sendMessage("§aSkull head owner set to: " + owner);
        });
    }

    private void handleSpeed(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /speed <1-10>");
            return;
        }
        float speedVal;
        try {
            speedVal = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Speed must be a number.");
            return;
        }
        if (speedVal < 1 || speedVal > 10) {
            plugin.getMessenger().sendError(player, "Speed must be between 1 and 10.");
            return;
        }
        final float val = speedVal / 10f;
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            if (player.isFlying()) {
                player.setFlySpeed(val);
                player.sendMessage("§aFly speed set to " + args[0]);
            } else {
                player.setWalkSpeed(val);
                player.sendMessage("§aWalk speed set to " + args[0]);
            }
        });
    }

    private void handleSudo(Player player, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /sudo <player> <command...>");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        String cmd = builder.toString().trim();
        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }
        final String finalCmd = cmd;
        FoliaScheduler.runAtEntity(plugin, target, () -> {
            target.performCommand(finalCmd);
            player.sendMessage("§aForced " + target.getName() + " to execute /" + finalCmd);
        });
    }

    private void handleSuicide(Player player) {
        FoliaScheduler.runAtEntity(plugin, player, () -> {
            player.setHealth(0.0);
            player.sendMessage("§cYou took your own life.");
        });
    }

    private void handleEditSign(Player player, String[] args) {
        Block block = player.getTargetBlockExact(5);
        if (block == null || !(block.getState() instanceof Sign)) {
            plugin.getMessenger().sendError(player, "You must look at a sign to edit it.");
            return;
        }
        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /editsign <line_1_indexed> <text>");
            return;
        }
        int line;
        try {
            line = Integer.parseInt(args[0]) - 1;
        } catch (NumberFormatException e) {
            plugin.getMessenger().sendError(player, "Line must be a number.");
            return;
        }
        if (line < 0 || line > 3) {
            plugin.getMessenger().sendError(player, "Sign lines are 1 to 4.");
            return;
        }
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            textBuilder.append(args[i]).append(" ");
        }
        final String text = textBuilder.toString().trim();
        final int finalLine = line;

        FoliaScheduler.runAtLocation(plugin, block.getLocation(), () -> {
            Sign sign = (Sign) block.getState();
            sign.setLine(finalLine, text);
            sign.update(true);
            player.sendMessage("§aSign line " + (finalLine + 1) + " updated.");
        });
    }

    private void handleList(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        builder.append("§6§lOnline Players (").append(Bukkit.getOnlinePlayers().size()).append("): §f");
        List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            names.add(p.getName() + (afkPlayers.getOrDefault(p.getUniqueId(), false) ? " §7[AFK]" : ""));
        }
        builder.append(String.join(", ", names));
        sender.sendMessage(builder.toString());
    }

    private void handleKickAll(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }
        String reason = builder.toString().trim();
        if (reason.isEmpty()) {
            reason = "Kicked by administrator.";
        }
        final String kickReason = reason;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(sender)) continue;
            // Kick them on their region/player thread
            FoliaScheduler.runAtEntity(plugin, p, () -> {
                p.kick(Component.text(kickReason));
            });
        }
        sender.sendMessage("§aKicked all online players.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (args.length == 1) {
            if (cmd.equals("ext") || cmd.equals("seen") || cmd.equals("skull") || cmd.equals("sudo") || cmd.equals("playtime")) {
                List<String> list = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(p.getName());
                    }
                }
                return list;
            }
            if (cmd.equals("exp")) {
                return List.of("give", "set");
            }
            if (cmd.equals("potion")) {
                List<String> list = new ArrayList<>();
                for (org.bukkit.potion.PotionEffectType type : org.bukkit.potion.PotionEffectType.values()) {
                    if (type.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(type.getName().toLowerCase());
                    }
                }
                return list;
            }
            if (cmd.equals("recipe")) {
                List<String> list = new ArrayList<>();
                for (Material mat : Material.values()) {
                    if (mat.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(mat.name().toLowerCase());
                    }
                }
                return list;
            }
        }
        return Collections.emptyList();
    }
    private void handleRecipe(Player player, String[] args) {
        if (args.length < 1) {
            plugin.getMessenger().sendError(player, "Usage: /recipe <item_type>");
            return;
        }
        Material mat = Material.matchMaterial(args[0]);
        if (mat == null) {
            plugin.getMessenger().sendError(player, "Material not found.");
            return;
        }
        List<org.bukkit.inventory.Recipe> recipes = Bukkit.getRecipesFor(new ItemStack(mat));
        if (recipes.isEmpty()) {
            player.sendMessage("§cNo crafting recipe found for: " + mat.name());
            return;
        }
        player.sendMessage("§8§m═════════════§r §6§lRECIPE FOR: §e" + mat.name() + " §8§m═════════════");
        for (org.bukkit.inventory.Recipe r : recipes) {
            if (r instanceof org.bukkit.inventory.ShapedRecipe) {
                org.bukkit.inventory.ShapedRecipe shaped = (org.bukkit.inventory.ShapedRecipe) r;
                player.sendMessage("§7Shaped Recipe grid:");
                for (String line : shaped.getShape()) {
                    player.sendMessage("  §f" + line);
                }
                player.sendMessage("§7Ingredients:");
                for (Map.Entry<Character, ItemStack> entry : shaped.getIngredientMap().entrySet()) {
                    if (entry.getValue() != null) {
                        player.sendMessage("  §e" + entry.getKey() + " §7= §f" + entry.getValue().getType().name());
                    }
                }
                break;
            }
        }
        player.sendMessage("§8§m═════════════════════════════════════════");
    }
}
