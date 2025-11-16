package dev.ajaretro.foliaCore.data;

import java.util.UUID;

public record Mail(
        UUID sender,
        long timestamp,
        String message
) {
    public Mail(UUID sender, String message) {
        this(sender, System.currentTimeMillis(), message);
    }
}