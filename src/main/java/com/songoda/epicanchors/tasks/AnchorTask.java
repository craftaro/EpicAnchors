package com.songoda.epicanchors.tasks;

import com.songoda.epicanchors.EpicAnchors;
import com.songoda.epicanchors.anchor.Anchor;
import com.songoda.epicanchors.utils.ServerVersion;
import com.songoda.epicspawners.EpicSpawners;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnchorTask extends BukkitRunnable {

    private static EpicAnchors plugin;

    private Map<Location, Integer> delays = new HashMap<>();

    private Class<?> clazzEntity, clazzCraftEntity, clazzMinecraftServer;

    private Method methodTick, methodGetHandle;

    private Field fieldCurrentTick, fieldActivatedTick;

    private boolean epicSpawners;


    public AnchorTask(EpicAnchors plug) {
        plugin = plug;
        epicSpawners = Bukkit.getPluginManager().getPlugin("EpicSpawners") != null;

        try {
            String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            clazzMinecraftServer = Class.forName("net.minecraft.server." + ver + ".MinecraftServer");
            clazzEntity = Class.forName("net.minecraft.server." + ver + ".Entity");
            clazzCraftEntity = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftEntity");

            if (plugin.isServerVersionAtLeast(ServerVersion.V1_13))
                methodTick = clazzEntity.getDeclaredMethod("tick");
            else if (plugin.isServerVersion(ServerVersion.V1_12))
                methodTick = clazzEntity.getDeclaredMethod("B_");
            else if (plugin.isServerVersion(ServerVersion.V1_11))
                methodTick = clazzEntity.getDeclaredMethod("A_");
            else if (plugin.isServerVersionAtLeast(ServerVersion.V1_9))
                methodTick = clazzEntity.getDeclaredMethod("m");
            else
                methodTick = clazzEntity.getDeclaredMethod("t_");

            methodGetHandle = clazzCraftEntity.getDeclaredMethod("getHandle");

            fieldCurrentTick = clazzMinecraftServer.getDeclaredField("currentTick");
            fieldActivatedTick = clazzEntity.getDeclaredField("activatedTick");

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        this.runTaskTimer(plugin, 0, 3);
    }

    private void doParticle() {
        for (Anchor anchor : plugin.getAnchorManager().getAnchors().values()) {
            Location location1 = anchor.getLocation().add(.5, .5, .5);
            if (location1.getWorld() == null) continue;
            float xx = (float) (0 + (Math.random() * .75));
            float yy = (float) (0 + (Math.random() * 1));
            float zz = (float) (0 + (Math.random() * .75));
            if (!plugin.isServerVersionAtLeast(ServerVersion.V1_13))
                location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 5, xx, yy, zz, 1);
            else
                location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 5, xx, yy, zz, 1, new Particle.DustOptions(Color.WHITE, 1F));
        }
    }

    @Override
    public void run() {
        if (plugin.isServerVersionAtLeast(ServerVersion.V1_9))
            doParticle();
        for (Anchor anchor : plugin.getAnchorManager().getAnchors().values()) {

            if (anchor.getLocation() == null) continue;

            if (plugin.getHologram() != null)
                plugin.getHologram().update(anchor);

            Location location = anchor.getLocation();

            if (anchor.getLocation().getBlock().getType() != Material.valueOf(plugin.getConfig().getString("Main.Anchor Block Material")))
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
            anchor.setTicksLeft(ticksLeft - 3);

            if (ticksLeft <= 0) {
                plugin.getAnchorManager().removeAnchor(location);
                if (plugin.isServerVersionAtLeast(ServerVersion.V1_9))
                    location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(.5, .5, .5), 5, 0, 0, 0, 5);
                location.getWorld().playSound(location, plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                        ? Sound.ENTITY_GENERIC_EXPLODE : Sound.valueOf("EXPLODE"), 10, 10);
                location.getBlock().setType(Material.AIR);
                chunk.unload();
            }

            if (!epicSpawners || EpicSpawners.getInstance().getSpawnerManager() == null) continue;

            EpicSpawners.getInstance().getSpawnerManager().getSpawners().stream()
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
