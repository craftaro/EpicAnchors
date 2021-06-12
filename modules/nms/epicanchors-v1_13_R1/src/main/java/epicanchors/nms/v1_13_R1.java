package epicanchors.nms;

import com.songoda.epicanchors.AnchorNMS;
import com.songoda.epicanchors.utils.ReflectionUtils;
import com.songoda.epicanchors.utils.Utils;
import net.minecraft.server.v1_13_R1.AxisAlignedBB;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ChunkRegionLoader;
import net.minecraft.server.v1_13_R1.ChunkSection;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.EntityInsentient;
import net.minecraft.server.v1_13_R1.Fluid;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.MobSpawnerAbstract;
import net.minecraft.server.v1_13_R1.MobSpawnerData;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.NBTTagList;
import net.minecraft.server.v1_13_R1.Particles;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_13_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R1.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_13_R1.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class v1_13_R1 extends AnchorNMS {
    @SuppressWarnings("unused")
    public v1_13_R1(JavaPlugin plugin) {
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
        Integer result = world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED);

        if (result == null) {
            result = world.getGameRuleDefault(GameRule.RANDOM_TICK_SPEED);
        }

        return result == null ? 3 : result;
    }

    /**
     * This class contains some modified methods from {@link WorldServer}
     * which originally (vanilla server) goes by the name <code>ServerLevel</code>.
     */
    private static class NotchianServerLevel {
        /**
         * Method is based on {@link WorldServer#l()}.
         */
        static void randomTickChunk(net.minecraft.server.v1_13_R1.Chunk chunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException {
            if (tickAmount > 0) {
                int j = chunk.locX * 16;
                int k = chunk.locZ * 16;

                for (ChunkSection chunksection : chunk.getSections()) {
                    if (chunksection != net.minecraft.server.v1_13_R1.Chunk.a && chunksection.b()) {
                        for (int i = 0; i < tickAmount; ++i) {
                            int worldM = (int) ReflectionUtils.getFieldValue(chunk.world, "m");
                            worldM = worldM * 3 + 1013904223;
                            ReflectionUtils.setFieldValue(chunk.world, "m", worldM);

                            int l1 = worldM >> 2;
                            int i2 = l1 & 15;
                            int j2 = l1 >> 8 & 15;
                            int k2 = l1 >> 16 & 15;

                            IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                            Fluid fluid = chunksection.b(i2, k2, j2);

                            if (iblockdata.t()) {
                                iblockdata.b(chunk.world, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), chunk.world.random);
                            }

                            if (fluid.h()) {
                                fluid.b(chunk.world, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), chunk.world.random);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This class contains some modified methods from {@link MobSpawnerAbstract}
     * which originally (vanilla server) goes by the name <code>BaseSpawner</code>.
     */
    private static class NotchianBaseSpawner {
        private static Method iMethod;

        static {
            try {
                iMethod = MobSpawnerAbstract.class.getDeclaredMethod("i");
                iMethod.setAccessible(true);
            } catch (NoSuchMethodException ex) {
                Utils.logException(null, ex);
            }
        }

        static MobSpawnerAbstract getNotchianSpawner(CraftCreatureSpawner spawner) throws NoSuchFieldException, IllegalAccessException {
            Object cTileEntity = ReflectionUtils.getFieldValue(spawner, "tileEntity");

            return (MobSpawnerAbstract) ReflectionUtils.getFieldValue(cTileEntity, "a");
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#h()}.
         */
        static boolean isNearPlayer(MobSpawnerAbstract spawner) {
            BlockPosition blockposition = spawner.b();

            return spawner.a().isPlayerNearby(
                    (double) blockposition.getX() + .5D,
                    (double) blockposition.getY() + .5D,
                    (double) blockposition.getZ() + .5D,
                    spawner.requiredPlayerRange);
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#c()}.
         *
         * @return false if the spawner is not inactive, true otherwise
         */
        static boolean tickInactiveSpawner(MobSpawnerAbstract spawner) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
            if (isNearPlayer(spawner)) return false;

            BlockPosition blockposition = spawner.b();

            if (spawner.a().isClientSide) {
                double d0 = (float) blockposition.getX() + spawner.a().random.nextFloat();
                double d1 = (float) blockposition.getY() + spawner.a().random.nextFloat();
                double d2 = (float) blockposition.getZ() + spawner.a().random.nextFloat();

                spawner.a().addParticle(Particles.M, d0, d1, d2, 0D, 0D, 0D);
                spawner.a().addParticle(Particles.y, d0, d1, d2, 0D, 0D, 0D);

                if (spawner.spawnDelay > 0) {
                    --spawner.spawnDelay;
                }

                double spawnerE = (double) ReflectionUtils.getFieldValue(spawner, "e");

                ReflectionUtils.setFieldValue(spawner, "f", spawnerE);
                ReflectionUtils.setFieldValue(spawner, "e", (spawnerE + (double) (1000F / ((float) spawner.spawnDelay + 200F))) % 360D);
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

                MobSpawnerData spawnData = (MobSpawnerData) ReflectionUtils.getFieldValue(spawner, "spawnData");

                while (true) {
                    if (i >= spawner.spawnCount) {
                        if (flag) {
                            delay(spawner);
                        }

                        break;
                    }

                    NBTTagCompound nbttagcompound = spawnData.b();
                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);

                    net.minecraft.server.v1_13_R1.World world = spawner.a();

                    int j = nbttaglist.size();
                    double d3 = j >= 1 ? nbttaglist.k(0) : (double) blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawner.spawnRange + .5D;
                    double d4 = j >= 2 ? nbttaglist.k(1) : (double) (blockposition.getY() + world.random.nextInt(3) - 1);
                    double d5 = j >= 3 ? nbttaglist.k(2) : (double) blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawner.spawnRange + .5D;

                    Entity entity = ChunkRegionLoader.a(nbttagcompound, world, d3, d4, d5, false);

                    if (entity == null) {
                        delay(spawner);
                        return true;
                    }

                    int k = world.a(entity.getClass(), (new AxisAlignedBB(blockposition.getX(),
                            blockposition.getY(),
                            blockposition.getZ(),
                            blockposition.getX() + 1,
                            blockposition.getY() + 1,
                            blockposition.getZ() + 1))
                            .g(spawner.spawnRange)).size();

                    if (k >= spawner.maxNearbyEntities) {
                        delay(spawner);
                        return true;
                    }

                    EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;
                    entity.setPositionRotation(entity.locX, entity.locY, entity.locZ, world.random.nextFloat() * 360F, 0F);

                    if (entityinsentient == null || entityinsentient.M() && entityinsentient.canSpawn()) {
                        if (spawnData.b().d() == 1 && spawnData.b().hasKeyOfType("id", 8) && entity instanceof EntityInsentient) {
                            ((EntityInsentient) entity).prepare(world.getDamageScaler(new BlockPosition(entity)), null, null);
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
         * This method calls {@link MobSpawnerAbstract#i()} using Reflections.
         */
        static void delay(MobSpawnerAbstract spawner) throws InvocationTargetException, IllegalAccessException {
            iMethod.invoke(spawner);
        }
    }
}
