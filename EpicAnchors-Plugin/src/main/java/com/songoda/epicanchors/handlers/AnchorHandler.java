package com.songoda.epicanchors.handlers;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicspawners.api.EpicSpawnersAPI;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class AnchorHandler {

    private EpicAnchorsPlugin instance;
    private Map<Location, Integer> delays = new HashMap<>();

    private boolean epicSpawners;

    public AnchorHandler(EpicAnchorsPlugin instance) {
        this.instance = instance;

        epicSpawners =  instance.getServer().getPluginManager().getPlugin("EpicSpawners") != null;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this::doAnchorCheck, 0, 1); //ToDo: way to fast.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this::doParticle, 0, 2); //ToDo: way to fast.
    }

    private void doParticle() {
        for (Anchor anchor : instance.getAnchorManager().getAnchors().values()) {
            Location location1 = anchor.getLocation().add(.5, .5, .5);
            if (location1.getWorld() == null) continue;
            float xx = (float) (0 + (Math.random() * .15));
            float yy = (float) (0 + (Math.random() * 1));
            float zz = (float) (0 + (Math.random() * .15));
            location1.getWorld().spawnParticle(Particle.SPELL, location1, 5, xx, yy, zz, 5);

            xx = (float) (0 + (Math.random() * .75));
            yy = (float) (0 + (Math.random() * 1));
            zz = (float) (0 + (Math.random() * .75));
            location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 5, xx, yy, zz, 1, new Particle.DustOptions(Color.WHITE, 1F));
        }
    }

    private void doAnchorCheck() {
        for (Anchor anchor : instance.getAnchorManager().getAnchors().values()) {

            if (anchor.getLocation() == null || anchor.getLocation().getBlock().getType() != Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material"))) continue;

            instance.getMenuHandler().updateMenu();

            Location location = anchor.getLocation();

            Chunk chunk = location.getChunk();
            chunk.load();

            // Load entities
            for (Entity entity : chunk.getEntities()) {
                if (!(entity instanceof LivingEntity)) continue;

                ((EntityInsentient)entity).movementTick();

            }

            int ticksLeft = anchor.getTicksLeft();
            anchor.setTicksLeft(ticksLeft - 20);

            if (ticksLeft <= 0) {
                instance.getAnchorManager().removeAnchor(location);
                location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(.5, .5, .5), 5, 0, 0, 0, 5);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10, 10);
                location.getBlock().setType(Material.AIR);
                instance.getMenuHandler().removeAnchor(location);
                chunk.unload();
            }

            if (!epicSpawners) continue;

            EpicSpawnersAPI.getSpawnerManager().getSpawners().stream()
                    .filter(spawner -> spawner.getWorld().isChunkLoaded(spawner.getX() >> 4, spawner.getZ() >> 4)
                            && chunk == spawner.getLocation().getChunk()).forEach(spawner -> {
                Block block = spawner.getLocation().getBlock();

                if (!delays.containsKey(block.getLocation())) {
                    if (block.getLocation() == null) return;
                    delays.put(block.getLocation(), spawner.updateDelay());
                    return;
                }
                int delay = delays.get(block.getLocation());
                delay -= 20;
                delays.put(block.getLocation(), delay);
                if (delay <= 0) {
                    spawner.spawn();
                    delays.remove(block.getLocation());
                }

            });
        }
    }
}
