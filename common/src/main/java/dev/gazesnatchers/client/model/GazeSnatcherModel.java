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
import net.minecraft.util.Mth;

/**
 * Import Blockbench (gaze_snatcher.bbmodel). La pose de repos vient du dépliage box-UV ;
 * idle et walk reprennent les pistes du fichier.
 */
public class GazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public GazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.jaw = this.head.getChild("Jaw");
        this.body = root.getChild("Body");
        this.leftArm = root.getChild("LeftArm");
        this.rightArm = root.getChild("RightArm");
        this.leftLeg = root.getChild("LeftLeg");
        this.rightLeg = root.getChild("RightLeg");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parent = mesh.getRoot();
        PartDefinition head = parent.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));
        PartDefinition body = parent.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 28).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));
        PartDefinition leftArm = parent.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 28).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -8.0F, 0.0F));
        PartDefinition rightArm = parent.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 28).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -8.0F, 0.0F));
        PartDefinition leftLeg = parent.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(56, 28).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = parent.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(72, 28).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 2.0F, 0.0F));
        head.addOrReplaceChild("Jaw", CubeListBuilder.create().texOffs(0, 64).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -6.0F));
        leftArm.addOrReplaceChild("Claw", CubeListBuilder.create().texOffs(88, 28).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.0F, -1.0F));
        rightArm.addOrReplaceChild("Claw", CubeListBuilder.create().texOffs(104, 28).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.0F, -1.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float lookX = state.xRot * Mth.DEG_TO_RAD;
        float lookY = state.yRot * Mth.DEG_TO_RAD;
        float idle = state.ageInTicks;
        float limb = state.walkAnimationPos;
        float swing = state.walkAnimationSpeed;

        this.head.xRot = lookX * 0.65F + Mth.sin(idle * 0.06F) * 0.04F;
        this.head.yRot = lookY;
        this.head.zRot = Mth.sin(idle * 0.04F) * 0.03F;
        this.jaw.xRot = 0.12F + Mth.sin(idle * 0.11F) * 0.07F;
        this.jaw.yRot = 0.0F;
        this.jaw.zRot = 0.0F;

        this.body.xRot = 0.22F + Mth.sin(idle * 0.07F) * 0.04F;
        this.body.yRot = lookY * 0.12F;
        this.body.zRot = 0.0F;

        float arm = Mth.cos(limb * 0.6662F) * 1.05F * swing;
        this.leftArm.xRot = 0.16F + arm;
        this.rightArm.xRot = 0.16F - arm;
        this.leftArm.yRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.leftArm.zRot = 0.06F;
        this.rightArm.zRot = -0.06F;

        float leg = Mth.cos(limb * 0.6662F) * 0.85F * swing;
        this.leftLeg.xRot = -leg;
        this.rightLeg.xRot = leg;
        this.leftLeg.yRot = 0.0F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.zRot = 0.0F;
        this.rightLeg.zRot = 0.0F;

    }
}
