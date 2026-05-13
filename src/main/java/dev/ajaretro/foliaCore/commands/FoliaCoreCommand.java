package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Root FoliaCore command that exposes help and version information.
 */
public class FoliaCoreCommand implements CommandExecutor {

    private static final List<HelpEntry> HELP_ENTRIES = List.of(
            new HelpEntry("Core", "/foliacore", "foliacore.help", "Show the command list and your available tools.", "foliacore.help"),
            new HelpEntry("Core", "/foliacore reload", "foliacore.reload", "Reload the plugin configuration.", "foliacore.reload"),
            new HelpEntry("Core", "/foliacore version", "foliacore.version", "Show the plugin version.", "foliacore.version"),

            new HelpEntry("Social and Chat", "/msg", "foliacore.msg", "Send private messages.", "foliacore.msg"),
            new HelpEntry("Social and Chat", "/reply", "foliacore.reply", "Reply to the last private message.", "foliacore.reply"),
            new HelpEntry("Social and Chat", "/mail", "foliacore.mail.send / foliacore.mail.read / foliacore.mail.clear", "Send, read, or clear mailbox messages.", "foliacore.mail.send", "foliacore.mail.read", "foliacore.mail.clear"),
            new HelpEntry("Social and Chat", "/nick", "foliacore.nick / foliacore.nick.color", "Change your nickname.", "foliacore.nick", "foliacore.nick.color"),
            new HelpEntry("Social and Chat", "/realname", "foliacore.realname", "Resolve a nickname back to a player.", "foliacore.realname"),
            new HelpEntry("Social and Chat", "/block", "foliacore.block", "Block a player from contacting you.", "foliacore.block"),
            new HelpEntry("Social and Chat", "/unblock", "foliacore.unblock", "Remove a player block.", "foliacore.unblock"),
            new HelpEntry("Social and Chat", "/chat", "foliacore.chat.global / foliacore.chat.world / foliacore.chat.regional", "Switch between chat modes.", "foliacore.chat.global", "foliacore.chat.world", "foliacore.chat.regional"),
            new HelpEntry("Social and Chat", "/mute", "foliacore.mute", "Mute a player.", "foliacore.mute"),
            new HelpEntry("Social and Chat", "/unmute", "foliacore.unmute", "Unmute a player.", "foliacore.unmute"),
            new HelpEntry("Social and Chat", "/socialspy", "foliacore.socialspy", "View private social messages.", "foliacore.socialspy"),
            new HelpEntry("Social and Chat", "/staffchat /sc", "foliacore.staffchat", "Use the staff channel.", "foliacore.staffchat"),

            new HelpEntry("Teleport and Travel", "/sethome", "foliacore.sethome", "Save a home location.", "foliacore.sethome"),
            new HelpEntry("Teleport and Travel", "/home", "foliacore.home", "Teleport to your home.", "foliacore.home"),
            new HelpEntry("Teleport and Travel", "/homes", "foliacore.homes.list", "List your saved homes.", "foliacore.homes.list"),
            new HelpEntry("Teleport and Travel", "/delhome", "foliacore.delhome", "Delete one of your homes.", "foliacore.delhome"),
            new HelpEntry("Teleport and Travel", "/tpa", "foliacore.tpa", "Request teleportation to a player.", "foliacore.tpa"),
            new HelpEntry("Teleport and Travel", "/tpahere", "foliacore.tpahere", "Request a player to come to you.", "foliacore.tpahere"),
            new HelpEntry("Teleport and Travel", "/tpaccept", "foliacore.tpaccept", "Accept a teleport request.", "foliacore.tpaccept"),
            new HelpEntry("Teleport and Travel", "/tpdeny", "foliacore.tpdeny", "Deny a teleport request.", "foliacore.tpdeny"),
            new HelpEntry("Teleport and Travel", "/spawn", "foliacore.spawn", "Return to spawn.", "foliacore.spawn"),
            new HelpEntry("Teleport and Travel", "/back", "foliacore.back", "Return to your previous location.", "foliacore.back"),
            new HelpEntry("Teleport and Travel", "/warp", "foliacore.warp.all / foliacore.warp.<warpname>", "Use a warp by global or per-warp access.", "foliacore.warp.all"),
            new HelpEntry("Teleport and Travel", "/warps", "foliacore.warps.list", "List the warps you are allowed to see.", "foliacore.warps.list"),
            new HelpEntry("Teleport and Travel", "/setwarp", "foliacore.setwarp", "Create a warp.", "foliacore.setwarp"),
            new HelpEntry("Teleport and Travel", "/delwarp", "foliacore.delwarp", "Remove a warp.", "foliacore.delwarp"),
            new HelpEntry("Teleport and Travel", "/setspawn", "foliacore.setspawn", "Set the server spawn point.", "foliacore.setspawn"),
            new HelpEntry("Teleport and Travel", "/setfirstspawn", "foliacore.setfirstspawn", "Set the first join spawn point.", "foliacore.setfirstspawn"),
            new HelpEntry("Teleport and Travel", "/tp", "foliacore.tp / foliacore.tp.others", "Teleport yourself or other players.", "foliacore.tp", "foliacore.tp.others"),
            new HelpEntry("Teleport and Travel", "/tphere", "foliacore.tphere", "Bring another player to you.", "foliacore.tphere"),
            new HelpEntry("Teleport and Travel", "/gps", "foliacore.gps", "Use the GPS helper.", "foliacore.gps"),

            new HelpEntry("Teams and Kits", "/team", "foliacore.team.create / foliacore.team.disband / foliacore.team.invite / foliacore.team.accept / foliacore.team.leave / foliacore.team.kick", "Manage teams.", "foliacore.team.create", "foliacore.team.disband", "foliacore.team.invite", "foliacore.team.accept", "foliacore.team.leave", "foliacore.team.kick"),
            new HelpEntry("Teams and Kits", "/kit", "foliacore.kit", "Open the kit GUI or claim an allowed kit.", "foliacore.kit"),
            new HelpEntry("Teams and Kits", "/createkit", "foliacore.kit.admin", "Create a new kit.", "foliacore.kit.admin"),
            new HelpEntry("Teams and Kits", "/delkit", "foliacore.kit.admin", "Remove a kit.", "foliacore.kit.admin"),

            new HelpEntry("Utility and Player Tools", "/calc", "foliacore.calc", "Run the calculator command.", "foliacore.calc"),
            new HelpEntry("Utility and Player Tools", "/trash /dispose", "foliacore.trash", "Open the trash inventory.", "foliacore.trash"),
            new HelpEntry("Utility and Player Tools", "/workbench /wb", "foliacore.workbench", "Open a crafting table anywhere.", "foliacore.workbench"),
            new HelpEntry("Utility and Player Tools", "/hat", "foliacore.hat", "Wear the item in your hand as a hat.", "foliacore.hat"),
            new HelpEntry("Utility and Player Tools", "/enderchest /ec", "foliacore.enderchest / foliacore.enderchest.others", "Open your ender chest or another player's.", "foliacore.enderchest", "foliacore.enderchest.others"),
            new HelpEntry("Utility and Player Tools", "/ping", "foliacore.ping / foliacore.ping.others", "Check ping for yourself or another player.", "foliacore.ping", "foliacore.ping.others"),
            new HelpEntry("Utility and Player Tools", "/scoreboard /sidebar", "foliacore.scoreboard.toggle", "Toggle the sidebar display.", "foliacore.scoreboard.toggle"),
            new HelpEntry("Utility and Player Tools", "/feed", "foliacore.feed / foliacore.feed.others", "Feed yourself or another player.", "foliacore.feed", "foliacore.feed.others"),
            new HelpEntry("Utility and Player Tools", "/fly", "foliacore.fly / foliacore.fly.others", "Toggle flight.", "foliacore.fly", "foliacore.fly.others"),
            new HelpEntry("Utility and Player Tools", "/heal", "foliacore.heal / foliacore.heal.others", "Heal yourself or another player.", "foliacore.heal", "foliacore.heal.others"),
            new HelpEntry("Utility and Player Tools", "/god", "foliacore.god / foliacore.god.others", "Toggle invulnerability.", "foliacore.god", "foliacore.god.others"),
            new HelpEntry("Utility and Player Tools", "/repair", "foliacore.repair / foliacore.repair.all / foliacore.repair.others / foliacore.repair.others.all", "Repair held items, inventory, or another player.", "foliacore.repair", "foliacore.repair.all", "foliacore.repair.others", "foliacore.repair.others.all"),
            new HelpEntry("Utility and Player Tools", "/give", "foliacore.give", "Give items to a player.", "foliacore.give"),
            new HelpEntry("Utility and Player Tools", "/clear", "foliacore.clear / foliacore.clear.others", "Clear inventory.", "foliacore.clear", "foliacore.clear.others"),
            new HelpEntry("Utility and Player Tools", "/invsee", "foliacore.invsee", "Inspect another player's inventory.", "foliacore.invsee"),
            new HelpEntry("Utility and Player Tools", "/gamemode /gms /gmc /gma /gmsp", "foliacore.gamemode / foliacore.gamemode.others", "Change gamemode for yourself or another player.", "foliacore.gamemode", "foliacore.gamemode.others"),

            new HelpEntry("Administration and World Control", "/broadcast", "foliacore.broadcast", "Broadcast a server-wide announcement.", "foliacore.broadcast"),
            new HelpEntry("Administration and World Control", "/time", "foliacore.time", "Change the server time.", "foliacore.time"),
            new HelpEntry("Administration and World Control", "/weather", "foliacore.weather", "Change the server weather.", "foliacore.weather"),
            new HelpEntry("Administration and World Control", "/status", "foliacore.status", "View plugin status information.", "foliacore.status"),
            new HelpEntry("Administration and World Control", "/clearchat", "foliacore.clearchat / foliacore.clearchat.bypass", "Clear the public chat.", "foliacore.clearchat", "foliacore.clearchat.bypass"),
            new HelpEntry("Administration and World Control", "/vanish", "foliacore.vanish", "Toggle vanish mode.", "foliacore.vanish"),
            new HelpEntry("Administration and World Control", "/antiraid", "foliacore.admin.antiraid", "Access anti-raid tools.", "foliacore.admin.antiraid"),
            new HelpEntry("Administration and World Control", "/ban", "foliacore.ban / foliacore.ban.exempt", "Ban a player.", "foliacore.ban", "foliacore.ban.exempt"),
            new HelpEntry("Administration and World Control", "/tempban", "foliacore.tempban / foliacore.ban.exempt", "Temporarily ban a player.", "foliacore.tempban", "foliacore.ban.exempt"),
            new HelpEntry("Administration and World Control", "/unban", "foliacore.unban", "Remove a ban.", "foliacore.unban"),
            new HelpEntry("Administration and World Control", "/kick", "foliacore.kick / foliacore.kick.exempt", "Kick a player.", "foliacore.kick", "foliacore.kick.exempt"),
            new HelpEntry("Administration and World Control", "/marker", "foliacore.marker.set / foliacore.marker.delete / foliacore.marker.list", "Manage markers.", "foliacore.marker.set", "foliacore.marker.delete", "foliacore.marker.list")
    );

    private final FoliaCore plugin;

    public FoliaCoreCommand(FoliaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("commands")) {
            showHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            showVersion(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfiguration(sender);
            return true;
        }

        plugin.getMessenger().sendError(sender, "Unknown subcommand. Use /foliacore help.");
        return true;
    }

    private void showHelp(CommandSender sender) {
        plugin.getMessenger().sendMessage(sender, "");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Help ===");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Use /foliacore version for release info.");

        String currentCategory = null;
        for (HelpEntry entry : HELP_ENTRIES) {
            if (!isVisible(sender, entry)) {
                continue;
            }

            if (!entry.category.equals(currentCategory)) {
                currentCategory = entry.category;
                plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + currentCategory + ChatColor.DARK_GRAY + ":");
            }

            plugin.getMessenger().sendMessage(sender,
                    ChatColor.DARK_AQUA + " - "
                            + ChatColor.WHITE + entry.command
                            + ChatColor.DARK_GRAY + " | "
                            + ChatColor.GRAY + entry.description);
        }

        plugin.getMessenger().sendMessage(sender, ChatColor.DARK_GRAY + "Commands shown here still respect server-side toggles.");
        plugin.getMessenger().sendMessage(sender, "");
    }

    private void showVersion(CommandSender sender) {
        plugin.getMessenger().sendMessage(sender, "");
        plugin.getMessenger().sendMessage(sender, ChatColor.GOLD + "=== FoliaCore Version ===");
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Plugin: " + ChatColor.WHITE + plugin.getDescription().getName());
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        plugin.getMessenger().sendMessage(sender, ChatColor.GRAY + "Backend: " + ChatColor.WHITE + "Folia");
        plugin.getMessenger().sendMessage(sender, "");
    }

    private void reloadConfiguration(CommandSender sender) {
        plugin.getConfigManager().load();
        plugin.getMessenger().sendSuccess(sender, "Configuration reloaded.");

        if (plugin.getDisplayManager() != null) {
            plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> {
                for (var player : plugin.getServer().getOnlinePlayers()) {
                    plugin.getDisplayManager().refreshPlayer(player);
                }
            });
        }
    }

    private boolean isVisible(CommandSender sender, HelpEntry entry) {
        if (!plugin.getConfigManager().isCommandEnabled(entry.primaryCommand)) {
            return false;
        }

        for (String permission : entry.permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    private static final class HelpEntry {
        private final String category;
        private final String command;
        private final String primaryCommand;
        private final String description;
        private final String[] permissions;

        private HelpEntry(String category, String command, String description, String... permissions) {
            this.category = category;
            this.command = command;
            this.description = description;
            this.permissions = permissions;
            this.primaryCommand = command.contains(" ") ? command.substring(1, command.indexOf(' ')) : command.substring(1);
        }
    }
}