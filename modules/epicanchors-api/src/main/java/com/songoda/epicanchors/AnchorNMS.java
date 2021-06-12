package com.songoda.epicanchors;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Provides abstraction to be used in maven modules with the specified spigot version.
 */
public abstract class AnchorNMS {
    protected final JavaPlugin plugin;

    protected AnchorNMS(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Tries to load a given chunk<br>
     * This method <em>might</em> introduce logic to keep chunks forcefully loaded.
     * <p>
     * More information: <a href="https://minecraft.fandom.com/wiki/Tick#Chunk_tick">https://minecraft.fandom.com/wiki/Tick#Chunk_tick</a>
     *
     * @param chunk The chunk to load
     *
     * @return true, if the chunk has been successfully loaded
     *
     * @see #unloadAnchoredChunk(Chunk)
     */
    public abstract boolean loadAnchoredChunk(Chunk chunk);

    /**
     * Tries unloading a given chunk if there are no players inside.<br>
     * Any logic introduced in {@link #loadAnchoredChunk(Chunk)} to keep a chunk loaded is removed.
     *
     * @param chunk The chunk to unload
     *
     * @return true, if the chunk has been successfully unloaded
     */
    public abstract boolean unloadAnchoredChunk(Chunk chunk);

    /**
     * Ticks all inactive spawners in a specific chunk ignoring the minimum required players within a specific range.<br>
     * A spawner is deemed inactive if the server should already be ticking it.
     *
     * @param chunk  The chunk to tick the spawners in
     * @param amount The amount of ticks to execute for each spawner
     */
    public abstract void tickInactiveSpawners(Chunk chunk, int amount);

    /**
     * Performs random ticks on a specific chunks.
     * <br><br>
     * More information: <a href="https://minecraft.fandom.com/wiki/Tick#Random_tick">https://minecraft.fandom.com/wiki/Tick#Random_tick</a>
     *
     * @param chunk      The chunk to tick
     * @param tickAmount The number of blocks to tick per ChunkSection, normally referred to as <code>randomTickSpeed</code>
     */
    public abstract void doRandomTick(Chunk chunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException;

    /**
     * Returns the current value for the GameRule <code>randomTickSpeed</code>.
     *
     * @param world The world to retrieve the value from
     *
     * @return The current value or 3 if the GameRule does not exist
     */
    public abstract int getRandomTickSpeed(World world);

    protected static class Helper {
        private Helper() {
            throw new IllegalStateException("Utility class");
        }

        public static int getRandomTickSpeedLegacy(World world) {
            try {
                return Integer.parseInt(world.getGameRuleValue("randomTickSpeed"));
            } catch (NumberFormatException ignore) {
                return 3;
            }
        }
    }
}
