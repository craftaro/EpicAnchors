package epicanchors.nms;

import com.songoda.epicanchors.AnchorNMS;
import com.songoda.epicanchors.utils.ReflectionUtils;
import com.songoda.epicanchors.utils.Utils;
import net.minecraft.server.v1_9_R2.AxisAlignedBB;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.ChunkRegionLoader;
import net.minecraft.server.v1_9_R2.ChunkSection;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.MobSpawnerAbstract;
import net.minecraft.server.v1_9_R2.MobSpawnerData;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagList;
import net.minecraft.server.v1_9_R2.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_9_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R2.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_9_R2.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class v1_9_R2 extends AnchorNMS {
    @SuppressWarnings("unused")
    public v1_9_R2(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean loadAnchoredChunk(Chunk chunk) {
        return chunk.load();
    }

    @Override
    public boolean unloadAnchoredChunk(Chunk chunk) {
        return chunk.unload();
    }

    @Override
    public void tickInactiveSpawners(Chunk chunk, int amount) {
        if (amount <= 0) return;

        try {
            for (BlockState tileEntity : chunk.getTileEntities()) {
                if (tileEntity instanceof CreatureSpawner) {
                    MobSpawnerAbstract spawner = NotchianBaseSpawner.getNotchianSpawner((CraftCreatureSpawner) tileEntity);

                    for (int i = 0; i < amount; ++i) {
                        if (!NotchianBaseSpawner.tickInactiveSpawner(spawner)) {
                            break; // Spawner not inactive
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Utils.logException(super.plugin, ex);
        }
    }

    @Override
    public void doRandomTick(Chunk chunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException {
        NotchianServerLevel.randomTickChunk(((CraftChunk) chunk).getHandle(), tickAmount);
    }

    @Override
    public int getRandomTickSpeed(World world) {
        return Helper.getRandomTickSpeedLegacy(world);
    }

    /**
     * This class contains some modified methods from {@link WorldServer}.
     */
    private static class NotchianServerLevel {
        /**
         * Method is based on {@link WorldServer#j()}.
         */
        static void randomTickChunk(net.minecraft.server.v1_9_R2.Chunk chunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException {
            if (tickAmount > 0) {
                int j = chunk.locX * 16;
                int k = chunk.locZ * 16;

                for (ChunkSection chunksection : chunk.getSections()) {
                    if (chunksection != net.minecraft.server.v1_9_R2.Chunk.a && chunksection.shouldTick()) {
                        for (int k1 = 0; k1 < tickAmount; ++k1) {
                            int worldL = (int) ReflectionUtils.getFieldValue(chunk.world, "l");
                            worldL = worldL * 3 + 1013904223;
                            ReflectionUtils.setFieldValue(chunk.world, "l", worldL);

                            int l1 = worldL >> 2;
                            int i2 = l1 & 15;
                            int j2 = l1 >> 8 & 15;
                            int k2 = l1 >> 16 & 15;

                            IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                            Block block = iblockdata.getBlock();

                            if (block.isTicking()) {
                                block.a(chunk.world, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), iblockdata, chunk.world.random);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This class contains some modified methods from {@link MobSpawnerAbstract}.
     */
    private static class NotchianBaseSpawner {
        private static Method iMethod, hMethod;

        static {
            try {
                iMethod = MobSpawnerAbstract.class.getDeclaredMethod("i");
                iMethod.setAccessible(true);

                hMethod = MobSpawnerAbstract.class.getDeclaredMethod("h");
                hMethod.setAccessible(true);
            } catch (NoSuchMethodException ex) {
                Utils.logException(null, ex);
            }
        }

        static MobSpawnerAbstract getNotchianSpawner(CraftCreatureSpawner spawner) throws NoSuchFieldException, IllegalAccessException {
            Object cTileEntity = ReflectionUtils.getFieldValue(spawner, "spawner");

            return (MobSpawnerAbstract) ReflectionUtils.getFieldValue(cTileEntity, "a");
        }

        /**
         * This method calls {@link MobSpawnerAbstract#h()} using Reflections.
         */
        static boolean isNearPlayer(MobSpawnerAbstract spawner) throws InvocationTargetException, IllegalAccessException {
            return (boolean) hMethod.invoke(spawner);
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#c()}.
         *
         * @return false if the spawner is not inactive, true otherwise
         */
        static boolean tickInactiveSpawner(MobSpawnerAbstract spawner) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            if (isNearPlayer(spawner)) return false;

            BlockPosition blockposition = spawner.b();

            if (spawner.a().isClientSide) {
                double d0 = (float) blockposition.getX() + spawner.a().random.nextFloat();
                double d1 = (float) blockposition.getY() + spawner.a().random.nextFloat();
                double d2 = (float) blockposition.getZ() + spawner.a().random.nextFloat();

                spawner.a().addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, 0D, 0D, 0D);
                spawner.a().addParticle(EnumParticle.FLAME, d0, d1, d2, 0D, 0D, 0D);

                if (spawner.spawnDelay > 0) {
                    --spawner.spawnDelay;
                }

                double spawnerD = (double) ReflectionUtils.getFieldValue(spawner, "d");

                ReflectionUtils.setFieldValue(spawner, "e", spawnerD);
                ReflectionUtils.setFieldValue(spawner, "d", (spawnerD + (double) (1000F / ((float) spawner.spawnDelay + 200F))) % 360D);
            } else {
                if (spawner.spawnDelay == -1) {
                    delay(spawner);
                }

                if (spawner.spawnDelay > 0) {
                    --spawner.spawnDelay;
                    return true;
                }

                boolean flag = false;
                int i = 0;

                int spawnCount = (int) ReflectionUtils.getFieldValue(spawner, "spawnCount");
                int spawnRange = (int) ReflectionUtils.getFieldValue(spawner, "spawnRange");
                int maxNearbyEntities = (int) ReflectionUtils.getFieldValue(spawner, "maxNearbyEntities");
                MobSpawnerData spawnData = (MobSpawnerData) ReflectionUtils.getFieldValue(spawner, "spawnData");

                while (true) {
                    if (i >= spawnCount) {
                        if (flag) {
                            delay(spawner);
                        }
                        break;
                    }

                    NBTTagCompound nbttagcompound = spawnData.b();
                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
                    net.minecraft.server.v1_9_R2.World world = spawner.a();
                    int j = nbttaglist.size();
                    double d3 = j >= 1 ? nbttaglist.e(0) : (double) blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawnRange + .5D;
                    double d4 = j >= 2 ? nbttaglist.e(1) : (double) (blockposition.getY() + world.random.nextInt(3) - 1);
                    double d5 = j >= 3 ? nbttaglist.e(2) : (double) blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawnRange + .5D;
                    Entity entity = ChunkRegionLoader.a(nbttagcompound, world, d3, d4, d5, false);
                    if (entity == null) {
                        return true;
                    }

                    int k = world.a(entity.getClass(), (new AxisAlignedBB(
                            blockposition.getX(),
                            blockposition.getY(),
                            blockposition.getZ(),
                            blockposition.getX() + 1,
                            blockposition.getY() + 1,
                            blockposition.getZ() + 1))
                            .g(spawnRange)).size();
                    if (k >= maxNearbyEntities) {
                        delay(spawner);
                        return true;
                    }

                    EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;
                    entity.setPositionRotation(entity.locX, entity.locY, entity.locZ, world.random.nextFloat() * 360.0F, 0.0F);
                    if (entityinsentient == null || entityinsentient.cG() && entityinsentient.canSpawn()) {
                        if (spawnData.b().d() == 1 && spawnData.b().hasKeyOfType("id", 8) && entity instanceof EntityInsentient) {
                            ((EntityInsentient) entity).prepare(world.D(new BlockPosition(entity)), null);
                        }

                        if (entity.world.spigotConfig.nerfSpawnerMobs) {
                            entity.fromMobSpawner = true;
                        }

                        if (!CraftEventFactory.callSpawnerSpawnEvent(entity, blockposition).isCancelled()) {
                            ChunkRegionLoader.a(entity, world, CreatureSpawnEvent.SpawnReason.SPAWNER);
                            world.triggerEffect(2004, blockposition, 0);
                            if (entityinsentient != null) {
                                entityinsentient.doSpawnEffect();
                            }

                            flag = true;
                        }
                    }

                    ++i;
                }
            }

            return true;
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#i()}.
         */
        static void delay(MobSpawnerAbstract spawner) throws InvocationTargetException, IllegalAccessException {
            iMethod.invoke(spawner);
        }
    }
}
