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
 * Convention humanoid : pieds à y=24 (LivingEntityRenderer translate -1.501).
 * Atlas 64×64, UV non chevauchés.
 */
public class GazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public GazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.body = root.getChild("Body");
        this.leftArm = root.getChild("LeftArm");
        this.rightArm = root.getChild("RightArm");
        this.leftLeg = root.getChild("LeftLeg");
        this.rightLeg = root.getChild("RightLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation none = new CubeDeformation(0.0F);

        // Cou ~ y=8 ; tête au-dessus (Y-), pieds à y=24
        root.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, none),
                PartPose.offset(0.0F, -8.0F, 0.0F));

        root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.5F, -16.0F, -1.5F, 5.0F, 16.0F, 3.0F, none),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        root.addOrReplaceChild(
                "LeftArm",
                CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, none),
                PartPose.offset(3.5F, -6.0F, 0.0F));

        root.addOrReplaceChild(
                "RightArm",
                CubeListBuilder.create().texOffs(40, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F, none).mirror(false),
                PartPose.offset(-3.5F, -6.0F, 0.0F));

        root.addOrReplaceChild(
                "LeftLeg",
                CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F, none),
                PartPose.offset(1.5F, 8.0F, 0.0F));

        root.addOrReplaceChild(
                "RightLeg",
                CubeListBuilder.create().texOffs(40, 24).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F, none).mirror(false),
                PartPose.offset(-1.5F, 8.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float walk = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;
        float limb = Mth.cos(walk * 0.6662F) * 1.2F * walkSpeed;

        head.yRot = state.yRot * ((float) Math.PI / 180F);
        head.xRot = state.xRot * ((float) Math.PI / 180F);

        body.xRot = 0.06F;

        leftArm.xRot = -limb * 0.55F;
        rightArm.xRot = limb * 0.55F;
        leftArm.zRot = -0.05F;
        rightArm.zRot = 0.05F;

        leftLeg.xRot = limb;
        rightLeg.xRot = -limb;
    }

    public Identifier texture() {
        return GazeSnatchers.id("textures/entity/gaze_snatcher.png");
    }
}
