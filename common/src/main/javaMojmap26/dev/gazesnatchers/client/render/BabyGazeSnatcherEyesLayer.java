package dev.gazesnatchers.client.render;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.client.model.BabyGazeSnatcherModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class BabyGazeSnatcherEyesLayer extends EyesLayer<LivingEntityRenderState, BabyGazeSnatcherModel> {
    private static final Identifier GLOW = GazeSnatchers.id("textures/entity/baby_gaze_snatcher_emissive.png");

    public BabyGazeSnatcherEyesLayer(RenderLayerParent<LivingEntityRenderState, BabyGazeSnatcherModel> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return RenderTypes.eyes(GLOW);
    }
}
