package dev.gazesnatchers.sleep;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.config.GazeSnatchersConfig;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.util.WorldTime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Fait apparaître un Gaze Snatcher adulte lorsqu'un joueur tente de dormir la nuit.
 */
public final class SleepWatcher {
    private static Runnable platformRegistrar = () -> {
    };

    public static void bindPlatformHooks(Runnable registrar) {
        platformRegistrar = registrar;
    }

    public static void registerPlatformHooks() {
        platformRegistrar.run();
    }

    public static void onPlayerStartSleeping(ServerPlayer player, BlockPos bedPos) {
        try {
            trySpawn(player);
        } catch (RuntimeException ex) {
            GazeSnatchers.LOGGER.error("Échec du spawn Gaze Snatcher au sommeil", ex);
        }
    }

    private static void trySpawn(ServerPlayer player) {
        ServerLevel level = player.level();
        if (WorldTime.isDay(level)) {
            return;
        }
        RandomSource random = level.getRandom();
        if (random.nextDouble() > GazeSnatchersConfig.spawnChance()) {
            return;
        }

        GazeSnatcherEntity snatcher = ModEntities.GAZE_SNATCHER.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (snatcher == null) {
            return;
        }

        Vec3 forward = player.getViewVector(1.0F).normalize();
        Vec3 spawn = player.position().add(forward.scale(2.2D));
        BlockPos feet = BlockPos.containing(spawn.x, spawn.y, spawn.z);
        snatcher.setPos(spawn.x, feet.getY(), spawn.z);
        snatcher.setYRot(player.getYRot() + 180.0F);
        snatcher.linkSleeper(player);
        snatcher.finalizeSpawn(level, level.getCurrentDifficultyAt(feet), EntitySpawnReason.MOB_SUMMONED, null);
        if (!level.addFreshEntity(snatcher)) {
            GazeSnatchers.LOGGER.warn("addFreshEntity a refusé le Gaze Snatcher");
            return;
        }
        snatcher.playSound(ModSounds.GAZE_SNATCHER_SPAWN, 1.25F, 0.7F + random.nextFloat() * 0.25F);
    }

    private SleepWatcher() {
    }
}
