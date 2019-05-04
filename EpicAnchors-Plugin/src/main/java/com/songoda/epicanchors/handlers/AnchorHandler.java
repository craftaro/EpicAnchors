package com.songoda.epicanchors.handlers;

import com.songoda.epicanchors.EpicAnchorsPlugin;
import com.songoda.epicanchors.api.anchor.Anchor;
import com.songoda.epicanchors.utils.ServerVersion;
import com.songoda.epicspawners.api.EpicSpawnersAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnchorHandler {

    private EpicAnchorsPlugin instance;
    private Map<Location, Integer> delays = new HashMap<>();

    private Class<?> clazzEntity, clazzCraftEntity, clazzMinecraftServer;

    private Method methodTick, methodGetHandle;

    private Field fieldCurrentTick, fieldActivatedTick;

    private boolean epicSpawners;

    public AnchorHandler(EpicAnchorsPlugin instance) {
        this.instance = instance;

        try {
            String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            clazzMinecraftServer = Class.forName("net.minecraft.server." + ver + ".MinecraftServer");
            clazzEntity = Class.forName("net.minecraft.server." + ver + ".Entity");
            clazzCraftEntity = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftEntity");

            if (instance.isServerVersionAtLeast(ServerVersion.V1_13))
                methodTick = clazzEntity.getDeclaredMethod("tick");
            else if (instance.isServerVersion(ServerVersion.V1_12))
                methodTick = clazzEntity.getDeclaredMethod("B_");
            else if (instance.isServerVersion(ServerVersion.V1_11))
                methodTick = clazzEntity.getDeclaredMethod("A_");
            else if (instance.isServerVersionAtLeast(ServerVersion.V1_9))
                methodTick = clazzEntity.getDeclaredMethod("m");
            else
                methodTick = clazzEntity.getDeclaredMethod("t_");

            methodGetHandle = clazzCraftEntity.getDeclaredMethod("getHandle");

            fieldCurrentTick = clazzMinecraftServer.getDeclaredField("currentTick");
            fieldActivatedTick = clazzEntity.getDeclaredField("activatedTick");

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        epicSpawners =  instance.getServer().getPluginManager().getPlugin("EpicSpawners") != null;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this::doAnchorCheck, 0, 1); //ToDo: way to fast.
        if (instance.isServerVersionAtLeast(ServerVersion.V1_9))
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
            if (!instance.isServerVersionAtLeast(ServerVersion.V1_13))
                location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 5, xx, yy, zz, 1);
            else
                location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 5, xx, yy, zz, 1, new Particle.DustOptions(Color.WHITE, 1F));



        }
    }

    private void doAnchorCheck() {
        for (Anchor anchor : instance.getAnchorManager().getAnchors().values()) {

            if (anchor.getLocation() == null) continue;

            Location location = anchor.getLocation();

            if (anchor.getLocation().getBlock().getType() != Material.valueOf(instance.getConfig().getString("Main.Anchor Block Material")))
                continue;

            Chunk chunk = location.getChunk();
            chunk.load();

            // Load entities
            for (Entity entity : chunk.getEntities()) {
                if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;

                if (entity.getNearbyEntities(32, 32, 32).stream().anyMatch(entity1 -> entity1 instanceof Player)) {
                    continue;
                }

                try {
                    Object objCraftEntity = clazzCraftEntity.cast(entity);
                    Object objEntity = methodGetHandle.invoke(objCraftEntity);

                    fieldActivatedTick.set(objEntity, fieldCurrentTick.getLong(objEntity));
                    methodTick.invoke(objEntity);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            int ticksLeft = anchor.getTicksLeft();
            anchor.setTicksLeft(ticksLeft - 1);

            if (ticksLeft <= 0) {
                instance.getAnchorManager().removeAnchor(location);
                if (instance.isServerVersionAtLeast(ServerVersion.V1_9))
                    location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(.5, .5, .5), 5, 0, 0, 0, 5);
                location.getWorld().playSound(location, instance.isServerVersionAtLeast(ServerVersion.V1_13)
                        ? Sound.ENTITY_GENERIC_EXPLODE : Sound.valueOf("EXLODE"), 10, 10);
                location.getBlock().setType(Material.AIR);
                chunk.unload();
            }

            if (!epicSpawners || EpicSpawnersAPI.getSpawnerManager() == null) continue;

            EpicSpawnersAPI.getSpawnerManager().getSpawners().stream()
                    .filter(spawner -> spawner.getWorld().isChunkLoaded(spawner.getX() >> 4, spawner.getZ() >> 4)
                            && chunk == spawner.getLocation().getChunk()).forEach(spawner -> {
                Block block = spawner.getLocation().getBlock();

                if (!delays.containsKey(block.getLocation())) {
                    delays.put(block.getLocation(), spawner.updateDelay());
                    return;
                }
                int delay = delays.get(block.getLocation());
                delay -= 1;
                delays.put(block.getLocation(), delay);
                if (delay <= 0) {
                    spawner.spawn();
                    delays.remove(block.getLocation());
                }
            });
        }
    }
}
