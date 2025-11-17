package dev.ajaretro.foliaCore.commands;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Team;
import dev.ajaretro.foliaCore.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TeamCommand implements CommandExecutor {

    private final FoliaCore plugin;
    private final TeamManager tm;
    private static final Pattern TEAM_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    public TeamCommand(FoliaCore plugin) {
        this.plugin = plugin;
        this.tm = plugin.getTeamManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessenger().sendError(sender, "This command can only be run by a player.");
            return true;
        }

        if (args.length == 0) {
            handleHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "disband":
                handleDisband(player);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            case "decline":
                handleDecline(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "info":
            case "who":
                handleInfo(player, args);
                break;
            default:
                handleHelp(player);
                break;
        }

        return true;
    }

    private void handleHelp(Player player) {
        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "--- FoliaCore Team Help ---");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team create <name>" + ChatColor.WHITE + " - Create a new team.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team invite <player>" + ChatColor.WHITE + " - Invite a player to your team.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team accept [name]" + ChatColor.WHITE + " - Accept a team invite.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team decline" + ChatColor.WHITE + " - Decline a team invite.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team kick <player>" + ChatColor.WHITE + " - Kick a player from your team.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team leave" + ChatColor.WHITE + " - Leave your current team.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team disband" + ChatColor.WHITE + " - Disband your team.");
        plugin.getMessenger().sendMessage(player, ChatColor.GOLD + "/team info [name]" + ChatColor.WHITE + " - Get info on a team.");
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("foliacore.team.create")) {
            plugin.getMessenger().sendError(player, "You do not have permission to create a team.");
            return;
        }

        if (tm.getTeam(player.getUniqueId()) != null) {
            plugin.getMessenger().sendError(player, "You are already in a team. You must leave it first.");
            return;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /team create <name>");
            return;
        }

        String teamName = args[1];
        if (!TEAM_NAME_PATTERN.matcher(teamName).matches()) {
            plugin.getMessenger().sendError(player, "Team name must be 3-16 characters and only contain letters, numbers, and underscores.");
            return;
        }

        if (tm.teamExists(teamName)) {
            plugin.getMessenger().sendError(player, "A team with that name already exists.");
            return;
        }

        tm.createTeam(teamName, player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have created the team: " + ChatColor.GOLD + teamName);
    }

    private void handleDisband(Player player) {
        if (!player.hasPermission("foliacore.team.disband")) {
            plugin.getMessenger().sendError(player, "You do not have permission to disband a team.");
            return;
        }

        Team team = tm.getTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessenger().sendError(player, "You are not in a team.");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You are not the owner of this team.");
            return;
        }

        String teamName = team.getName();
        notifyTeam(team, player.getUniqueId(), ChatColor.RED + "Your team has been disbanded by " + player.getName() + ".");
        tm.disbandTeam(team);
        plugin.getMessenger().sendSuccess(player, "You have disbanded your team: " + ChatColor.GOLD + teamName);
    }

    private void handleInvite(Player player, String[] args) {
        if (!player.hasPermission("foliacore.team.invite")) {
            plugin.getMessenger().sendError(player, "You do not have permission to invite players.");
            return;
        }

        Team team = tm.getTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessenger().sendError(player, "You are not in a team.");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Only the team owner can invite players.");
            return;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /team invite <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            plugin.getMessenger().sendError(player, "Player not found or is not online.");
            return;
        }

        if (tm.getTeam(target.getUniqueId()) != null) {
            plugin.getMessenger().sendError(player, "That player is already in a team.");
            return;
        }

        tm.addInvite(target.getUniqueId(), team);
        plugin.getMessenger().sendSuccess(player, "Invite sent to " + ChatColor.GOLD + target.getName());

        target.getScheduler().run(plugin, (task) -> {
            plugin.getMessenger().sendMessage(target, ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has invited you to join their team: " + ChatColor.GOLD + team.getName());
            plugin.getMessenger().sendMessage(target, "Type " + ChatColor.GREEN + "/team accept " + team.getName() + ChatColor.WHITE + " to join. This invite will expire in 60 seconds.");
        }, null);
    }

    private void handleAccept(Player player, String[] args) {
        if (!player.hasPermission("foliacore.team.accept")) {
            plugin.getMessenger().sendError(player, "You do not have permission to accept invites.");
            return;
        }

        TeamManager.TeamInvite invite = tm.getInvite(player.getUniqueId());
        if (invite == null) {
            plugin.getMessenger().sendError(player, "You do not have a pending team invite.");
            return;
        }

        if (args.length > 1 && !invite.team().getName().equalsIgnoreCase(args[1])) {
            plugin.getMessenger().sendError(player, "That is not the correct team name. Your invite is for: " + ChatColor.GOLD + invite.team().getName());
            return;
        }

        if (tm.getTeam(player.getUniqueId()) != null) {
            plugin.getMessenger().sendError(player, "You are already in a team. You must leave it first.");
            return;
        }

        tm.removeInvite(player.getUniqueId());
        tm.joinTeam(invite.team(), player.getUniqueId());

        plugin.getMessenger().sendSuccess(player, "You have joined the team: " + ChatColor.GOLD + invite.team().getName());
        notifyTeam(invite.team(), player.getUniqueId(), ChatColor.GREEN + player.getName() + " has joined the team!");
    }

    private void handleDecline(Player player) {
        TeamManager.TeamInvite invite = tm.getInvite(player.getUniqueId());
        if (invite == null) {
            plugin.getMessenger().sendError(player, "You do not have a pending team invite.");
            return;
        }

        tm.removeInvite(player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have declined the invite from " + ChatColor.GOLD + invite.team().getName());

        Player owner = Bukkit.getPlayer(invite.team().getOwner());
        if (owner != null && owner.isOnline()) {
            owner.getScheduler().run(plugin, (task) -> {
                plugin.getMessenger().sendError(owner, player.getName() + " has declined your team invite.");
            }, null);
        }
    }

    private void handleLeave(Player player) {
        if (!player.hasPermission("foliacore.team.leave")) {
            plugin.getMessenger().sendError(player, "You do not have permission to leave a team.");
            return;
        }

        Team team = tm.getTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessenger().sendError(player, "You are not in a team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You are the owner. You must disband the team or transfer ownership.");
            return;
        }

        tm.leaveTeam(team, player.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have left the team: " + ChatColor.GOLD + team.getName());
        notifyTeam(team, player.getUniqueId(), ChatColor.RED + player.getName() + " has left the team.");
    }

    private void handleKick(Player player, String[] args) {
        if (!player.hasPermission("foliacore.team.kick")) {
            plugin.getMessenger().sendError(player, "You do not have permission to kick players.");
            return;
        }

        Team team = tm.getTeam(player.getUniqueId());
        if (team == null) {
            plugin.getMessenger().sendError(player, "You are not in a team.");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "Only the team owner can kick players.");
            return;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendError(player, "Usage: /team kick <player>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            plugin.getMessenger().sendError(player, "Player not found.");
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            plugin.getMessenger().sendError(player, "You cannot kick yourself.");
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            plugin.getMessenger().sendError(player, "That player is not in your team.");
            return;
        }

        tm.leaveTeam(team, target.getUniqueId());
        plugin.getMessenger().sendSuccess(player, "You have kicked " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + " from the team.");
        notifyTeam(team, player.getUniqueId(), ChatColor.RED + target.getName() + " has been kicked from the team.");

        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            onlineTarget.getScheduler().run(plugin, (task) -> {
                plugin.getMessenger().sendError(onlineTarget, "You have been kicked from the team by " + player.getName());
            }, null);
        }
    }

    private void handleInfo(Player player, String[] args) {
        Team team;
        if (args.length < 2) {
            team = tm.getTeam(player.getUniqueId());
            if (team == null) {
                plugin.getMessenger().sendError(player, "You are not in a team. Usage: /team info <name>");
                return;
            }
        } else {
            team = tm.getTeam(args[1]);
            if (team == null) {
                plugin.getMessenger().sendError(player, "No team found with that name.");
                return;
            }
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());
        String ownerName = owner.getName() != null ? owner.getName() : "Unknown";

        String membersList = team.getMembers().stream()
                .map(uuid -> {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    return p.isOnline() ? ChatColor.GREEN + p.getName() : ChatColor.GRAY + p.getName();
                })
                .collect(Collectors.joining(ChatColor.WHITE + ", "));

        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "--- Team Info: " + ChatColor.GOLD + team.getName() + " ---");
        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "Owner: " + ChatColor.RED + ownerName);
        plugin.getMessenger().sendMessage(player, ChatColor.YELLOW + "Members (" + team.getSize() + "): " + membersList);
    }

    private void notifyTeam(Team team, UUID playerToSkip, String message) {
        for (UUID memberUUID : team.getMembers()) {
            if (memberUUID.equals(playerToSkip)) {
                continue;
            }
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.getScheduler().run(plugin, (task) -> {
                    plugin.getMessenger().sendMessage(member, message);
                }, null);
            }
        }
    }
}