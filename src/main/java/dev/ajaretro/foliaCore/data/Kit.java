/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.data;

import dev.ajaretro.foliaCore.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class Kit {
    private final String name;
    private final long cooldown;
    private final String permission;
    private final Material displayMaterial;
    private final String itemsBase64;

    public Kit(String name, long cooldown, String permission, Material displayMaterial, String itemsBase64) {
        this.name = name;
        this.cooldown = cooldown;
        this.permission = permission;
        this.displayMaterial = displayMaterial;
        this.itemsBase64 = itemsBase64;
    }

    public String getName() {
        return name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public String getPermission() {
        return permission;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public String getItemsBase64() {
        return itemsBase64;
    }

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