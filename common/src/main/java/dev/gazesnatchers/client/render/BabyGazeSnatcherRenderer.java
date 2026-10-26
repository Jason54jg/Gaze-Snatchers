package dev.gazesnatchers.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import dev.gazesnatchers.entity.BabyGazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class BabyGazeSnatcherRenderer extends MobRenderer<BabyGazeSnatcherEntity, LivingEntityRenderState, BabyGazeSnatcherModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "gazesnatchers", "textures/entity/baby_gaze_snatcher.png");
    /** Modèle pieds@24 (~1.5 blocs) → hitbox ~0.55 */
    private static final float MODEL_SCALE = 0.38F;

    public BabyGazeSnatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new BabyGazeSnatcherModel(context.bakeLayer(BabyGazeSnatcherModel.LAYER)), 0.25F);
        this.addLayer(new BabyGazeSnatcherEyesLayer(this));
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
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
