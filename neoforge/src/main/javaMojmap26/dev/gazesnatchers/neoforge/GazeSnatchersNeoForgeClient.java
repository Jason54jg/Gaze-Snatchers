package dev.gazesnatchers.neoforge;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import dev.gazesnatchers.client.model.GazeSnatcherModel;
import dev.gazesnatchers.client.render.BabyGazeSnatcherRenderer;
import dev.gazesnatchers.client.render.GazeSnatcherRenderer;
import dev.gazesnatchers.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = GazeSnatchers.MOD_ID, dist = Dist.CLIENT)
public class GazeSnatchersNeoForgeClient {
    public GazeSnatchersNeoForgeClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(GazeSnatchersNeoForgeClient::registerLayerDefinitions);
        modEventBus.addListener(GazeSnatchersNeoForgeClient::registerRenderers);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GazeSnatcherModel.LAYER, GazeSnatcherModel::createBodyLayer);
        event.registerLayerDefinition(BabyGazeSnatcherModel.LAYER, BabyGazeSnatcherModel::createBodyLayer);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GAZE_SNATCHER, GazeSnatcherRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherRenderer::new);
    }
}
