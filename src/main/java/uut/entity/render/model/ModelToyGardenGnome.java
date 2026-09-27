package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import uut.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;

public class ModelToyGardenGnome extends ModelBase
{
    ModelRenderer head;
    ModelRenderer head_r1;
    ModelRenderer head_r2;
    ModelRenderer mustache;
    ModelRenderer hat;
    ModelRenderer body;
    ModelRenderer leftarm;
    ModelRenderer rightarm;
    ModelRenderer leftleg;
    ModelRenderer rightleg;

    public ModelToyGardenGnome() {
		this.textureWidth = 128;
		this.textureHeight = 128;

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -7.0F, -4.0F, 8, 7, 8, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 0, 4, -1.0F, -4.0F, -5.0F, 2, 2, 2, 0.0F, false));

        this.head_r1 = new ModelRenderer(this);
        this.head_r1.setRotationPoint(4.0F, -2.0F, -1.0F);
        this.head.addChild(head_r1);
        this.setRotationAngle(head_r1, 0.1309F, 0.1745F, 0.0F);
        this.head_r1.cubeList.add(new ModelBox(head_r1, 0, 30, -0.5F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

        this.head_r2 = new ModelRenderer(this);
        this.head_r2.setRotationPoint(-4.0F, -2.0F, -1.0F);
        this.head.addChild(head_r2);
        this.setRotationAngle(head_r2, 0.1309F, -0.1745F, 0.0F);
        this.head_r2.cubeList.add(new ModelBox(head_r2, 0, 30, -0.5F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

        this.mustache = new ModelRenderer(this);
        this.mustache.setRotationPoint(0.0F, -3.0F, -4.0F);
        this.head.addChild(mustache);
        this.setRotationAngle(mustache, -0.0873F, 0.0F, 0.0F);
        this.mustache.cubeList.add(new ModelBox(mustache, 0, 46, -4.5F, 0.0F, 0.0F, 9, 10, 3, 0.0F, false));

        this.hat = new ModelRenderer(this);
        this.hat.setRotationPoint(0.0F, -5.0F, -4.0F);
        this.head.addChild(hat);
        this.setRotationAngle(hat, -0.0873F, 0.0F, 0.0F);
        this.hat.cubeList.add(new ModelBox(hat, 0, 30, -5.0F, -4.0F, -1.0F, 10, 4, 10, 0.0F, false));
        this.hat.cubeList.add(new ModelBox(hat, 40, 30, -4.0F, -8.0F, 0.0F, 8, 4, 8, 0.0F, false));
        this.hat.cubeList.add(new ModelBox(hat, 72, 30, -3.0F, -12.0F, 1.0F, 6, 4, 6, 0.0F, false));

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, 2.0F, -2.0F, 8, 10, 4, 0.0F, false));

        this.leftarm = new ModelRenderer(this);
        this.leftarm.setRotationPoint(5.0F, 6.0F, 0.0F);
        this.leftarm.cubeList.add(new ModelBox(leftarm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 10, 4, 0.0F, true));

        this.rightarm = new ModelRenderer(this);
        this.rightarm.setRotationPoint(-5.0F, 6.0F, 0.0F);
        this.rightarm.cubeList.add(new ModelBox(rightarm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 10, 4, 0.0F, false));

        this.leftleg = new ModelRenderer(this);
        this.leftleg.setRotationPoint(2.0F, 14.0F, 0.0F);
        this.leftleg.cubeList.add(new ModelBox(leftleg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F, true));

        this.rightleg = new ModelRenderer(this);
        this.rightleg.setRotationPoint(-2.0F, 14.0F, 0.0F);
        this.rightleg.cubeList.add(new ModelBox(rightleg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F, false));
    }

    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        head.render(f5);
        body.render(f5);
        leftarm.render(f5);
        rightarm.render(f5);
        leftleg.render(f5);
        rightleg.render(f5);
    }

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        this.leftleg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.rightleg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.leftleg.rotateAngleY = 0.0f;
        this.rightleg.rotateAngleY = 0.0f;
        this.leftleg.rotateAngleZ = 0.0f;
        this.rightleg.rotateAngleZ = 0.0f;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        EntityToyGardenGnome entity = (EntityToyGardenGnome) entitylivingbaseIn;
        int attackTimer = entity.getAttackTimer();
        float time = entity.ticksExisted + partialTickTime;

        float idleRightX = MathHelper.sin(time * 0.067F) * 0.05F;
        float idleRightZ = MathHelper.cos(time * 0.09F) * 0.05F + 0.05F;
        float idleLeftX = MathHelper.sin(time * 0.067F + 0.5F) * 0.05F;
        float idleLeftZ = MathHelper.cos(time * 0.09F + 0.5F) * 0.05F + 0.05F;

        this.leftarm.rotateAngleX = idleLeftX + (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
        this.leftarm.rotateAngleZ = -idleLeftZ;
        this.leftarm.rotateAngleY = 0.0F;

        if (attackTimer > 0) {
            float progress = attackTimer - partialTickTime;
            float t = progress / 6.0F;
            float swing = MathHelper.sin(t * (float)Math.PI);
            float side  = -MathHelper.sin(t * (float)Math.PI * 2.0F);

            this.rightarm.rotateAngleX = -0.5F - 1.4F * swing;
            this.rightarm.rotateAngleY = side * 0.9F;
            this.rightarm.rotateAngleZ = 0.0F;
        } else {
            this.rightarm.rotateAngleX = idleRightX + (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
            this.rightarm.rotateAngleZ = idleRightZ;
            this.rightarm.rotateAngleY = 0.0F;
        }
    }

    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }

    protected EnumHandSide getMainHand(final Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            final EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
            final EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return (entitylivingbase.swingingHand == EnumHand.MAIN_HAND) ? enumhandside : enumhandside.opposite();
        }
        return EnumHandSide.RIGHT;
    }

    public void postRenderArm(final float scale, final EnumHandSide side) {
        this.getArmForSide(side).postRender(scale);
    }

    protected ModelRenderer getArmForSide(final EnumHandSide side) {
        return (side == EnumHandSide.LEFT) ? this.leftarm : this.rightarm;
    }

    @SideOnly(Side.CLIENT)
    public enum ArmPose
    {
        EMPTY,
        ITEM,
        BLOCK,
        BOW_AND_ARROW;
    }
}
