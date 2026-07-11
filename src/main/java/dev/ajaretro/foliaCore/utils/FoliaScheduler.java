/*
 * Copyright (c) 2026 AJA_RETRO (https://ajaretro.dev). All Rights Reserved.
 * 
 * This source code and compiled binaries are the intellectual property of the author.
 * Redistribution, modification, or derivative works are strictly prohibited under the
 * terms of the Source-Available License.
 */

package dev.ajaretro.foliaCore.utils;

import dev.ajaretro.foliaCore.FoliaCore;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Region-safe Folia scheduler utility wrapper.
 */
public class FoliaScheduler {

    /**
     * Executes a task asynchronously on the async scheduler.
     * Use for database calls, file reading/writing, and non-tick calculations.
     */
    public static ScheduledTask runAsync(FoliaCore plugin, Runnable task) {
        return Bukkit.getAsyncScheduler().runNow(plugin, t -> task.run());
    }

    /**
     * Executes a task on the global region scheduler.
     * Use for tasks not tied to any specific region (e.g. broadcasting, server-wide tallies).
     */
    public static ScheduledTask runGlobal(FoliaCore plugin, Runnable task) {
        return Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
    }

    /**
     * Executes a task on the region scheduler owning the given location.
     */
    public static ScheduledTask runAtLocation(FoliaCore plugin, Location loc, Runnable task) {
        return Bukkit.getRegionScheduler().run(plugin, loc, t -> task.run());
    }

    /**
     * Executes a task on the region scheduler owning the location of the given entity.
     */
    public static ScheduledTask runAtEntity(FoliaCore plugin, Entity entity, Runnable task) {
        return entity.getScheduler().run(plugin, t -> task.run(), null);
    }

    /**
     * Executes a task on the region scheduler owning the chunk at the given chunk coordinates.
     */
    public static ScheduledTask runAtChunk(FoliaCore plugin, World world, int cx, int cz, Runnable task) {
        return Bukkit.getRegionScheduler().run(plugin, world, cx, cz, t -> task.run());
    }
}
