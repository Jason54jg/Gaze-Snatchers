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
 * Reproduction visuelle des refs Blockbench fournies (pas de .bbmodel source) :
 * grand, squelettique, bras très longs, mains 4 doigts, pieds 3 orteils, yeux blancs.
 * Pieds à y=24. Atlas 128×128.
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

        // --- Tête cube (yeux peints en texture) ---
        root.addOrReplaceChild(
                "Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, none),
                PartPose.offset(0.0F, -20.0F, 1.0F));

        // --- Torse squelette : colonne + côtes espacées ---
        PartDefinition body = root.addOrReplaceChild(
                "Body",
                CubeListBuilder.create()
                        // colonne vertébrale
                        .texOffs(40, 0).addBox(-1.0F, -22.0F, -1.0F, 2.0F, 22.0F, 2.0F, none)
                        // bassin
                        .texOffs(48, 0).addBox(-3.0F, -2.0F, -1.5F, 6.0F, 3.0F, 3.0F, none),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.18F, 0.0F, 0.0F));

        // côtes (gaps entre chaque rangée = look hollow / bony)
        float[] ribY = {-18.0F, -15.0F, -12.0F, -9.0F, -6.0F};
        for (int i = 0; i < ribY.length; i++) {
            float y = ribY[i];
            float half = 3.5F - i * 0.15F;
            body.addOrReplaceChild(
                    "Rib" + i,
                    CubeListBuilder.create().texOffs(48, 8)
                            .addBox(-half, 0.0F, -2.0F, half * 2.0F, 1.5F, 1.0F, none)
                            .addBox(-half, 0.0F, 1.0F, half * 2.0F, 1.5F, 1.0F, none),
                    PartPose.offset(0.0F, y, 0.0F));
        }

        // --- Bras filiformes (1 ligne de blocs) ---
        PartDefinition leftArm = root.addOrReplaceChild(
                "LeftArm",
                CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 28.0F, 2.0F, none),
                PartPose.offset(4.0F, -18.0F, 0.5F));
        addClaw(leftArm, false);

        PartDefinition rightArm = root.addOrReplaceChild(
                "RightArm",
                CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 28.0F, 2.0F, none).mirror(false),
                PartPose.offset(-4.0F, -18.0F, 0.5F));
        addClaw(rightArm, true);

        // --- Jambes fines ---
        PartDefinition leftLeg = root.addOrReplaceChild(
                "LeftLeg",
                CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 22.0F, 2.0F, none),
                PartPose.offset(2.0F, 2.0F, 0.0F));
        addToes(leftLeg, false);

        PartDefinition rightLeg = root.addOrReplaceChild(
                "RightLeg",
                CubeListBuilder.create().texOffs(72, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 22.0F, 2.0F, none).mirror(false),
                PartPose.offset(-2.0F, 2.0F, 0.0F));
        addToes(rightLeg, true);

        return LayerDefinition.create(mesh, 128, 128);
    }

    /** Main griffe : paume + 4 doigts longs incurvés. */
    private static void addClaw(PartDefinition arm, boolean mirror) {
        CubeListBuilder palmBuilder = CubeListBuilder.create().texOffs(80, 0);
        if (mirror) {
            palmBuilder.mirror();
        }
        PartDefinition hand = arm.addOrReplaceChild(
                "Hand",
                palmBuilder.addBox(-2.5F, 0.0F, -1.5F, 5.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 28.0F, 0.0F));

        for (int i = 0; i < 4; i++) {
            float x = -1.8F + i * 1.2F;
            CubeListBuilder finger = CubeListBuilder.create().texOffs(80, 6);
            if (mirror) {
                finger.mirror();
            }
            hand.addOrReplaceChild(
                    "Finger" + i,
                    finger.addBox(-0.45F, 0.0F, -0.45F, 0.9F, 5.0F, 0.9F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(x, 1.5F, 0.2F, 0.35F, 0.0F, (i - 1.5F) * 0.12F));
        }
    }

    private static void addToes(PartDefinition leg, boolean mirror) {
        CubeListBuilder sole = CubeListBuilder.create().texOffs(80, 14);
        if (mirror) {
            sole.mirror();
        }
        PartDefinition foot = leg.addOrReplaceChild(
                "Foot",
                sole.addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 22.0F, 0.0F));
        for (int i = 0; i < 3; i++) {
            float x = -1.0F + i;
            CubeListBuilder toe = CubeListBuilder.create().texOffs(80, 20);
            if (mirror) {
                toe.mirror();
            }
            foot.addOrReplaceChild(
                    "Toe" + i,
                    toe.addBox(-0.4F, 0.0F, -2.2F, 0.8F, 1.0F, 2.2F, new CubeDeformation(0.0F)),
                    PartPose.offset(x, 1.0F, -2.5F));
        }
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        float walk = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float limb = Mth.cos(walk * 0.6662F) * 1.1F * speed;

        head.yRot = state.yRot * ((float) Math.PI / 180F);
        head.xRot = state.xRot * ((float) Math.PI / 180F) + 0.08F;

        body.xRot = 0.18F;

        leftArm.xRot = -0.2F - limb * 0.4F;
        rightArm.xRot = -0.2F + limb * 0.4F;
        leftArm.zRot = -0.06F;
        rightArm.zRot = 0.06F;

        leftLeg.xRot = limb;
        rightLeg.xRot = -limb;
    }

    public Identifier texture() {
        return GazeSnatchers.id("textures/entity/gaze_snatcher.png");
    }
}
