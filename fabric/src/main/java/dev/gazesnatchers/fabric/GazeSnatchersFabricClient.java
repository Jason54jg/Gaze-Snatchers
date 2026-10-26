package dev.gazesnatchers.fabric;

import dev.gazesnatchers.client.GazeSnatchersClient;
import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import dev.gazesnatchers.client.model.GazeSnatcherModel;
import dev.gazesnatchers.client.render.BabyGazeSnatcherRenderer;
import dev.gazesnatchers.client.render.GazeSnatcherRenderer;
import dev.gazesnatchers.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GazeSnatchersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(GazeSnatcherModel.LAYER, GazeSnatcherModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BabyGazeSnatcherModel.LAYER, BabyGazeSnatcherModel::createBodyLayer);
        EntityRendererRegistry.register(ModEntities.GAZE_SNATCHER, GazeSnatcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherRenderer::new);
    }
}
