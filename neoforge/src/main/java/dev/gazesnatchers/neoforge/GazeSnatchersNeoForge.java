package dev.gazesnatchers.neoforge;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.GazeSnatchersConstants;
import dev.gazesnatchers.command.GazeSnatchersCommands;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import dev.gazesnatchers.registry.ModCreativeTabs;
import dev.gazesnatchers.registry.ModEntities;
import dev.gazesnatchers.registry.ModItems;
import dev.gazesnatchers.registry.ModMobEffects;
import dev.gazesnatchers.registry.ModSounds;
import dev.gazesnatchers.sleep.SleepWatcher;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(GazeSnatchers.MOD_ID)
public class GazeSnatchersNeoForge {
    public GazeSnatchersNeoForge(IEventBus modEventBus) {
        GazeSnatchers.LOGGER.info("Initialisation de Gaze Snatchers…");
        SleepWatcher.bindPlatformHooks(() -> NeoForge.EVENT_BUS.addListener(GazeSnatchersNeoForge::onCanPlayerSleep));
        GazeSnatchers.bindPlatformCommands(() -> NeoForge.EVENT_BUS.addListener(GazeSnatchersNeoForge::registerCommands));
        modEventBus.addListener(GazeSnatchersNeoForge::onRegister);
        modEventBus.addListener(GazeSnatchersNeoForge::registerAttributes);
        GazeSnatchers.initEarly();
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> ModSounds.register(helper::register));
        event.register(Registries.MOB_EFFECT, helper -> ModMobEffects.register(helper::register));
        event.register(Registries.ENTITY_TYPE, helper -> ModEntities.register(helper::register));
        event.register(Registries.ITEM, helper -> {
            ModItems.register(helper::register);
            ModItems.registerSpawnEggs(helper::register);
        });
        event.register(Registries.CREATIVE_MODE_TAB, helper -> ModCreativeTabs.register(helper::register));
        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            GazeSnatchers.LOGGER.info("Gaze Snatchers prêt (MC {}).", GazeSnatchersConstants.MINECRAFT_RANGE);
        }
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        GazeSnatchersCommands.register(event.getDispatcher());
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
