package dev.gazesnatchers.client.model;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class GazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart body;

    public GazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.body = root.getChild("Body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -22.0F, 0.0F));

        root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -22.0F, 0.0F));

        root.addOrReplaceChild(
                "LeftArm",
                CubeListBuilder.create().texOffs(56, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, -20.0F, 0.0F));

        root.addOrReplaceChild(
                "RightArm",
                CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-5.0F, -20.0F, 0.0F));

        root.addOrReplaceChild(
                "LeftLeg",
                CubeListBuilder.create().texOffs(0, 32).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -2.0F, 0.0F));

        root.addOrReplaceChild(
                "RightLeg",
                CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-2.0F, -2.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float yRot = state.yRot * ((float) Math.PI / 180F);
        float xRot = state.xRot * ((float) Math.PI / 180F);
        head.yRot = yRot;
        head.xRot = xRot;
        // Posture légèrement penchée (silhouette Blockbench).
        body.xRot = 0.18F + xRot * 0.15F;
        body.yRot = yRot * 0.25F;
    }

    public Identifier texture() {
        return GazeSnatchers.id("textures/entity/gaze_snatcher.png");
    }
}
