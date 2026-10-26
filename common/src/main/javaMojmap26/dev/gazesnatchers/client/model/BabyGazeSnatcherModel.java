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
import net.minecraft.util.Mth;

/**
 * Reproduction du bébé Blockbench (screenshot) :
 * grosse tête cube, corps trapu pierre, 4 pattes écartées, yeux violets.
 * Hiérarchie identique : Head, Body, Left_Foreleg, Right_Foreleg, Left_Hindleg, Right_Hindleg.
 */
public class BabyGazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("baby_gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart leftForeleg;
    private final ModelPart rightForeleg;
    private final ModelPart leftHindleg;
    private final ModelPart rightHindleg;

    public BabyGazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.leftForeleg = root.getChild("Left_Foreleg");
        this.rightForeleg = root.getChild("Right_Foreleg");
        this.leftHindleg = root.getChild("Left_Hindleg");
        this.rightHindleg = root.getChild("Right_Hindleg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation none = new CubeDeformation(0.0F);

        // Tête dominante (presque aussi large que le corps)
        root.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -10.0F, -10.0F, 12.0F, 12.0F, 12.0F, none),
                PartPose.offset(0.0F, 12.0F, -2.0F));

        // Corps bas / pierre
        root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(0, 24).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 7.0F, 12.0F, none),
                PartPose.offset(0.0F, 17.0F, 2.0F));

        // Pattes courtes, écartées (pose « frog ») — pieds à y=24
        float spread = 0.35F;
        root.addOrReplaceChild(
                "Left_Foreleg",
                CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, none),
                PartPose.offsetAndRotation(4.5F, 17.0F, -3.5F, 0.0F, 0.0F, -spread));
        root.addOrReplaceChild(
                "Right_Foreleg",
                CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, none).mirror(false),
                PartPose.offsetAndRotation(-4.5F, 17.0F, -3.5F, 0.0F, 0.0F, spread));
        root.addOrReplaceChild(
                "Left_Hindleg",
                CubeListBuilder.create().texOffs(48, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, none),
                PartPose.offsetAndRotation(4.5F, 17.0F, 5.0F, 0.0F, 0.0F, -spread));
        root.addOrReplaceChild(
                "Right_Hindleg",
                CubeListBuilder.create().texOffs(48, 12).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, none).mirror(false),
                PartPose.offsetAndRotation(-4.5F, 17.0F, 5.0F, 0.0F, 0.0F, spread));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float walk = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float fl = Mth.cos(walk * 0.6662F) * 1.2F * speed;
        float fr = Mth.cos(walk * 0.6662F + (float) Math.PI) * 1.2F * speed;

        head.yRot = state.yRot * ((float) Math.PI / 180F);
        head.xRot = state.xRot * ((float) Math.PI / 180F) * 0.35F;

        leftForeleg.xRot = fl;
        rightForeleg.xRot = fr;
        leftHindleg.xRot = fr;
        rightHindleg.xRot = fl;
    }

    public Identifier texture() {
        return GazeSnatchers.id("textures/entity/baby_gaze_snatcher.png");
    }
}
