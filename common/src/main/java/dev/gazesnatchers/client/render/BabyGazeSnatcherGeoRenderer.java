package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.geo.BabyGazeSnatcherGeoModel;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BabyGazeSnatcherGeoRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<BabyGazeSnatcherEntity, R> {
    public BabyGazeSnatcherGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new BabyGazeSnatcherGeoModel());
    }
}
