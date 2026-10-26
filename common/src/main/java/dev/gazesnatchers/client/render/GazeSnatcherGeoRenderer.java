package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.geo.GazeSnatcherGeoModel;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GazeSnatcherGeoRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<GazeSnatcherEntity, R> {
    public GazeSnatcherGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new GazeSnatcherGeoModel());
    }
}
