package dev.gazesnatchers.fabric;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.command.GazeSnatchersCommands;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModCreativeTabs;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.level.ServerPlayer;

public class GazeSnatchersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SleepWatcher.bindPlatformHooks(GazeSnatchersFabric::registerSleepEvents);
        GazeSnatchers.bindPlatformCommands(() -> CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> GazeSnatchersCommands.register(dispatcher)));
        GazeSnatchers.init();
        FabricDefaultAttributeRegistry.register(ModEntities.GAZE_SNATCHER, GazeSnatcherEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherEntity.createAttributes());
        CreativeModeTabEvents.modifyOutputEvent(ModCreativeTabs.TAB_KEY).register(output -> {
            output.accept(ModItems.GAZE_SHARD);
            output.accept(ModItems.GAZE_WARD_AMULET);
            output.accept(ModItems.GAZE_DRAUGHT);
            output.accept(ModItems.GAZE_BINDING_CHARM);
            output.accept(ModItems.GAZE_SNATCHER_SPAWN_EGG);
            output.accept(ModItems.BABY_GAZE_SNATCHER_SPAWN_EGG);
        });
    }

    private static void registerSleepEvents() {
        EntitySleepEvents.START_SLEEPING.register((player, pos) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                SleepWatcher.onPlayerStartSleeping(serverPlayer, pos);
            }
        });
    }
}
