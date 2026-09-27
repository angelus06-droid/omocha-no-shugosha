package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyElfArcher;

public class ModelToyElfArcher extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer skirt;
    private final ModelRenderer quiver;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_arm;
    private final ModelRenderer head;
    private final ModelRenderer head_r1;
    private final ModelRenderer head_r2;
    private final ModelRenderer hair;
    private final ModelRenderer hair_lower;
    private final ModelRenderer right_ear;
    private final ModelRenderer left_ear;
    private final ModelRenderer cape;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;

    public ModelToyElfArcher() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, -12.0F, -2.0F, 8, 12, 4, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 16, 32, -4.0F, -12.0F, -2.0F, 8, 12, 4, 0.1F, false));

        skirt = new ModelRenderer(this);
        skirt.setRotationPoint(0.0F, -3.0F, 0.0F);
        body.addChild(skirt);


        quiver = new ModelRenderer(this);
        quiver.setRotationPoint(1.0F, -6.0F, 2.0F);
        body.addChild(quiver);
        setRotationAngle(quiver, 0.0F, 0.0F, 0.48F);
        quiver.cubeList.add(new ModelBox(quiver, 0, 51, -3.0F, -5.0F, 0.0F, 4, 11, 3, 0.0F, false));

        left_arm = new ModelRenderer(this);
        left_arm.setRotationPoint(5.0F, -10.0F, 0.0F);
        body.addChild(left_arm);
        setRotationAngle(left_arm, 0.0F, 0.0F, -0.0436F);
        left_arm.cubeList.add(new ModelBox(left_arm, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
        left_arm.cubeList.add(new ModelBox(left_arm, 0, 32, -1.5F, -2.0F, -2.0F, 4, 6, 4, 0.1F, true));
        left_arm.cubeList.add(new ModelBox(left_arm, 0, 42, -1.0F, 8.0F, -2.0F, 3, 1, 4, 0.1F, true));

        right_arm = new ModelRenderer(this);
        right_arm.setRotationPoint(-5.0F, -10.0F, 0.0F);
        body.addChild(right_arm);
        setRotationAngle(right_arm, 0.0F, 0.0F, 0.0436F);
        right_arm.cubeList.add(new ModelBox(right_arm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -12.0F, 0.0F);
        body.addChild(head);
        head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

        head_r1 = new ModelRenderer(this);
        head_r1.setRotationPoint(4.0F, -4.0F, -2.0F);
        head.addChild(head_r1);
        setRotationAngle(head_r1, -0.1745F, 0.4363F, -0.2618F);
        head_r1.cubeList.add(new ModelBox(head_r1, 90, -3, 0.0F, 0.0F, -2.0F, 0, 8, 3, 0.0F, false));

        head_r2 = new ModelRenderer(this);
        head_r2.setRotationPoint(-4.0F, -4.0F, -2.0F);
        head.addChild(head_r2);
        setRotationAngle(head_r2, -0.1745F, -0.4363F, 0.2618F);
        head_r2.cubeList.add(new ModelBox(head_r2, 84, -3, 0.0F, 0.0F, -2.0F, 0, 8, 3, 0.0F, false));

        hair = new ModelRenderer(this);
        hair.setRotationPoint(0.0F, -3.0F, 4.0F);
        head.addChild(hair);
        setRotationAngle(hair, 0.1745F, 0.0F, 0.0F);
        hair.cubeList.add(new ModelBox(hair, 62, 24, -3.0F, -2.0F, -2.0F, 6, 9, 3, 0.0F, false));

        hair_lower = new ModelRenderer(this);
        hair_lower.setRotationPoint(0.0F, 7.0F, 0.0F);
        hair.addChild(hair_lower);
        hair_lower.cubeList.add(new ModelBox(hair_lower, 62, 36, -2.0F, 0.0F, -1.0F, 4, 4, 2, 0.0F, false));
        hair_lower.cubeList.add(new ModelBox(hair_lower, 65, 42, -1.0F, 4.0F, 1.0F, 2, 9, 0, 0.0F, false));

        right_ear = new ModelRenderer(this);
        right_ear.setRotationPoint(-4.0F, -1.0F, -1.0F);
        head.addChild(right_ear);
        setRotationAngle(right_ear, 0.3491F, -0.6109F, -0.1309F);
        right_ear.cubeList.add(new ModelBox(right_ear, 24, -5, 0.0F, -3.0F, 0.0F, 0, 3, 5, 0.0F, false));

        left_ear = new ModelRenderer(this);
        left_ear.setRotationPoint(4.0F, -1.0F, -1.0F);
        head.addChild(left_ear);
        setRotationAngle(left_ear, 0.3491F, 0.6109F, 0.1309F);
        left_ear.cubeList.add(new ModelBox(left_ear, 24, -2, 0.0F, -3.0F, 0.0F, 0, 3, 5, 0.0F, false));

        cape = new ModelRenderer(this);
        cape.setRotationPoint(0.0F, -12.0F, 2.0F);
        body.addChild(cape);
        setRotationAngle(cape, 0.1745F, 0.0F, 0.0F);
        cape.cubeList.add(new ModelBox(cape, 84, 19, -4.0F, 0.0F, 0.0F, 8, 15, 1, 0.0F, false));
        cape.cubeList.add(new ModelBox(cape, 85, 37, -3.0F, 15.0F, 0.0F, 6, 1, 1, 0.0F, false));

        left_leg = new ModelRenderer(this);
        left_leg.setRotationPoint(2.0F, 12.0F, 0.0F);
        left_leg.cubeList.add(new ModelBox(left_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

        right_leg = new ModelRenderer(this);
        right_leg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        right_leg.cubeList.add(new ModelBox(right_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        body.render(f5);
        left_leg.render(f5);
        right_leg.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.right_leg.rotateAngleY = 0.0F;
        this.left_leg.rotateAngleY = 0.0F;

        this.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;

        this.right_arm.rotateAngleY = 0.0F;
        this.left_arm.rotateAngleY = 0.0F;
        this.right_arm.rotateAngleZ = 0.0F;
        this.left_arm.rotateAngleZ = 0.0F;

        float baseHairAngleX = 0.1745F;

        float gravityCompensation = -this.head.rotateAngleX * 0.6F;

        float hairWalkSwing = MathHelper.cos(limbSwing * 0.6662F) * 0.15F * limbSwingAmount;

        float idleWave = MathHelper.sin(ageInTicks * 0.05F) * 0.03F;

        this.hair.rotateAngleX = baseHairAngleX + gravityCompensation + hairWalkSwing + idleWave;
        this.hair.rotateAngleZ = MathHelper.sin(ageInTicks * 0.05F) * 0.02F;

        float lowerWalkSwing = MathHelper.cos(limbSwing * 0.6662F - 0.5F) * 0.2F * limbSwingAmount;
        float lowerIdleWave = MathHelper.sin(ageInTicks * 0.08F - 0.5F) * 0.05F;

        this.hair_lower.rotateAngleX = (gravityCompensation * 0.5F) + lowerWalkSwing + lowerIdleWave;
        this.hair_lower.rotateAngleZ = MathHelper.cos(ageInTicks * 0.05F) * 0.03F;

        boolean isAiming = false;
        if (entityIn instanceof EntityToyElfArcher) {
            isAiming = ((EntityToyElfArcher) entityIn).isAiming();
        } else if (entityIn instanceof EntityLivingBase) {
            isAiming = ((EntityLivingBase) entityIn).isHandActive();
        }

        if (isAiming) {
            float yaw = this.head.rotateAngleY;
            float pitch = this.head.rotateAngleX;

            this.right_arm.rotateAngleY = -0.1F + yaw;
            this.right_arm.rotateAngleX = -1.5707964F + pitch;
            this.right_arm.rotateAngleZ = 0.0F;

            this.left_arm.rotateAngleY = 0.5F + yaw;
            this.left_arm.rotateAngleX = -1.5000000F + pitch;
            this.left_arm.rotateAngleZ = -0.1F;
        } else if (limbSwingAmount < 0.05F) {
            float idleTime = ageInTicks * 0.03F;
            this.right_arm.rotateAngleZ = MathHelper.cos(idleTime) * 0.02F + 0.02F;
            this.left_arm.rotateAngleZ = -(MathHelper.cos(idleTime) * 0.02F + 0.02F);
            this.right_arm.rotateAngleX += MathHelper.sin(idleTime * 0.8F) * 0.015F;
            this.left_arm.rotateAngleX -= MathHelper.sin(idleTime * 0.8F) * 0.015F;
        }
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    protected EnumHandSide getMainHand(final Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            final EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
            final EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return (entitylivingbase.swingingHand == EnumHand.MAIN_HAND) ? enumhandside : enumhandside.opposite();
        }
        return EnumHandSide.RIGHT;
    }

    public void postRenderArm(final float scale, final EnumHandSide side) {
        this.body.postRender(scale);
        this.getArmForSide(side).postRender(scale);
    }

    protected ModelRenderer getArmForSide(final EnumHandSide side) {
        return (side == EnumHandSide.LEFT) ? this.left_arm : this.right_arm;
    }
}