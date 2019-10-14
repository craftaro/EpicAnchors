package com.songoda.epicanchors.tasks;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.anchor.AnchorManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class VisualizeTask extends BukkitRunnable {

    private static VisualizeTask instance;
    private static EpicAnchors plugin;
    private final static Map<OfflinePlayer, Boolean> active = new ConcurrentHashMap();
    private final static Random random = new Random();
    int radius;

    public VisualizeTask(EpicAnchors plug) {
        plugin = plug;
        radius = Bukkit.getServer().getViewDistance();
        if (instance == null) {
            instance = this;
            instance.runTaskTimerAsynchronously(plugin, 60, 10);
        }
    }

    public static boolean togglePlayer(Player p) {
        Boolean isActive = active.get(p);
        active.put(p, isActive = (isActive == null || !isActive));
        return isActive;
    }

    public static void removePlayer(Player p) {
        active.remove(p);
    }

    @Override
    public void run() {
        active.entrySet().stream()
                .filter(e -> e.getValue() && e.getKey().isOnline())
                .forEach(e -> particleTick((Player) e.getKey()));
    }

    void particleTick(Player player) {
        final AnchorManager anchorManager = plugin.getAnchorManager();
        final Location playerLocation = player.getLocation();
        final World world = playerLocation.getWorld();
        // start and stop chunk coordinates
        int startY = playerLocation.getBlockY() + 1;
        int cxi = (playerLocation.getBlockX() >> 4) - radius, cxn = cxi + radius * 2;
        int czi = (playerLocation.getBlockZ() >> 4) - radius, czn = czi + radius * 2;
        // loop through the chunks to find applicable ones
        for (int cx = cxi; cx < cxn; cx++) {
            for (int cz = czi; cz < czn; cz++) {
                // sanity check
                if (!world.isChunkLoaded(cx, cz))
                    continue;

                // so! Is this a claimed chunk?
                Anchor anchor = anchorManager.getAnchor(world.getName(), cx, cz);
                if (anchor != null) {
                    // we found one!
                    // now we get to spawn the silly particles for the player
                    showChunkParticles(player, world.getChunkAt(cx, cz), startY);
                }
            }
        }
    }

    void showChunkParticles(Player player, Chunk c, int startY) {
        // loop through the chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // show about 1/5 of the blocks per tick
                boolean show = random.nextFloat() < .2;
                if (!show)
                    continue;

                // Exclude everything over max height
                if (startY >= c.getWorld().getMaxHeight()) continue;

                // only show if there is a space to show above a solid block
                Block b = c.getBlock(x, startY, z);
                int maxDown = 8;
                do {
                    show = b.getType().isTransparent() && !(b = b.getRelative(BlockFace.DOWN)).getType().isTransparent();
                } while (--maxDown > 0 && !show);

                // can we do this?
                if (show) {
                    final Location loc = b.getLocation().add(.5, 1.5, .5);

                    player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 0, 0, 0, 0, 1);
                }
            }
        }
    }
}
