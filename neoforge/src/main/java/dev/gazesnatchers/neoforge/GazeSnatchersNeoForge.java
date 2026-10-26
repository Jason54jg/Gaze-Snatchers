package dev.gazesnatchers.neoforge;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

@Mod(GazeSnatchers.MOD_ID)
public class GazeSnatchersNeoForge {
    public GazeSnatchersNeoForge(IEventBus modEventBus) {
        SleepWatcher.bindPlatformHooks(() -> modEventBus.addListener(GazeSnatchersNeoForge::onCanPlayerSleep));
        modEventBus.addListener(GazeSnatchersNeoForge::registerAttributes);
        GazeSnatchers.init();
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GAZE_SNATCHER, GazeSnatcherEntity.createAttributes().build());
        event.put(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherEntity.createAttributes().build());
    }

    private static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getVanillaProblem() == null) {
            SleepWatcher.onPlayerStartSleeping(event.getEntity(), event.getPos());
        }
    }
}
