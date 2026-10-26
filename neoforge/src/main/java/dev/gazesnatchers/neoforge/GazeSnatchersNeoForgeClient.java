package dev.gazesnatchers.neoforge;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.client.render.BabyGazeSnatcherGeoRenderer;
import dev.gazesnatchers.client.render.GazeSnatcherGeoRenderer;
import dev.gazesnatchers.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = GazeSnatchers.MOD_ID, dist = Dist.CLIENT)
public class GazeSnatchersNeoForgeClient {
    public GazeSnatchersNeoForgeClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(GazeSnatchersNeoForgeClient::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GAZE_SNATCHER, GazeSnatcherGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherGeoRenderer::new);
    }
}
