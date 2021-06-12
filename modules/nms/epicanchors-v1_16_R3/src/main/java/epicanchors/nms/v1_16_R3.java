package epicanchors.nms;

import com.songoda.epicanchors.AnchorNMS;
import com.songoda.epicanchors.utils.ReflectionUtils;
import com.songoda.epicanchors.utils.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Optional;

public class v1_16_R3 extends AnchorNMS {
    @SuppressWarnings("unused")
    public v1_16_R3(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean loadAnchoredChunk(Chunk chunk) {
        return chunk.addPluginChunkTicket(super.plugin);
    }

    @Override
    public boolean unloadAnchoredChunk(Chunk chunk) {
        chunk.removePluginChunkTicket(super.plugin);

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
    public void doRandomTick(Chunk chunk, int tickAmount) {
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
         * Method is based on {@link WorldServer#a(net.minecraft.server.v1_16_R3.Chunk, int)}.
         */
        static void randomTickChunk(net.minecraft.server.v1_16_R3.Chunk chunk, int tickAmount) {
            if (tickAmount > 0) {
                GameProfilerFiller profiler = chunk.world.getMethodProfiler();

                ChunkCoordIntPair chunkPos = chunk.getPos();
                int minBlockX = chunkPos.d();
                int minBlockZ = chunkPos.e();

                profiler.enter("tickBlocks");
                for (ChunkSection cSection : chunk.getSections()) {
                    if (cSection != net.minecraft.server.v1_16_R3.Chunk.a &&    // cSection != Chunk.EMPTY_SECTION
                            cSection.d()) { // #isRandomlyTicking()
                        int bottomBlockY = cSection.getYPosition();

                        for (int i = 0; i < tickAmount; ++i) {
                            BlockPosition randomBlockPos = chunk.world.a(minBlockX, bottomBlockY, minBlockZ, 15);   // getBlockRandomPos
                            profiler.enter("randomTick");

                            IBlockData blockState = cSection.getType(randomBlockPos.getX() - minBlockX,
                                    randomBlockPos.getY() - bottomBlockY,
                                    randomBlockPos.getZ() - minBlockZ);   // #getBlockState

                            if (blockState.isTicking()) {   // #isRandomlyTicking()
                                blockState.b(chunk.world, randomBlockPos, chunk.world.random);  // #randomTick
                            }

                            Fluid fluidState = blockState.getFluid();   // #getFluidState()
                            if (fluidState.f()) {   // #isRandomlyTicking()
                                fluidState.b(chunk.world, randomBlockPos, chunk.world.random);  // #randomTick
                            }

                            profiler.exit();
                        }
                    }
                }

                profiler.exit();
            }
        }
    }

    /**
     * This class contains some modified methods from {@link MobSpawnerAbstract}
     * which originally (vanilla server) goes by the name <code>BaseSpawner</code>.
     */
    private static class NotchianBaseSpawner {
        static MobSpawnerAbstract getNotchianSpawner(CraftCreatureSpawner spawner) throws NoSuchFieldException, IllegalAccessException {
            Object cTileEntity = ReflectionUtils.getFieldValue(spawner, "tileEntity");

            return (MobSpawnerAbstract) ReflectionUtils.getFieldValue(cTileEntity, "a");
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#h()}.
         */
        static boolean isNearPlayer(MobSpawnerAbstract spawner) {
            BlockPosition blockposition = spawner.b();

            return spawner.a()
                    .isPlayerNearby((double) blockposition.getX() + 0.5D,
                            (double) blockposition.getY() + 0.5D,
                            (double) blockposition.getZ() + 0.5D,
                            spawner.requiredPlayerRange);
        }

        /**
         * This method is based on {@link MobSpawnerAbstract#c()}.
         *
         * @return false if the spawner is not inactive, true otherwise
         */
        static boolean tickInactiveSpawner(MobSpawnerAbstract spawner) throws NoSuchFieldException, IllegalAccessException {
            if (isNearPlayer(spawner)) return false;

            net.minecraft.server.v1_16_R3.World world = spawner.a();
            BlockPosition blockposition = spawner.b();
            if (!(world instanceof WorldServer)) {
                double d0 = (double) blockposition.getX() + world.random.nextDouble();
                double d1 = (double) blockposition.getY() + world.random.nextDouble();
                double d2 = (double) blockposition.getZ() + world.random.nextDouble();

                world.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                world.addParticle(Particles.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);

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

                while (true) {
                    if (i >= spawner.spawnCount) {
                        if (flag) {
                            delay(spawner);
                        }

                        break;
                    }

                    NBTTagCompound nbttagcompound = spawner.spawnData.getEntity();
                    Optional<EntityTypes<?>> optional = EntityTypes.a(nbttagcompound);
                    if (!optional.isPresent()) {
                        delay(spawner);
                        return true;
                    }

                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
                    int j = nbttaglist.size();
                    double d3 = j >= 1 ? nbttaglist.h(0) : (double) blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawner.spawnRange + .5D;
                    double d4 = j >= 2 ? nbttaglist.h(1) : (double) (blockposition.getY() + world.random.nextInt(3) - 1);
                    double d5 = j >= 3 ? nbttaglist.h(2) : (double) blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) spawner.spawnRange + .5D;

                    if (world.b(optional.get().a(d3, d4, d5))) {
                        WorldServer worldserver = (WorldServer) world;
                        if (EntityPositionTypes.a(optional.get(), worldserver, EnumMobSpawn.SPAWNER, new BlockPosition(d3, d4, d5), world.getRandom())) {
                            label116:
                            {
                                Entity entity = EntityTypes.a(nbttagcompound, world, (entity1) -> {
                                    entity1.setPositionRotation(d3, d4, d5, entity1.yaw, entity1.pitch);
                                    return entity1;
                                });
                                if (entity == null) {
                                    delay(spawner);
                                    return true;
                                }

                                int k = world.a(entity.getClass(), (new AxisAlignedBB(
                                        blockposition.getX(),
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

                                entity.setPositionRotation(entity.locX(), entity.locY(), entity.locZ(), world.random.nextFloat() * 360F, 0F);
                                if (entity instanceof EntityInsentient) {
                                    EntityInsentient entityinsentient = (EntityInsentient) entity;
                                    if (!entityinsentient.a(world, EnumMobSpawn.SPAWNER) || !entityinsentient.a(world)) {
                                        break label116;
                                    }

                                    if (spawner.spawnData.getEntity().e() == 1 && spawner.spawnData.getEntity().hasKeyOfType("id", 8)) {
                                        ((EntityInsentient) entity).prepare(worldserver, world.getDamageScaler(entity.getChunkCoordinates()), EnumMobSpawn.SPAWNER, null, null);
                                    }

                                    if (entityinsentient.world.spigotConfig.nerfSpawnerMobs) {
                                        entityinsentient.aware = false;
                                    }
                                }

                                if (CraftEventFactory.callSpawnerSpawnEvent(entity, blockposition).isCancelled()) {
                                    Entity vehicle = entity.getVehicle();
                                    if (vehicle != null) {
                                        vehicle.dead = true;
                                    }

                                    Entity passenger;
                                    for (Iterator<Entity> var20 = entity.getAllPassengers().iterator(); var20.hasNext(); passenger.dead = true) {
                                        passenger = var20.next();
                                    }
                                } else {
                                    if (!worldserver.addAllEntitiesSafely(entity, CreatureSpawnEvent.SpawnReason.SPAWNER)) {
                                        delay(spawner);
                                        return true;
                                    }

                                    world.triggerEffect(2004, blockposition, 0);
                                    if (entity instanceof EntityInsentient) {
                                        ((EntityInsentient) entity).doSpawnEffect();
                                    }

                                    flag = true;
                                }
                            }
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
        static void delay(MobSpawnerAbstract spawner) {
            if (spawner.maxSpawnDelay <= spawner.minSpawnDelay) {
                spawner.spawnDelay = spawner.minSpawnDelay;
            } else {
                int i = spawner.maxSpawnDelay - spawner.minSpawnDelay;
                spawner.spawnDelay = spawner.minSpawnDelay + spawner.a().random.nextInt(i);
            }

            if (!spawner.mobs.isEmpty()) {
                spawner.setSpawnData(WeightedRandom.a(spawner.a().random, spawner.mobs));
            }

            spawner.a(1);
        }
    }
}
