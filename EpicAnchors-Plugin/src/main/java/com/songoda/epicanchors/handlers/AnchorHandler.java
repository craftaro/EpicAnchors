package com.songoda.epicanchors.handlers;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.anchor.EAnchor;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicspawners.api.EpicSpawnersAPI;
import com.songoda.epicspawners.api.spawner.Spawner;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class AnchorHandler {

    private EpicAnchorsPlugin instance;
    private Map<Location, Integer> delays = new HashMap<>();

    private boolean epicSpawners;

    public AnchorHandler(EpicAnchorsPlugin instance) {
        this.instance = instance;

        epicSpawners =  instance.getServer().getPluginManager().getPlugin("EpicSpawners") != null;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this::doAnchorCheck, 0, 20); //ToDo: way to fast.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this::doParticle, 0, 2); //ToDo: way to fast.
    }

    private void doParticle() {
        for (Anchor anchor : instance.getAnchorManager().getAnchors().values()) {
            Location location1 = anchor.getLocation().add(.5, .5, .5);
            if (location1 == null || location1.getWorld() == null) continue;
            float xx = (float) (0 + (Math.random() * .15));
            float yy = (float) (0 + (Math.random() * 1));
            float zz = (float) (0 + (Math.random() * .15));
            Arconix.pl().getApi().packetLibrary.getParticleManager().broadcastParticle(location1, xx, yy, zz, 0, "SPELL", 5);

            xx = (float) (0 + (Math.random() * .75));
            yy = (float) (0 + (Math.random() * 1));
            zz = (float) (0 + (Math.random() * .75));
            Arconix.pl().getApi().packetLibrary.getParticleManager().broadcastParticle(location1, xx, yy, zz, 0, "REDSTONE", 1);
        }
    }

    private void doAnchorCheck() {
        for (Anchor anchor : instance.getAnchorManager().getAnchors().values()) {

            if (anchor.getLocation() == null || anchor.getLocation().getBlock().getType() != Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material"))) continue;

            instance.getMenuHandler().updateMenu();

            Location location = anchor.getLocation();

            location.getChunk().load();

            Chunk chunk = location.getChunk();

            int cx = chunk.getX() << 4;
            int cz = chunk.getZ() << 4;

            int ticksLeft = anchor.getTicksLeft();
            anchor.setTicksLeft(ticksLeft - 20);

            if (ticksLeft <= 0) {
                instance.getAnchorManager().removeAnchor(location);
                Arconix.pl().getApi().packetLibrary.getParticleManager().broadcastParticle(location.clone().add(.5, .5, .5), 0, 0, 0, 0, "LAVA", 10);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10, 10);
                location.getBlock().setType(Material.AIR);
                instance.getMenuHandler().removeAnchor(location);
                chunk.unload();
            }


            if (!epicSpawners) continue;

            for (int x = cx; x < cx + 16; x++) {
                for (int z = cz; z < cz + 16; z++) {
                    for (int y = 0; y < location.getWorld().getMaxHeight(); y++) {
                        Block block = location.getWorld().getBlockAt(x, y, z);
                        if (block.getType() != Material.SPAWNER) continue;
                        Spawner spawner = EpicSpawnersAPI.getSpawnerManager().getSpawnerFromWorld(block.getLocation());
                        if (!delays.containsKey(block.getLocation())) {
                            if (block == null || block.getLocation() == null || spawner == null) continue;
                            delays.put(block.getLocation(), spawner.updateDelay());
                            continue;
                        }
                        int delay = delays.get(block.getLocation());
                        delay -= 20;
                        delays.put(block.getLocation(), delay);
                        if (delay <= 0) {
                            spawner.spawn();
                            delays.remove(block.getLocation());
                        }
                    }

                }
            }

        }

    }
}
