package dev.gazesnatchers.client.render;

import dev.gazesnatchers.client.model.GazeSnatcherModel;
import dev.gazesnatchers.entity.GazeSnatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class GazeSnatcherRenderer extends MobRenderer<GazeSnatcherEntity, LivingEntityRenderState, GazeSnatcherModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "gazesnatchers", "textures/entity/gaze_snatcher.png");

    public GazeSnatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new GazeSnatcherModel(context.bakeLayer(GazeSnatcherModel.LAYER)), 0.45F);
        this.addLayer(new GazeSnatcherEyesLayer(this));
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
