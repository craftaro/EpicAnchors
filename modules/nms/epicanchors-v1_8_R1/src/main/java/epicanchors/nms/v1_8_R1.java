package epicanchors.nms;

import com.songoda.epicanchors.AnchorNMS;
import com.songoda.epicanchors.utils.ReflectionUtils;
import com.songoda.epicanchors.utils.Utils;
import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.ChunkSection;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.EntityTypes;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.IBlockData;
import net.minecraft.server.v1_8_R1.MobSpawnerAbstract;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftCreatureSpawner;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class v1_8_R1 extends AnchorNMS {
    @SuppressWarnings("unused")
    public v1_8_R1(JavaPlugin plugin) {
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
                    MobSpawnerAbstract spawner = MobSpawnerUtils.getNotchianSpawner((CraftCreatureSpawner) tileEntity);

                    for (int i = 0; i < amount; ++i) {
                        if (!MobSpawnerUtils.tickInactiveSpawner(spawner)) {
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
        WorldServerUtils.randomTickChunk(((CraftChunk) chunk).getHandle(), tickAmount);
    }

    @Override
    public int getRandomTickSpeed(World world) {
        return Helper.getRandomTickSpeedLegacy(world);
    }

    /**
     * This class contains some modified methods from {@link WorldServer}.
     */
    private static class WorldServerUtils {
        /**
         * Method is based on {@link WorldServer#h()}.
         */
        static void randomTickChunk(net.minecraft.server.v1_8_R1.Chunk chunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException {
            if (tickAmount > 0) {
                int k = chunk.locX * 16;
                int l = chunk.locZ * 16;

                for (ChunkSection cSection : chunk.getSections()) {
                    if (cSection != null && cSection.shouldTick()) {

                        for (int i = 0; i < tickAmount; ++i) {
                            int m = (int) ReflectionUtils.getFieldValue(chunk.world, "m");

                            m = m * 3 + 1013904223;
                            ReflectionUtils.setFieldValue(chunk.world, "m", m);

                            int i2 = m >> 2;
                            int j2 = i2 & 15;
                            int k2 = i2 >> 8 & 15;
                            int l2 = i2 >> 16 & 15;

                            BlockPosition blockposition2 = new BlockPosition(j2 + k, l2 + cSection.getYPosition(), k2 + l);
                            IBlockData iblockdata = cSection.getType(j2, l2, k2);
                            Block block = iblockdata.getBlock();

                            if (block.isTicking()) {
                                block.a(chunk.world, blockposition2, iblockdata, chunk.world.random);
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
    private static class MobSpawnerUtils {
        private static Method aEntityBooleanMethod, gMethod, hMethod;

        static {
            try {
                aEntityBooleanMethod = MobSpawnerAbstract.class.getDeclaredMethod("a", Entity.class, boolean.class);
                aEntityBooleanMethod.setAccessible(true);

                gMethod = MobSpawnerAbstract.class.getDeclaredMethod("g");
                gMethod.setAccessible(true);

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
         * This method calls {@link MobSpawnerAbstract#g()} using Reflections.
         */
        static boolean isNearPlayer(MobSpawnerAbstract spawner) throws InvocationTargetException, IllegalAccessException {
            return (boolean) gMethod.invoke(spawner);
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#c()}.
         *
         * @return false if the spawner is not inactive, true otherwise
         */
        static boolean tickInactiveSpawner(MobSpawnerAbstract spawner) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
            if (isNearPlayer(spawner)) return false;

            BlockPosition blockposition = spawner.b();

            if (spawner.a().isStatic) {
                double d1 = (float) blockposition.getX() + spawner.a().random.nextFloat();
                double d2 = (float) blockposition.getY() + spawner.a().random.nextFloat();
                double d0 = (float) blockposition.getZ() + spawner.a().random.nextFloat();

                spawner.a().addParticle(EnumParticle.SMOKE_NORMAL, d1, d2, d0, 0D, 0D, 0D);
                spawner.a().addParticle(EnumParticle.FLAME, d1, d2, d0, 0D, 0D, 0D);

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

                int spawnCount = (int) ReflectionUtils.getFieldValue(spawner, "spawnCount");
                int spawnRange = (int) ReflectionUtils.getFieldValue(spawner, "spawnRange");
                int maxNearbyEntities = (int) ReflectionUtils.getFieldValue(spawner, "maxNearbyEntities");
                while (true) {
                    if (i >= spawnCount) {
                        if (flag) {
                            delay(spawner);
                        }

                        break;
                    }

                    Entity entity = EntityTypes.createEntityByName(spawner.getMobName(), spawner.a());
                    if (entity == null) {
                        return true;
                    }

                    int j = spawner.a()
                            .a(entity.getClass(), (new AxisAlignedBB(blockposition.getX(),
                                    blockposition.getY(),
                                    blockposition.getZ(),
                                    blockposition.getX() + 1,
                                    blockposition.getY() + 1,
                                    blockposition.getZ() + 1))
                                    .grow(spawnRange, spawnRange, spawnRange)).size();

                    if (j >= maxNearbyEntities) {
                        delay(spawner);
                        return true;
                    }

                    double d0 = (double) blockposition.getX() + (spawner.a().random.nextDouble() - spawner.a().random.nextDouble()) * (double) spawnRange + 0.5D;
                    double d3 = blockposition.getY() + spawner.a().random.nextInt(3) - 1;
                    double d4 = (double) blockposition.getZ() + (spawner.a().random.nextDouble() - spawner.a().random.nextDouble()) * (double) spawnRange + 0.5D;

                    EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;
                    entity.setPositionRotation(d0, d3, d4, spawner.a().random.nextFloat() * 360.0F, 0.0F);

                    if (entityinsentient == null || entityinsentient.bQ() && entityinsentient.canSpawn()) {
                        aEntityBooleanMethod.invoke(spawner, entity, true);
                        spawner.a().triggerEffect(2004, blockposition, 0);

                        if (entityinsentient != null) {
                            entityinsentient.y();
                        }

                        flag = true;
                    }

                    ++i;
                }
            }

            return true;
        }

        /**
         * This method calls {@link MobSpawnerAbstract#h()} using Reflections.
         */
        static void delay(MobSpawnerAbstract spawner) throws IllegalAccessException, InvocationTargetException {
            hMethod.invoke(spawner);
        }
    }
}
