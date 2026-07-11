/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player ban record.
 */
public class Ban {
    private final UUID playerUUID;
    private final String playerName;
    private final String reason;
    private final long banTime;
    private final long expiryTime;
    private final boolean permanent;

    public Ban(UUID playerUUID, String playerName, String reason, long banTime, long expiryTime, boolean permanent) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.reason = reason == null || reason.isEmpty() ? "No reason provided" : reason;
        this.banTime = banTime;
        this.expiryTime = expiryTime;
        this.permanent = permanent;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getReason() {
        return reason;
    }

    public long getBanTime() {
        return banTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean isExpired() {
        if (permanent) return false;
        return System.currentTimeMillis() > expiryTime;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", playerUUID.toString());
        map.put("name", playerName);
        map.put("reason", reason);
        map.put("ban_time", banTime);
        map.put("expiry_time", expiryTime);
        map.put("permanent", permanent);
        return map;
    }

    public static Ban deserialize(Map<String, Object> map) {
        return new Ban(
                UUID.fromString((String) map.get("uuid")),
                (String) map.get("name"),
                (String) map.get("reason"),
                ((Number) map.get("ban_time")).longValue(),
                ((Number) map.get("expiry_time")).longValue(),
                (boolean) map.get("permanent")
        );
    }
}

