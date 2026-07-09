package dev.ajaretro.foliaCore.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.data.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MySqlStorageProvider implements StorageProvider {
    private final FoliaCore plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean ssl;

    private HikariDataSource dataSource;

    public MySqlStorageProvider(FoliaCore plugin, String host, int port, String database, String username, String password, boolean ssl) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    @Override
    public void init() throws Exception {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl + "&allowPublicKeyRetrieval=true");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Performance tuning
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        
        hikariConfig.setConnectionTimeout(5000);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(hikariConfig);

        // Create tables (Existing + New)
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            // Warps
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_warps (" +
                    "name VARCHAR(64) PRIMARY KEY, " +
                    "world VARCHAR(64) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "yaw FLOAT NOT NULL, " +
                    "pitch FLOAT NOT NULL" +
                    ")");

            // Homes
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_homes (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "name VARCHAR(64) NOT NULL, " +
                    "world VARCHAR(64) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "yaw FLOAT NOT NULL, " +
                    "pitch FLOAT NOT NULL, " +
                    "PRIMARY KEY (uuid, name)" +
                    ")");

            // Spawns
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_spawns (" +
                    "type VARCHAR(32) PRIMARY KEY, " +
                    "world VARCHAR(64) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "yaw FLOAT NOT NULL, " +
                    "pitch FLOAT NOT NULL" +
                    ")");

            // Kits
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_kits (" +
                    "name VARCHAR(64) PRIMARY KEY, " +
                    "cooldown BIGINT NOT NULL, " +
                    "permission VARCHAR(128) NOT NULL, " +
                    "display_material VARCHAR(64) NOT NULL, " +
                    "items_base64 TEXT NOT NULL" +
                    ")");

            // Bans
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_bans (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(64) NOT NULL, " +
                    "reason TEXT NOT NULL, " +
                    "ban_time BIGINT NOT NULL, " +
                    "expiry_time BIGINT NOT NULL, " +
                    "permanent BOOLEAN NOT NULL" +
                    ")");

            // Mail
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_mail (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "recipient VARCHAR(36) NOT NULL, " +
                    "sender VARCHAR(36) NOT NULL, " +
                    "timestamp BIGINT NOT NULL, " +
                    "message TEXT NOT NULL" +
                    ")");

            // Markers
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_markers (" +
                    "name VARCHAR(64) PRIMARY KEY, " +
                    "world VARCHAR(64) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL" +
                    ")");

            // Teams
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_teams (" +
                    "name VARCHAR(64) PRIMARY KEY, " +
                    "owner VARCHAR(36) NOT NULL" +
                    ")");

            // Team Members
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_team_members (" +
                    "team_name VARCHAR(64) NOT NULL, " +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "PRIMARY KEY (team_name, uuid)" +
                    ")");

            // --- New Systems Tables ---
            // Economy Balances
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_balances (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "balance DOUBLE NOT NULL" +
                    ")");

            // Jails
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_jails (" +
                    "name VARCHAR(64) PRIMARY KEY, " +
                    "world VARCHAR(64) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "yaw FLOAT NOT NULL, " +
                    "pitch FLOAT NOT NULL" +
                    ")");

            // Jailed Players
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_jailed_players (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "jail VARCHAR(64) NOT NULL, " +
                    "expiry_time BIGINT NOT NULL, " +
                    "reason TEXT NOT NULL, " +
                    "jail_time BIGINT NOT NULL, " +
                    "return_world VARCHAR(64), " +
                    "return_x DOUBLE, " +
                    "return_y DOUBLE, " +
                    "return_z DOUBLE, " +
                    "return_yaw FLOAT, " +
                    "return_pitch FLOAT" +
                    ")");

            // Ignores
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_ignores (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "ignored_uuid VARCHAR(36) NOT NULL, " +
                    "PRIMARY KEY (uuid, ignored_uuid)" +
                    ")");

            // Powertools
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foliacore_powertools (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "material VARCHAR(64) NOT NULL, " +
                    "command TEXT NOT NULL, " +
                    "PRIMARY KEY (uuid, material)" +
                    ")");
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // --- Warps ---
    @Override
    public void saveWarps(Collection<Warp> warps) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_warps");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_warps (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Warp warp : warps) {
                    insertStmt.setString(1, warp.getName());
                    insertStmt.setString(2, warp.getWorldName());
                    insertStmt.setDouble(3, warp.getX());
                    insertStmt.setDouble(4, warp.getY());
                    insertStmt.setDouble(5, warp.getZ());
                    insertStmt.setFloat(6, warp.getYaw());
                    insertStmt.setFloat(7, warp.getPitch());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving warps: " + e.getMessage());
        }
    }

    @Override
    public Collection<Warp> loadWarps() {
        List<Warp> warps = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_warps");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                warps.add(new Warp(
                        rs.getString("name"),
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading warps: " + e.getMessage());
        }
        return warps;
    }

    // --- Homes ---
    @Override
    public void saveHomes(Map<UUID, Map<String, Home>> homes) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_homes");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_homes (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
                    String uuidStr = entry.getKey().toString();
                    for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                        Home home = homeEntry.getValue();
                        insertStmt.setString(1, uuidStr);
                        insertStmt.setString(2, homeEntry.getKey());
                        insertStmt.setString(3, home.getWorldName());
                        insertStmt.setDouble(4, home.getX());
                        insertStmt.setDouble(5, home.getY());
                        insertStmt.setDouble(6, home.getZ());
                        insertStmt.setFloat(7, home.getYaw());
                        insertStmt.setFloat(8, home.getPitch());
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving homes: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Map<String, Home>> loadHomes() {
        Map<UUID, Map<String, Home>> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_homes");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String name = rs.getString("name");
                    Home home = new Home(
                            rs.getString("world"),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    );
                    loaded.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(name.toLowerCase(), home);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping invalid UUID in SQL homes load.");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading homes: " + e.getMessage());
        }
        return loaded;
    }

    // --- Spawns ---
    @Override
    public void saveSpawn(Location spawnLocation) {
        saveSpawnInternal("spawn", spawnLocation);
    }

    @Override
    public Location loadSpawn() {
        return loadSpawnInternal("spawn");
    }

    @Override
    public void saveFirstSpawn(Location firstSpawnLocation) {
        saveSpawnInternal("first_spawn", firstSpawnLocation);
    }

    @Override
    public Location loadFirstSpawn() {
        return loadSpawnInternal("first_spawn");
    }

    private void saveSpawnInternal(String type, Location loc) {
        if (loc == null) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM foliacore_spawns WHERE type = ?")) {
                stmt.setString(1, type);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("SQL error while deleting spawn: " + e.getMessage());
            }
            return;
        }

        String sql = "INSERT INTO foliacore_spawns (type, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            stmt.setString(2, loc.getWorld().getName());
            stmt.setDouble(3, loc.getX());
            stmt.setDouble(4, loc.getY());
            stmt.setDouble(5, loc.getZ());
            stmt.setFloat(6, loc.getYaw());
            stmt.setFloat(7, loc.getPitch());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving spawn (" + type + "): " + e.getMessage());
        }
    }

    private Location loadSpawnInternal(String type) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_spawns WHERE type = ?")) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String worldName = rs.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) return null;
                    return new Location(
                            world,
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    );
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading spawn (" + type + "): " + e.getMessage());
        }
        return null;
    }

    // --- Kits ---
    @Override
    public void saveKits(Collection<Kit> kits) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_kits");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_kits (name, cooldown, permission, display_material, items_base64) VALUES (?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Kit kit : kits) {
                    insertStmt.setString(1, kit.getName());
                    insertStmt.setLong(2, kit.getCooldown());
                    insertStmt.setString(3, kit.getPermission());
                    insertStmt.setString(4, kit.getDisplayMaterial().name());
                    insertStmt.setString(5, kit.getItemsBase64());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving kits: " + e.getMessage());
        }
    }

    @Override
    public Collection<Kit> loadKits() {
        List<Kit> kits = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_kits");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                kits.add(new Kit(
                        rs.getString("name"),
                        rs.getLong("cooldown"),
                        rs.getString("permission"),
                        Material.valueOf(rs.getString("display_material")),
                        rs.getString("items_base64")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading kits: " + e.getMessage());
        }
        return kits;
    }

    // --- Bans ---
    @Override
    public void saveBans(Collection<Ban> bans) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_bans");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_bans (uuid, name, reason, ban_time, expiry_time, permanent) VALUES (?, ?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Ban ban : bans) {
                    insertStmt.setString(1, ban.getPlayerUUID().toString());
                    insertStmt.setString(2, ban.getPlayerName());
                    insertStmt.setString(3, ban.getReason());
                    insertStmt.setLong(4, ban.getBanTime());
                    insertStmt.setLong(5, ban.getExpiryTime());
                    insertStmt.setBoolean(6, ban.isPermanent());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving bans: " + e.getMessage());
        }
    }

    @Override
    public Collection<Ban> loadBans() {
        List<Ban> bans = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_bans");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bans.add(new Ban(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getString("reason"),
                        rs.getLong("ban_time"),
                        rs.getLong("expiry_time"),
                        rs.getBoolean("permanent")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading bans: " + e.getMessage());
        }
        return bans;
    }

    // --- Mail ---
    @Override
    public void saveMail(Map<UUID, List<Mail>> mailboxes) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_mail");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_mail (recipient, sender, timestamp, message) VALUES (?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, List<Mail>> entry : mailboxes.entrySet()) {
                    String recipientStr = entry.getKey().toString();
                    synchronized (entry.getValue()) {
                        for (Mail mail : entry.getValue()) {
                            insertStmt.setString(1, recipientStr);
                            insertStmt.setString(2, mail.getSender().toString());
                            insertStmt.setLong(3, mail.getTimestamp());
                            insertStmt.setString(4, mail.getMessage());
                            insertStmt.addBatch();
                        }
                    }
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving mail: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, List<Mail>> loadMail() {
        Map<UUID, List<Mail>> mailboxes = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_mail ORDER BY timestamp ASC");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID recipient = UUID.fromString(rs.getString("recipient"));
                    UUID sender = UUID.fromString(rs.getString("sender"));
                    Mail mail = new Mail(sender, rs.getLong("timestamp"), rs.getString("message"));
                    mailboxes.computeIfAbsent(recipient, k -> Collections.synchronizedList(new ArrayList<>())).add(mail);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping invalid UUID in SQL mail load.");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading mail: " + e.getMessage());
        }
        return mailboxes;
    }

    // --- Markers ---
    @Override
    public void saveMarkers(Collection<Marker> markers) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_markers");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_markers (name, world, x, y, z) VALUES (?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Marker marker : markers) {
                    insertStmt.setString(1, marker.getName());
                    insertStmt.setString(2, marker.getWorldName());
                    insertStmt.setDouble(3, marker.getX());
                    insertStmt.setDouble(4, marker.getY());
                    insertStmt.setDouble(5, marker.getZ());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving markers: " + e.getMessage());
        }
    }

    @Override
    public Collection<Marker> loadMarkers() {
        List<Marker> markers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_markers");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                markers.add(new Marker(
                        rs.getString("name"),
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading markers: " + e.getMessage());
        }
        return markers;
    }

    // --- Teams ---
    @Override
    public void saveTeams(Collection<Team> teams) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteMembersStmt = conn.prepareStatement("DELETE FROM foliacore_team_members");
                 PreparedStatement deleteTeamsStmt = conn.prepareStatement("DELETE FROM foliacore_teams");
                 PreparedStatement insertTeamStmt = conn.prepareStatement("INSERT INTO foliacore_teams (name, owner) VALUES (?, ?)");
                 PreparedStatement insertMemberStmt = conn.prepareStatement("INSERT INTO foliacore_team_members (team_name, uuid) VALUES (?, ?)")) {
                
                deleteMembersStmt.executeUpdate();
                deleteTeamsStmt.executeUpdate();

                for (Team team : teams) {
                    insertTeamStmt.setString(1, team.getName());
                    insertTeamStmt.setString(2, team.getOwner().toString());
                    insertTeamStmt.addBatch();

                    for (UUID uuid : team.getMembers()) {
                        insertMemberStmt.setString(1, team.getName());
                        insertMemberStmt.setString(2, uuid.toString());
                        insertMemberStmt.addBatch();
                    }
                }
                insertTeamStmt.executeBatch();
                insertMemberStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving teams: " + e.getMessage());
        }
    }

    @Override
    public Collection<Team> loadTeams() {
        Map<String, Team> teamsMap = new ConcurrentHashMap<>();
        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_teams");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    UUID owner = UUID.fromString(rs.getString("owner"));
                    teamsMap.put(name.toLowerCase(), new Team(name, owner));
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_team_members");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String teamName = rs.getString("team_name");
                    UUID memberUUID = UUID.fromString(rs.getString("uuid"));
                    Team team = teamsMap.get(teamName.toLowerCase());
                    if (team != null) {
                        team.addMember(memberUUID);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading teams: " + e.getMessage());
        }
        return teamsMap.values();
    }

    // --- New Systems Methods ---

    // Economy
    @Override
    public void saveBalances(Map<UUID, Double> balances) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_balances");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_balances (uuid, balance) VALUES (?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                    insertStmt.setString(1, entry.getKey().toString());
                    insertStmt.setDouble(2, entry.getValue());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving balances: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Double> loadBalances() {
        Map<UUID, Double> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_balances");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("balance");
                    loaded.put(uuid, balance);
                } catch (Exception ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading balances: " + e.getMessage());
        }
        return loaded;
    }

    // Jails
    @Override
    public void saveJails(Map<String, Location> jails) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_jails");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_jails (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<String, Location> entry : jails.entrySet()) {
                    Location loc = entry.getValue();
                    insertStmt.setString(1, entry.getKey().toLowerCase());
                    insertStmt.setString(2, loc.getWorld().getName());
                    insertStmt.setDouble(3, loc.getX());
                    insertStmt.setDouble(4, loc.getY());
                    insertStmt.setDouble(5, loc.getZ());
                    insertStmt.setFloat(6, loc.getYaw());
                    insertStmt.setFloat(7, loc.getPitch());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving jails: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Location> loadJails() {
        Map<String, Location> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_jails");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    loaded.put(name.toLowerCase(), new Location(
                            world,
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z"),
                            rs.getFloat("yaw"),
                            rs.getFloat("pitch")
                    ));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading jails: " + e.getMessage());
        }
        return loaded;
    }

    @Override
    public void saveJailedPlayers(Map<UUID, JailedPlayer> jailedPlayers) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_jailed_players");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_jailed_players (uuid, jail, expiry_time, reason, jail_time, return_world, return_x, return_y, return_z, return_yaw, return_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, JailedPlayer> entry : jailedPlayers.entrySet()) {
                    JailedPlayer jp = entry.getValue();
                    insertStmt.setString(1, jp.getUuid().toString());
                    insertStmt.setString(2, jp.getJailName());
                    insertStmt.setLong(3, jp.getExpiryTime());
                    insertStmt.setString(4, jp.getReason());
                    insertStmt.setLong(5, jp.getJailTime());
                    Location ret = jp.getReturnLocation();
                    if (ret != null) {
                        insertStmt.setString(6, ret.getWorld().getName());
                        insertStmt.setDouble(7, ret.getX());
                        insertStmt.setDouble(8, ret.getY());
                        insertStmt.setDouble(9, ret.getZ());
                        insertStmt.setFloat(10, ret.getYaw());
                        insertStmt.setFloat(11, ret.getPitch());
                    } else {
                        insertStmt.setNull(6, Types.VARCHAR);
                        insertStmt.setNull(7, Types.DOUBLE);
                        insertStmt.setNull(8, Types.DOUBLE);
                        insertStmt.setNull(9, Types.DOUBLE);
                        insertStmt.setNull(10, Types.FLOAT);
                        insertStmt.setNull(11, Types.FLOAT);
                    }
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving jailed players: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, JailedPlayer> loadJailedPlayers() {
        Map<UUID, JailedPlayer> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_jailed_players");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String jail = rs.getString("jail");
                    long expiry = rs.getLong("expiry_time");
                    String reason = rs.getString("reason");
                    long jailTime = rs.getLong("jail_time");
                    
                    Location returnLoc = null;
                    String retWorldName = rs.getString("return_world");
                    if (retWorldName != null) {
                        World world = Bukkit.getWorld(retWorldName);
                        if (world != null) {
                            returnLoc = new Location(
                                    world,
                                    rs.getDouble("return_x"),
                                    rs.getDouble("return_y"),
                                    rs.getDouble("return_z"),
                                    rs.getFloat("return_yaw"),
                                    rs.getFloat("return_pitch")
                            );
                        }
                    }
                    loaded.put(uuid, new JailedPlayer(uuid, jail, expiry, reason, jailTime, returnLoc));
                } catch (Exception ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading jailed players: " + e.getMessage());
        }
        return loaded;
    }

    // Ignores
    @Override
    public void saveIgnores(Map<UUID, Set<UUID>> ignores) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_ignores");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_ignores (uuid, ignored_uuid) VALUES (?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, Set<UUID>> entry : ignores.entrySet()) {
                    String uuidStr = entry.getKey().toString();
                    for (UUID ignored : entry.getValue()) {
                        insertStmt.setString(1, uuidStr);
                        insertStmt.setString(2, ignored.toString());
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving ignores: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Set<UUID>> loadIgnores() {
        Map<UUID, Set<UUID>> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_ignores");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    UUID ignored = UUID.fromString(rs.getString("ignored_uuid"));
                    loaded.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(ignored);
                } catch (Exception ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading ignores: " + e.getMessage());
        }
        return loaded;
    }

    // Powertools
    @Override
    public void savePowertools(Map<UUID, Map<Material, String>> powertools) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM foliacore_powertools");
                 PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO foliacore_powertools (uuid, material, command) VALUES (?, ?, ?)")) {
                
                deleteStmt.executeUpdate();

                for (Map.Entry<UUID, Map<Material, String>> entry : powertools.entrySet()) {
                    String uuidStr = entry.getKey().toString();
                    for (Map.Entry<Material, String> ptEntry : entry.getValue().entrySet()) {
                        insertStmt.setString(1, uuidStr);
                        insertStmt.setString(2, ptEntry.getKey().name());
                        insertStmt.setString(3, ptEntry.getValue());
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while saving powertools: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, Map<Material, String>> loadPowertools() {
        Map<UUID, Map<Material, String>> loaded = new ConcurrentHashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM foliacore_powertools");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    Material mat = Material.valueOf(rs.getString("material").toUpperCase());
                    String cmd = rs.getString("command");
                    loaded.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(mat, cmd);
                } catch (Exception ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL error while loading powertools: " + e.getMessage());
        }
        return loaded;
    }
}
