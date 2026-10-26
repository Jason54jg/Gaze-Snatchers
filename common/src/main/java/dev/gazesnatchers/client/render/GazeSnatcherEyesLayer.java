package dev.gazesnatchers.client.render;

import dev.gazesnatchers.GazeSnatchers;
import dev.gazesnatchers.client.model.GazeSnatcherModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class GazeSnatcherEyesLayer extends EyesLayer<LivingEntityRenderState, GazeSnatcherModel> {
    private static final Identifier GLOW = GazeSnatchers.id("textures/entity/gaze_snatcher_emissive.png");

    public GazeSnatcherEyesLayer(RenderLayerParent<LivingEntityRenderState, GazeSnatcherModel> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return RenderTypes.eyes(GLOW);
    }
}
