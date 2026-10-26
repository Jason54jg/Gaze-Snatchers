package dev.gazesnatchers.neoforge;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.command.GazeSnatchersCommands;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModCreativeTabs;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

@Mod(GazeSnatchers.MOD_ID)
public class GazeSnatchersNeoForge {
    public GazeSnatchersNeoForge(IEventBus modEventBus) {
        SleepWatcher.bindPlatformHooks(() -> NeoForge.EVENT_BUS.addListener(GazeSnatchersNeoForge::onCanPlayerSleep));
        GazeSnatchers.bindPlatformCommands(() -> NeoForge.EVENT_BUS.addListener(GazeSnatchersNeoForge::registerCommands));
        modEventBus.addListener(GazeSnatchersNeoForge::registerAttributes);
        modEventBus.addListener(GazeSnatchersNeoForge::buildCreativeTab);
        GazeSnatchers.init();
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        GazeSnatchersCommands.register(event.getDispatcher());
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GAZE_SNATCHER, GazeSnatcherEntity.createAttributes().build());
        event.put(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherEntity.createAttributes().build());
    }

    private static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeTabs.TAB_KEY) {
            event.accept(ModItems.GAZE_SHARD);
            event.accept(ModItems.GAZE_WARD_AMULET);
            event.accept(ModItems.GAZE_DRAUGHT);
            event.accept(ModItems.GAZE_SNATCHER_SPAWN_EGG);
            event.accept(ModItems.BABY_GAZE_SNATCHER_SPAWN_EGG);
        }
    }

    private static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getVanillaProblem() == null) {
            SleepWatcher.onPlayerStartSleeping(event.getEntity(), event.getPos());
        }
    }
}
