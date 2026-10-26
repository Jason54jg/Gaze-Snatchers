package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.geo.BabyGazeSnatcherGeoModel;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;

public class BabyGazeSnatcherGeoRenderer extends GeoEntityRenderer<BabyGazeSnatcherEntity, LivingEntityRenderState> {
    public BabyGazeSnatcherGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new BabyGazeSnatcherGeoModel());
    }
}
