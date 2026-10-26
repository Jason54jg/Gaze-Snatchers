package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.geo.GazeSnatcherGeoModel;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;

public class GazeSnatcherGeoRenderer extends GeoEntityRenderer<GazeSnatcherEntity, LivingEntityRenderState> {
    public GazeSnatcherGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new GazeSnatcherGeoModel());
    }
}
