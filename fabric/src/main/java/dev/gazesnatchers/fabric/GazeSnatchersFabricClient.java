package dev.gazesnatchers.fabric;

import dev.gazesnatchers.client.render.BabyGazeSnatcherGeoRenderer;
import dev.gazesnatchers.client.render.GazeSnatcherGeoRenderer;
import dev.gazesnatchers.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GazeSnatchersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.GAZE_SNATCHER, GazeSnatcherGeoRenderer::new);
        EntityRendererRegistry.register(ModEntities.BABY_GAZE_SNATCHER, BabyGazeSnatcherGeoRenderer::new);
    }
}
