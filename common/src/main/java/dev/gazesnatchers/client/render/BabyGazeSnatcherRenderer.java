package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class BabyGazeSnatcherRenderer extends MobRenderer<BabyGazeSnatcherEntity, LivingEntityRenderState, BabyGazeSnatcherModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "gazesnatchers", "textures/entity/baby_gaze_snatcher.png");

    public BabyGazeSnatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new BabyGazeSnatcherModel(context.bakeLayer(BabyGazeSnatcherModel.LAYER)), 0.28F);
        this.addLayer(new BabyGazeSnatcherEyesLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
