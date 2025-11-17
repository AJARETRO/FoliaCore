package dev.ajaretro.foliaCore.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;

public record Warp(
        String name,
        String worldName,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {

    public Warp(String name, Location location) {
        this(
                name,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", worldName);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        return map;
    }

    public static Warp deserialize(String name, Map<String, Object> map) {
        return new Warp(
                name,
                (String) map.get("world"),
                (Double) map.get("x"),
                (Double) map.get("y"),
                (Double) map.get("z"),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue()
        );
    }
}