package dev.ajaretro.foliaCore.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a jailed player's status and records.
 */
public class JailedPlayer {
    private final UUID uuid;
    private final String jailName;
    private final long expiryTime;
    private final String reason;
    private final long jailTime;
    private final Location returnLocation;

    public JailedPlayer(UUID uuid, String jailName, long expiryTime, String reason, long jailTime, Location returnLocation) {
        this.uuid = uuid;
        this.jailName = jailName;
        this.expiryTime = expiryTime;
        this.reason = reason == null || reason.isEmpty() ? "No reason provided" : reason;
        this.jailTime = jailTime;
        this.returnLocation = returnLocation;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getJailName() {
        return jailName;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getReason() {
        return reason;
    }

    public long getJailTime() {
        return jailTime;
    }

    public Location getReturnLocation() {
        return returnLocation;
    }

    public boolean isExpired() {
        if (expiryTime == -1) return false;
        return System.currentTimeMillis() > expiryTime;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("jail", jailName);
        map.put("expiry_time", expiryTime);
        map.put("reason", reason);
        map.put("jail_time", jailTime);
        if (returnLocation != null) {
            map.put("return_world", returnLocation.getWorld().getName());
            map.put("return_x", returnLocation.getX());
            map.put("return_y", returnLocation.getY());
            map.put("return_z", returnLocation.getZ());
            map.put("return_yaw", returnLocation.getYaw());
            map.put("return_pitch", returnLocation.getPitch());
        }
        return map;
    }

    public static JailedPlayer deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString((String) map.get("uuid"));
        String jailName = (String) map.get("jail");
        long expiryTime = ((Number) map.get("expiry_time")).longValue();
        String reason = (String) map.get("reason");
        long jailTime = ((Number) map.get("jail_time")).longValue();

        Location returnLocation = null;
        if (map.containsKey("return_world")) {
            String worldName = (String) map.get("return_world");
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                double x = ((Number) map.get("return_x")).doubleValue();
                double y = ((Number) map.get("return_y")).doubleValue();
                double z = ((Number) map.get("return_z")).doubleValue();
                float yaw = ((Number) map.get("return_yaw")).floatValue();
                float pitch = ((Number) map.get("return_pitch")).floatValue();
                returnLocation = new Location(world, x, y, z, yaw, pitch);
            }
        }

        return new JailedPlayer(uuid, jailName, expiryTime, reason, jailTime, returnLocation);
    }
}
