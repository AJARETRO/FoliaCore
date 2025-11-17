package dev.ajaretro.foliaCore.managers;

import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.Team;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TeamManager {

    private static TeamManager instance;
    private final FoliaCore plugin;

    private final ConcurrentHashMap<String, Team> teamsByName;
    private final ConcurrentHashMap<UUID, Team> teamByPlayer;
    private final ConcurrentHashMap<UUID, TeamInvite> pendingInvites;

    private File dataFile;
    private FileConfiguration dataConfig;

    private static final long INVITE_TIMEOUT_MS = 60 * 1000;

    public record TeamInvite(Team team, long timestamp) {}

    public TeamManager(FoliaCore plugin) {
        this.plugin = plugin;
        this.teamsByName = new ConcurrentHashMap<>();
        this.teamByPlayer = new ConcurrentHashMap<>();
        this.pendingInvites = new ConcurrentHashMap<>();
    }

    public static TeamManager getInstance() {
        return instance;
    }

    public void load() {
        instance = this;

        dataFile = new File(plugin.getDataFolder(), "team_data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("team_data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadTeams();
    }

    private void loadTeams() {
        ConfigurationSection teamsSection = dataConfig.getConfigurationSection("teams");
        if (teamsSection == null) return;

        for (String teamName : teamsSection.getKeys(false)) {
            try {
                // --- THIS IS THE FIX ---
                ConfigurationSection teamSection = teamsSection.getConfigurationSection(teamName);
                if (teamSection == null) continue;
                Map<String, Object> teamData = teamSection.getValues(false);
                // --- END FIX ---

                Team team = Team.deserialize(teamName, teamData);
                teamsByName.put(teamName.toLowerCase(), team);
                for (UUID member : team.getMembers()) {
                    teamByPlayer.put(member, team);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not load team: " + teamName);
            }
        }
    }

    public void saveData() {
        try {
            dataConfig.set("teams", null);
            for (Map.Entry<String, Team> entry : teamsByName.entrySet()) {
                dataConfig.set("teams." + entry.getKey(), entry.getValue().serialize());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save team data to file!");
            e.printStackTrace();
        }
    }

    private void saveDataAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            saveData();
        });
    }

    public Team getTeam(String name) {
        return teamsByName.get(name.toLowerCase());
    }

    public Team getTeam(UUID playerUUID) {
        return teamByPlayer.get(playerUUID);
    }

    public boolean teamExists(String name) {
        return teamsByName.containsKey(name.toLowerCase());
    }

    public void createTeam(String name, UUID owner) {
        Team team = new Team(name, owner);
        teamsByName.put(name.toLowerCase(), team);
        teamByPlayer.put(owner, team);
        saveDataAsync();
    }

    public void disbandTeam(Team team) {
        teamsByName.remove(team.getName().toLowerCase());
        for (UUID member : team.getMembers()) {
            teamByPlayer.remove(member);
        }
        saveDataAsync();
    }

    public void addInvite(UUID target, Team team) {
        pendingInvites.put(target, new TeamInvite(team, System.currentTimeMillis()));
    }

    public TeamInvite getInvite(UUID target) {
        TeamInvite invite = pendingInvites.get(target);
        if (invite == null) return null;

        if (System.currentTimeMillis() - invite.timestamp() > INVITE_TIMEOUT_MS) {
            pendingInvites.remove(target);
            return null;
        }
        return invite;
    }

    public void removeInvite(UUID target) {
        pendingInvites.remove(target);
    }

    public void joinTeam(Team team, UUID player) {
        team.addMember(player);
        teamByPlayer.put(player, team);
        saveDataAsync();
    }

    public void leaveTeam(Team team, UUID player) {
        team.removeMember(player);
        teamByPlayer.remove(player);
        saveDataAsync();
    }
}