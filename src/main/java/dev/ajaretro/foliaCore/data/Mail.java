/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

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