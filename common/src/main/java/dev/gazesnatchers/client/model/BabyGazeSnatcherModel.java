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
 * Import Blockbench (baby_gaze_snatcher.bbmodel). texScale 0.25 : 4 texels par pixel modèle.
 * idle et walk reprennent les pistes du fichier.
 */
public class BabyGazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("baby_gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftFore;
    private final ModelPart rightFore;
    private final ModelPart leftHind;
    private final ModelPart rightHind;

    public BabyGazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.jaw = this.head.getChild("Jaw");
        this.body = root.getChild("Body");
        this.tail = this.body.getChild("Tail");
        this.tailTip = this.tail.getChild("TailTip");
        this.leftFore = root.getChild("Left_Foreleg");
        this.rightFore = root.getChild("Right_Foreleg");
        this.leftHind = root.getChild("Left_Hindleg");
        this.rightHind = root.getChild("Right_Hindleg");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parent = mesh.getRoot();
        PartDefinition head = parent.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 19.0F, -1.0F));
        PartDefinition body = parent.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition leftFore = parent.addOrReplaceChild("Left_Foreleg", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(2.0F, 21.0F, -2.0F));
        PartDefinition rightFore = parent.addOrReplaceChild("Right_Foreleg", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(-2.0F, 21.0F, -2.0F));
        PartDefinition leftHind = parent.addOrReplaceChild("Left_Hindleg", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(2.0F, 21.0F, 2.0F));
        PartDefinition rightHind = parent.addOrReplaceChild("Right_Hindleg", CubeListBuilder.create().texOffs(52, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(-2.0F, 21.0F, 2.0F));
        PartDefinition jaw = head.addOrReplaceChild("Jaw", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, -1.0F, -4.0F));
        PartDefinition toothL = jaw.addOrReplaceChild("ToothL", CubeListBuilder.create().texOffs(16, 40).addBox(-2.0F, 2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition toothR = jaw.addOrReplaceChild("ToothR", CubeListBuilder.create().texOffs(22, 40).addBox(1.0F, 2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition eyeL = head.addOrReplaceChild("EyeL", CubeListBuilder.create().texOffs(40, 18).addBox(0.0F, -4.0F, -7.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition eyeR = head.addOrReplaceChild("EyeR", CubeListBuilder.create().texOffs(52, 18).addBox(-2.0F, -4.0F, -7.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hornL = head.addOrReplaceChild("HornL", CubeListBuilder.create().texOffs(32, 28).addBox(1.0F, -9.0F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition hornR = head.addOrReplaceChild("HornR", CubeListBuilder.create().texOffs(38, 28).addBox(-2.0F, -9.0F, -2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition tail = body.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 2.0F, 3.0F));
        PartDefinition tailTip = tail.addOrReplaceChild("TailTip", CubeListBuilder.create().texOffs(18, 28).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, -1.0F, 6.0F));
        PartDefinition nubL = body.addOrReplaceChild("NubL", CubeListBuilder.create().texOffs(44, 28).addBox(-2.0F, -1.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition nubR = body.addOrReplaceChild("NubR", CubeListBuilder.create().texOffs(50, 28).addBox(1.0F, -1.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F), 0.25F, 0.25F), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float lookX = state.xRot * Mth.DEG_TO_RAD;
        float lookY = state.yRot * Mth.DEG_TO_RAD;
        float idle = state.ageInTicks;
        float limb = state.walkAnimationPos;
        float swing = state.walkAnimationSpeed;

        this.head.xRot = lookX * 0.8F + Mth.sin(idle * 0.2F) * 0.08F;
        this.head.yRot = lookY;
        this.head.zRot = 0.0F;
        this.jaw.xRot = 0.18F + Mth.sin(idle * 0.23F) * 0.1F;
        this.jaw.yRot = 0.0F;
        this.jaw.zRot = 0.0F;
        this.body.xRot = 0.08F;
        this.body.yRot = lookY * 0.2F;
        this.body.zRot = 0.0F;
        this.tail.xRot = -0.35F + Mth.sin(idle * 0.18F) * 0.12F;
        this.tail.yRot = Mth.sin(idle * 0.15F) * 0.35F;
        this.tail.zRot = 0.0F;
        this.tailTip.xRot = -0.45F;
        this.tailTip.yRot = Mth.sin(idle * 0.15F + 0.6F) * 0.25F;
        this.tailTip.zRot = 0.0F;

        float fore = Mth.cos(limb * 0.9F) * 1.3F * swing;
        float hind = Mth.cos(limb * 0.9F + (float) Math.PI) * 1.3F * swing;
        this.leftFore.xRot = fore;
        this.rightFore.xRot = -fore;
        this.leftHind.xRot = hind;
        this.rightHind.xRot = -hind;
        this.leftFore.yRot = this.rightFore.yRot = this.leftHind.yRot = this.rightHind.yRot = 0.0F;
        this.leftFore.zRot = this.rightFore.zRot = this.leftHind.zRot = this.rightHind.zRot = 0.0F;

    }
}
