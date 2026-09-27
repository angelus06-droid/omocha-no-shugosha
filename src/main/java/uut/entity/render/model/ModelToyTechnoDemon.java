package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import uut.entity.EntityToyTechnoDemon;

public class ModelToyTechnoDemon extends ModelBase {
    public ModelRenderer all;
    public ModelRenderer wheel;
    public ModelRenderer wheel2;
    public ModelRenderer body;
    public ModelRenderer bodyupper;
    public ModelRenderer rightpower;
    public ModelRenderer rightpowerback;
    public ModelRenderer leftpower;
    public ModelRenderer leftpowerback;
    public ModelRenderer rightarm;
    public ModelRenderer rightcannon;
    public ModelRenderer leftarm;
    public ModelRenderer leftcannon;

    private float rightArmAttackPitch;
    private float leftArmAttackPitch;
    private boolean isAttacking;

    public ModelToyTechnoDemon() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.all = new ModelRenderer(this);
        this.all.setRotationPoint(0.0F, 24.0F, 0.0F);

        this.wheel = new ModelRenderer(this);
        this.wheel.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.all.addChild(wheel);
        this.wheel.cubeList.add(new ModelBox(wheel, 6, 34, -3.0F, -5.0F, -2.5F, 2, 5, 5, 0.0F, false));

        this.wheel2 = new ModelRenderer(this);
        this.wheel2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.all.addChild(wheel2);
        this.wheel2.cubeList.add(new ModelBox(wheel2, 6, 34, 1.0F, -5.0F, -2.5F, 2, 5, 5, 0.0F, false));

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.all.addChild(body);
        this.body.cubeList.add(new ModelBox(body, 0, 24, -2.0F, 0.0F, -3.0F, 4, 4, 6, 0.0F, false));

        this.bodyupper = new ModelRenderer(this);
        this.bodyupper.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(bodyupper);
        this.bodyupper.cubeList.add(new ModelBox(bodyupper, 8, 21, -1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F, false));
        this.bodyupper.cubeList.add(new ModelBox(bodyupper, 0, 5, -3.0F, -7.0F, -3.0F, 6, 5, 6, 0.0F, false));
        this.bodyupper.cubeList.add(new ModelBox(bodyupper, 0, 16, -2.0F, -2.0F, -2.0F, 4, 1, 4, 0.0F, false));
        this.bodyupper.cubeList.add(new ModelBox(bodyupper, 8, 0, -2.0F, -8.0F, -2.0F, 4, 1, 4, 0.0F, false));
        this.bodyupper.cubeList.add(new ModelBox(bodyupper, 18, 7, -1.5F, -6.0F, -4.0F, 3, 3, 1, 0.0F, false));

        this.rightpower = new ModelRenderer(this);
        this.rightpower.setRotationPoint(-2.0F, -6.0F, 2.0F);
        this.bodyupper.addChild(rightpower);
        this.setRotationAngle(rightpower, -0.6981F, -0.3491F, -0.3054F);
        this.rightpower.cubeList.add(new ModelBox(rightpower, 22, 20, -1.0F, -2.0F, -1.0F, 2, 3, 2, 0.0F, false));
        this.rightpower.cubeList.add(new ModelBox(rightpower, 32, 23, -0.5F, -3.0F, -0.5F, 1, 1, 1, 0.0F, false));

        this.rightpowerback = new ModelRenderer(this);
        this.rightpowerback.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.rightpower.addChild(rightpowerback);
        this.rightpowerback.cubeList.add(new ModelBox(rightpowerback, 22, 17, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));

        this.leftpower = new ModelRenderer(this);
        this.leftpower.setRotationPoint(2.0F, -6.0F, 2.0F);
        this.bodyupper.addChild(leftpower);
        this.setRotationAngle(leftpower, -0.6981F, 0.3491F, 0.3054F);
        this.leftpower.cubeList.add(new ModelBox(leftpower, 22, 20, -1.0F, -2.0F, -1.0F, 2, 3, 2, 0.0F, false));
        this.leftpower.cubeList.add(new ModelBox(leftpower, 32, 23, -0.5F, -3.0F, -0.5F, 1, 1, 1, 0.0F, false));

        this.leftpowerback = new ModelRenderer(this);
        this.leftpowerback.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.leftpower.addChild(leftpowerback);
        this.leftpowerback.cubeList.add(new ModelBox(leftpowerback, 22, 17, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));

        this.rightarm = new ModelRenderer(this);
        this.rightarm.setRotationPoint(-3.0F, -5.0F, 0.0F);
        this.bodyupper.addChild(rightarm);
        this.setRotationAngle(rightarm, 0.1745F, 0.0F, 0.2618F);
        this.rightarm.cubeList.add(new ModelBox(rightarm, 36, 0, -2.5F, -1.0F, -1.5F, 3, 4, 3, 0.0F, false));
        this.rightarm.cubeList.add(new ModelBox(rightarm, 38, 7, -2.0F, 3.0F, -1.0F, 2, 3, 2, 0.0F, false));

        this.rightcannon = new ModelRenderer(this);
        this.rightcannon.setRotationPoint(-1.0F, 5.0F, -1.0F);
        this.rightarm.addChild(rightcannon);
        this.setRotationAngle(rightcannon, -0.1745F, -0.0436F, -0.1745F);
        this.rightcannon.cubeList.add(new ModelBox(rightcannon, 35, 12, -1.5F, -1.0F, -4.0F, 3, 3, 5, 0.0F, false));

        this.leftarm = new ModelRenderer(this);
        this.leftarm.setRotationPoint(3.0F, -5.0F, 0.0F);
        this.bodyupper.addChild(leftarm);
        this.setRotationAngle(leftarm, 0.1745F, 0.0F, -0.2618F);
        this.leftarm.cubeList.add(new ModelBox(leftarm, 36, 0, -0.5F, -1.0F, -1.5F, 3, 4, 3, 0.0F, true));
        this.leftarm.cubeList.add(new ModelBox(leftarm, 38, 7, 0.0F, 3.0F, -1.0F, 2, 3, 2, 0.0F, true));

        this.leftcannon = new ModelRenderer(this);
        this.leftcannon.setRotationPoint(1.0F, 5.0F, -1.0F);
        this.leftarm.addChild(leftcannon);
        this.setRotationAngle(leftcannon, -0.1745F, 0.0436F, 0.2182F);
        this.leftcannon.cubeList.add(new ModelBox(leftcannon, 35, 12, -1.5F, -1.0F, -4.0F, 3, 3, 5, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        all.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.bodyupper.rotateAngleX = 0.0F;
        this.rightarm.rotateAngleX = 0.1745F;
        this.rightarm.rotateAngleZ = 0.2618F;
        this.leftarm.rotateAngleX = 0.1745F;
        this.leftarm.rotateAngleZ = -0.2618F;
        this.rightpower.rotateAngleX = -0.6981F;
        this.leftpower.rotateAngleX = -0.6981F;

        this.rightpowerback.rotationPointY = -3.0F;
        this.leftpowerback.rotationPointY = -3.0F;

        this.bodyupper.rotateAngleX = limbSwingAmount * 0.2F;

        float idleOven = MathHelper.cos(ageInTicks * 0.05F) * 0.05F;
        this.rightarm.rotateAngleX += idleOven;
        this.leftarm.rotateAngleX += idleOven;
        this.rightarm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.05F) * 0.02F;
        this.leftarm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.05F) * 0.02F;

        float powerSpeed = 0.15F;
        float powerIntensity = 0.1F;
        this.rightpower.rotateAngleX += MathHelper.sin(ageInTicks * powerSpeed) * powerIntensity;
        this.leftpower.rotateAngleX += MathHelper.cos(ageInTicks * powerSpeed) * powerIntensity;

        float pistonSpeed = 0.25F;
        float pistonRange = 0.4F;

        this.rightpowerback.rotationPointY += MathHelper.sin(ageInTicks * pistonSpeed) * pistonRange;
        this.leftpowerback.rotationPointY += MathHelper.cos(ageInTicks * pistonSpeed) * pistonRange;
        // --------------------------------------------------------------

        if (this.isAttacking) {
            this.rightarm.rotateAngleX = this.rightArmAttackPitch;
            this.leftarm.rotateAngleX = this.leftArmAttackPitch;
        }
    }

    @Override
    public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
        final EntityToyTechnoDemon entityirongolem = (EntityToyTechnoDemon) entitylivingbaseIn;
        final int i = entityirongolem.getAttackTimer();

        if (i > 0) {
            this.isAttacking = true;
            this.rightArmAttackPitch = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
            this.leftArmAttackPitch = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
        } else {
            this.isAttacking = false;
        }
    }

    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}