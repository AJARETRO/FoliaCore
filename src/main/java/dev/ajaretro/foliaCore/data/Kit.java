package dev.ajaretro.foliaCore.data;

import dev.ajaretro.foliaCore.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public record Kit(
        String name,
        long cooldown,
        String permission,
        Material displayMaterial,
        String itemsBase64
) {

    public ItemStack[] getItems() {
        return ItemUtil.deserializeItems(itemsBase64);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cooldown-seconds", cooldown);
        map.put("permission", permission);
        map.put("display-material", displayMaterial.name());
        map.put("items-base64", itemsBase64);
        return map;
    }

    public static Kit deserialize(String name, Map<String, Object> map) {
        return new Kit(
                name,
                ((Number) map.get("cooldown-seconds")).longValue(),
                (String) map.get("permission"),
                Material.valueOf((String) map.get("display-material")),
                (String) map.get("items-base64")
        );
    }
}