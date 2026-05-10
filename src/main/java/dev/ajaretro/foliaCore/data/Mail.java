package dev.ajaretro.foliaCore.data;

import java.util.UUID;

public class Mail {
    private final UUID sender;
    private final long timestamp;
    private final String message;

    public Mail(UUID sender, long timestamp, String message) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Mail(UUID sender, String message) {
        this(sender, System.currentTimeMillis(), message);
    }

    public UUID getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}