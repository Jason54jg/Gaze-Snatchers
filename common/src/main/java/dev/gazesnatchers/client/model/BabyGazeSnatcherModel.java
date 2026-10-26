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
 * Import Blockbench (baby_gaze_snatcher.bbmodel). La pose de repos vient du dépliage box-UV ;
 * idle et walk reprennent les pistes du fichier.
 */
public class BabyGazeSnatcherModel extends EntityModel<LivingEntityRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("baby_gaze_snatcher"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftFore;
    private final ModelPart rightFore;
    private final ModelPart leftHind;
    private final ModelPart rightHind;

    public BabyGazeSnatcherModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.body = root.getChild("Body");
        this.leftFore = root.getChild("Left_Foreleg");
        this.rightFore = root.getChild("Right_Foreleg");
        this.leftHind = root.getChild("Left_Hindleg");
        this.rightHind = root.getChild("Right_Hindleg");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parent = mesh.getRoot();
        PartDefinition head = parent.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, -1.0F));
        PartDefinition body = parent.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition leftFore = parent.addOrReplaceChild("Left_Foreleg", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 21.0F, -2.0F));
        PartDefinition rightFore = parent.addOrReplaceChild("Right_Foreleg", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 21.0F, -2.0F));
        PartDefinition leftHind = parent.addOrReplaceChild("Left_Hindleg", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 21.0F, 2.0F));
        PartDefinition rightHind = parent.addOrReplaceChild("Right_Hindleg", CubeListBuilder.create().texOffs(52, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 21.0F, 2.0F));
        return LayerDefinition.create(mesh, 64, 64);
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
        this.body.xRot = 0.08F;
        this.body.yRot = lookY * 0.2F;
        this.body.zRot = 0.0F;

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
