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
 * Petit rampant — pieds à y=24, puis scale renderer (~0.4) pour hitbox 0.55.
 * Atlas 64×64 :
 * Head 8×8×8 @ (0,0) ; Body 6×5×8 @ (0,20) ; Legs 2×4×2 @ (32,0)/(32,8)
 */
public class BabyGazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("baby_gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftForeleg;
    private final ModelPart rightForeleg;
    private final ModelPart leftHindleg;
    private final ModelPart rightHindleg;

    public BabyGazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.body = root.getChild("Body");
        this.leftForeleg = root.getChild("LeftForeleg");
        this.rightForeleg = root.getChild("RightForeleg");
        this.leftHindleg = root.getChild("LeftHindleg");
        this.rightHindleg = root.getChild("RightHindleg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation none = new CubeDeformation(0.0F);

        // Corps bas (dessus des pattes ~ y=20), tête devant
        root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 5.0F, 8.0F, none),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        root.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, none),
                PartPose.offset(0.0F, 18.0F, -2.0F));

        root.addOrReplaceChild(
                "LeftForeleg",
                CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, none),
                PartPose.offset(2.5F, 20.0F, -2.5F));

        root.addOrReplaceChild(
                "RightForeleg",
                CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, none).mirror(false),
                PartPose.offset(-2.5F, 20.0F, -2.5F));

        root.addOrReplaceChild(
                "LeftHindleg",
                CubeListBuilder.create().texOffs(32, 8).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, none),
                PartPose.offset(2.5F, 20.0F, 2.5F));

        root.addOrReplaceChild(
                "RightHindleg",
                CubeListBuilder.create().texOffs(32, 8).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, none).mirror(false),
                PartPose.offset(-2.5F, 20.0F, 2.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float walk = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float fl = Mth.cos(walk * 0.6662F) * 1.4F * speed;
        float fr = Mth.cos(walk * 0.6662F + (float) Math.PI) * 1.4F * speed;

        head.yRot = state.yRot * ((float) Math.PI / 180F);
        head.xRot = state.xRot * ((float) Math.PI / 180F) * 0.5F;

        leftForeleg.xRot = fl;
        rightForeleg.xRot = fr;
        leftHindleg.xRot = fr;
        rightHindleg.xRot = fl;
    }

    public Identifier texture() {
        return GazeSnatchers.id("textures/entity/baby_gaze_snatcher.png");
    }
}
