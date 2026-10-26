package dev.gazesnatchers.fabric;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.command.GazeSnatchersCommands;
import dev.gazesnatchers.sleep.SleepWatcher;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.level.ServerPlayer;

public class GazeSnatchersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SleepWatcher.bindPlatformHooks(GazeSnatchersFabric::registerSleepEvents);
        GazeSnatchers.bindPlatformCommands(() -> CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> GazeSnatchersCommands.register(dispatcher)));
        // Entities must exist before attribute registration (otherwise LivingEntity ctor NPE).
        GazeSnatchers.init();
        FabricDefaultAttributeRegistry.register(ModEntities.GAZE_SNATCHER, GazeSnatcherEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherEntity.createAttributes());
    }

    private static void registerSleepEvents() {
        EntitySleepEvents.START_SLEEPING.register((player, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                SleepWatcher.onPlayerStartSleeping(serverPlayer, pos);
            }
        });
    }
}
