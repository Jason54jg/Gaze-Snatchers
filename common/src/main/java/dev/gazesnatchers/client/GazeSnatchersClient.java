package dev.gazesnatchers.client;

import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import dev.gazesnatchers.client.model.GazeSnatcherModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;

/** Helpers client communs — l'enregistrement des renderers se fait côté Fabric / NeoForge. */
public final class GazeSnatchersClient {
    public static void registerModelLayers(
            BiConsumer<ModelLayerLocation, LayerDefinition> registrar) {
        registrar.accept(GazeSnatcherModel.LAYER, GazeSnatcherModel.createBodyLayer());
        registrar.accept(BabyGazeSnatcherModel.LAYER, BabyGazeSnatcherModel.createBodyLayer());
    }

    private GazeSnatchersClient() {
    }
}
