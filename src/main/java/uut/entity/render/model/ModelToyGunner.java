package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.*;
import uut.entity.EntityToyGunner;

public class ModelToyGunner extends ModelBase {
    private final ModelRenderer head;
    private final ModelRenderer hat;
    private final ModelRenderer body;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public ModelToyGunner() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 58, -1.0F, -4.0F, -6.0F, 2, 2, 3, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 10, 61, -3.5F, -2.5F, -4.05F, 7, 2, 0, 0.0F, false));

        hat = new ModelRenderer(this);
        hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(hat);
        hat.cubeList.add(new ModelBox(hat, 32, 0, -4.0F, -14.0F, -4.0F, 8, 8, 8, 0.2F, false));
        hat.cubeList.add(new ModelBox(hat, 0, 0, -1.0F, -16.0F, -4.0F, 2, 2, 2, 0.0F, false));

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.cubeList.add(new ModelBox(body, 19, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 17, 32, -4.0F, 10.0F, -2.5F, 8, 6, 5, 0.1F, false));

        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        rightArm.cubeList.add(new ModelBox(rightArm, 45, 24, -2.5F, 0.0F, -1.5F, 3, 9, 3, 0.0F, false));
        rightArm.cubeList.add(new ModelBox(rightArm, 44, 36, -3.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, false));
        rightArm.cubeList.add(new ModelBox(rightArm, 43, 16, -3.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        leftArm.cubeList.add(new ModelBox(leftArm, 43, 16, -1.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, true));
        leftArm.cubeList.add(new ModelBox(leftArm, 45, 24, -0.5F, 0.0F, -1.5F, 3, 9, 3, 0.0F, true));
        leftArm.cubeList.add(new ModelBox(leftArm, 44, 36, -1.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, true));

        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        rightLeg.cubeList.add(new ModelBox(rightLeg, 0, 16, -1.5F, 0.0F, -1.5F, 3, 10, 3, 0.0F, false));
        rightLeg.cubeList.add(new ModelBox(rightLeg, 0, 29, -2.0F, 10.0F, -3.0F, 4, 2, 5, 0.0F, false));

        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        leftLeg.cubeList.add(new ModelBox(leftLeg, 0, 16, -1.5F, 0.0F, -1.5F, 3, 10, 3, 0.0F, true));
        leftLeg.cubeList.add(new ModelBox(leftLeg, 0, 29, -2.0F, 10.0F, -3.0F, 4, 2, 5, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.head.render(f5);
        this.body.render(f5);
        this.rightArm.render(f5);
        this.leftArm.render(f5);
        this.rightLeg.render(f5);
        this.leftLeg.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;

        this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.rightArm.rotateAngleY = 0.0F;
        this.leftArm.rotateAngleY = 0.0F;
        this.rightArm.rotateAngleZ = 0.0F;
        this.leftArm.rotateAngleZ = 0.0F;

        boolean isAiming = false;
        if (entityIn instanceof EntityToyGunner) {
            isAiming = ((EntityToyGunner) entityIn).isAiming();
        } else if (entityIn instanceof EntityLivingBase) {
            isAiming = ((EntityLivingBase) entityIn).isHandActive();
        }

        if (isAiming) {
            float yaw = this.head.rotateAngleY;
            float pitch = this.head.rotateAngleX;

            this.rightArm.rotateAngleY = -0.1F + yaw;
            this.rightArm.rotateAngleX = -1.5707964F + pitch;
            this.rightArm.rotateAngleZ = 0.0F;

            this.leftArm.rotateAngleY = 0.5F + yaw;
            this.leftArm.rotateAngleX = -1.5000000F + pitch;
            this.leftArm.rotateAngleZ = -0.1F;
        } else {
            this.rightArm.rotateAngleZ += (MathHelper.cos(ageInTicks * 0.09F) * 0.05F) + 0.05F;
            this.leftArm.rotateAngleZ -= (MathHelper.cos(ageInTicks * 0.09F) * 0.05F) + 0.05F;
            this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
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
        this.getArmForSide(side).postRender(scale);
    }

    protected ModelRenderer getArmForSide(final EnumHandSide side) {
        return (side == EnumHandSide.LEFT) ? this.leftArm : this.rightArm;
    }
}