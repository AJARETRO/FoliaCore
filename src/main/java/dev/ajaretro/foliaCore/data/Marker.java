package dev.ajaretro.foliaCore.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;

public record Marker(
        String name,
        String worldName,
        double x,
        double y,
        double z
) {
    public Marker(String name, Location location) {
        this(
                name,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", worldName);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }

    public static Marker deserialize(String name, Map<String, Object> map) {
        return new Marker(
                name,
                (String) map.get("world"),
                (Double) map.get("x"),
                (Double) map.get("y"),
                (Double) map.get("z")
        );
    }
}