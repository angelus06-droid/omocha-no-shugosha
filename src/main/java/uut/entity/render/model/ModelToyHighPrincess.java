package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyHighPrincess; // Asegúrate de que la ruta a tu entidad sea correcta

public class ModelToyHighPrincess extends ModelBase
{
    private final ModelRenderer body;
    private final ModelRenderer skirt;
    private final ModelRenderer skirt_r1;
    private final ModelRenderer skirt_r2;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_arm;
    private final ModelRenderer head;
    private final ModelRenderer hair;
    private final ModelRenderer crown;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;

    public ModelToyHighPrincess() {
        textureWidth = 128;
        textureHeight = 128;

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, -12.0F, -2.0F, 8, 12, 4, 0.0F, false));

        this.skirt = new ModelRenderer(this);
        this.skirt.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.body.addChild(skirt);

        this.skirt_r1 = new ModelRenderer(this);
        this.skirt_r1.setRotationPoint(4.0F, -1.0F, 0.0F);
        this.skirt.addChild(skirt_r1);
        this.setRotationAngle(skirt_r1, 0.0F, 0.0F, -0.1745F);
        this.skirt_r1.cubeList.add(new ModelBox(skirt_r1, 16, 32, -4.0F, 0.0F, -2.0F, 4, 8, 4, 0.21F, false));

        this.skirt_r2 = new ModelRenderer(this);
        this.skirt_r2.setRotationPoint(-4.0F, -1.0F, 0.0F);
        this.skirt.addChild(skirt_r2);
        this.setRotationAngle(skirt_r2, 0.0F, 0.0F, 0.1745F);
        this.skirt_r2.cubeList.add(new ModelBox(skirt_r2, 0, 32, 0.0F, 0.0F, -2.0F, 4, 8, 4, 0.2F, false));

        this.left_arm = new ModelRenderer(this);
        this.left_arm.setRotationPoint(5.0F, -10.0F, 0.0F);
        this.body.addChild(left_arm);
        this.setRotationAngle(left_arm, 0.0F, 0.0F, -0.0436F);
        this.left_arm.cubeList.add(new ModelBox(left_arm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));

        this.right_arm = new ModelRenderer(this);
        this.right_arm.setRotationPoint(-5.0F, -10.0F, 0.0F);
        this.body.addChild(right_arm);
        this.setRotationAngle(right_arm, 0.0F, 0.0F, 0.0436F);
        this.right_arm.cubeList.add(new ModelBox(right_arm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, -12.0F, 0.0F);
        this.body.addChild(head);
        this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

        this.hair = new ModelRenderer(this);
        this.hair.setRotationPoint(0.0F, -5.0F, 4.0F);
        this.head.addChild(hair);
        this.setRotationAngle(hair, 0.0873F, 0.0F, 0.0F);
        this.hair.cubeList.add(new ModelBox(hair, 88, 16, -5.0F, 0.0F, -3.0F, 10, 14, 4, 0.0F, false));

        this.crown = new ModelRenderer(this);
        this.crown.setRotationPoint(0.0F, -8.0F, -3.0F);
        this.head.addChild(crown);
        this.setRotationAngle(crown, -0.1745F, 0.0F, 0.0F);
        this.crown.cubeList.add(new ModelBox(crown, 64, 0, -2.5F, -4.0F, 0.0F, 5, 4, 5, 0.0F, false));

        this.left_leg = new ModelRenderer(this);
        this.left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.left_leg.cubeList.add(new ModelBox(left_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

        this.right_leg = new ModelRenderer(this);
        this.right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.right_leg.cubeList.add(new ModelBox(right_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        body.render(f5);
        left_leg.render(f5);
        right_leg.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        float baseHairAngle = 0.0873F;

        float headPitchRad = headPitch * 0.017453292F;
        float gravityCompensation = -headPitchRad * 0.8F;

        float idleWave = MathHelper.cos(ageInTicks * 0.09F) * 0.05F;

        float walkWave = MathHelper.abs(MathHelper.cos(limbSwing * 0.6662F)) * 0.35F * limbSwingAmount;

        this.hair.rotateAngleX = baseHairAngle + gravityCompensation + idleWave + walkWave;
        this.hair.rotateAngleZ = MathHelper.cos(limbSwing * 0.3331F) * 0.1F * limbSwingAmount;

        this.skirt.rotateAngleX = 0.0F;
        this.skirt.rotateAngleY = 0.0F;
        this.skirt.rotateAngleZ = 0.0F;

        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        if (entityIn instanceof EntityToyHighPrincess) {
            EntityToyHighPrincess princess = (EntityToyHighPrincess) entityIn;

            if (princess.isSpellcasting()) {
                float wave = MathHelper.sin(ageInTicks * 0.4F);
                float angleX = (-180.0F + (wave * 20.0F)) * 0.017453292F;

                this.left_arm.rotateAngleX = angleX;
                this.left_arm.rotateAngleY = 0.0F;
                this.left_arm.rotateAngleZ = 30.0F * 0.017453292F;

                this.right_arm.rotateAngleX = angleX;
                this.right_arm.rotateAngleY = 0.0F;
                this.right_arm.rotateAngleZ = -30.0F * 0.017453292F;


            } else if (princess.isSitting()) {
                this.left_arm.rotateAngleX = -25.0F * 0.017453292F;
                this.left_arm.rotateAngleY = 10.0F * 0.017453292F;
                this.left_arm.rotateAngleZ = 25.0F * 0.017453292F;

                this.right_arm.rotateAngleX = -25.0F * 0.017453292F;
                this.right_arm.rotateAngleY = -10.0F * 0.017453292F;
                this.right_arm.rotateAngleZ = -25.0F * 0.017453292F;

            } else {
                this.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
                this.left_arm.rotateAngleY = 0.0F;
                this.left_arm.rotateAngleZ = -0.0436F;

                this.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
                this.right_arm.rotateAngleY = 0.0F;
                this.right_arm.rotateAngleZ = 0.0436F;
            }
        }
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}