package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTeddyBear;
import uut.entity.EntityToyTeddyQilin;

public class ModelToyTeddyQilin extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer head_r1;
	public ModelRenderer head_r2;
	public ModelRenderer head_r3;
	public ModelRenderer head_r4;
	public ModelRenderer horn2;
	public ModelRenderer hornextra;
	public ModelRenderer horn;
	public ModelRenderer hornextra2;
	public ModelRenderer eyeright;
	public ModelRenderer eyeright_r1;
	public ModelRenderer eyeright_r2;
	public ModelRenderer eyeleft;
	public ModelRenderer eyeleft_r1;
	public ModelRenderer eyeleft_r2;
	public ModelRenderer ponytail;
	public ModelRenderer earright;
	public ModelRenderer earleft;
	public ModelRenderer body;
	public ModelRenderer collar;
	public ModelRenderer leftarm;
	public ModelRenderer rightarm;
	public ModelRenderer leftleg;
	public ModelRenderer rightleg;

	public ModelToyTeddyQilin() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 0, 45, -2.0F, -4.0F, -5.0F, 4, 3, 1, 0.0F, false));

		this.head_r1 = new ModelRenderer(this);
		this.head_r1.setRotationPoint(3.0F, -5.5F, -2.5F);
		this.head.addChild(head_r1);
		this.setRotationAngle(head_r1, -0.2618F, -0.7854F, -0.0873F);
		this.head_r1.cubeList.add(new ModelBox(head_r1, 58, 57, 0.0F, -0.5F, 0.0F, 3, 7, 0, 0.0F, false));

		this.head_r2 = new ModelRenderer(this);
		this.head_r2.setRotationPoint(3.0F, -4.5F, 1.5F);
		this.head.addChild(head_r2);
		this.setRotationAngle(head_r2, 0.2618F, -0.3927F, -0.0873F);
		this.head_r2.cubeList.add(new ModelBox(head_r2, 58, 57, 0.0F, -0.5F, 0.0F, 3, 7, 0, 0.0F, false));

		this.head_r3 = new ModelRenderer(this);
		this.head_r3.setRotationPoint(-3.0F, -4.5F, 1.5F);
		this.head.addChild(head_r3);
		this.setRotationAngle(head_r3, 0.2618F, 0.3927F, 0.0873F);
		this.head_r3.cubeList.add(new ModelBox(head_r3, 58, 50, -3.0F, -0.5F, 0.0F, 3, 7, 0, 0.0F, false));

		this.head_r4 = new ModelRenderer(this);
		this.head_r4.setRotationPoint(-3.0F, -5.5F, -2.5F);
		this.head.addChild(head_r4);
		this.setRotationAngle(head_r4, -0.2618F, 0.7854F, 0.0873F);
		this.head_r4.cubeList.add(new ModelBox(head_r4, 58, 50, -3.0F, -0.5F, 0.0F, 3, 7, 0, 0.0F, false));

		this.horn2 = new ModelRenderer(this);
		this.horn2.setRotationPoint(2.0F, -8.0F, -1.0F);
		this.head.addChild(horn2);
		this.setRotationAngle(horn2, 0.5672F, 0.4363F, 0.1745F);
		this.horn2.cubeList.add(new ModelBox(horn2, 0, 57, 0.0F, 0.0F, -1.0F, 2, 3, 4, 0.0F, true));

		this.hornextra = new ModelRenderer(this);
		this.hornextra.setRotationPoint(1.0F, 0.0F, 3.0F);
		this.horn2.addChild(hornextra);
		this.setRotationAngle(hornextra, -0.6109F, 0.0F, 0.0F);
		this.hornextra.cubeList.add(new ModelBox(hornextra, 12, 55, 0.0F, 0.0F, 0.0F, 0, 3, 6, 0.0F, true));

		this.horn = new ModelRenderer(this);
		this.horn.setRotationPoint(-2.0F, -8.0F, -1.0F);
		this.head.addChild(horn);
		this.setRotationAngle(horn, 0.5672F, -0.4363F, -0.1745F);
		this.horn.cubeList.add(new ModelBox(horn, 0, 57, -2.0F, 0.0F, -1.0F, 2, 3, 4, 0.0F, false));

		this.hornextra2 = new ModelRenderer(this);
		this.hornextra2.setRotationPoint(-1.0F, 0.0F, 3.0F);
		this.horn.addChild(hornextra2);
		this.setRotationAngle(hornextra2, -0.6109F, 0.0F, 0.0F);
		this.hornextra2.cubeList.add(new ModelBox(hornextra2, 12, 55, 0.0F, 0.0F, 0.0F, 0, 3, 6, 0.0F, false));

		this.eyeright = new ModelRenderer(this);
		this.eyeright.setRotationPoint(-2.0F, -4.0F, -4.5F);
		this.head.addChild(eyeright);
		this.setRotationAngle(eyeright, 0.0F, 0.1745F, 0.0F);
		this.eyeright.cubeList.add(new ModelBox(eyeright, 0, 49, -3.0F, -1.0F, 0.0F, 3, 3, 1, 0.0F, false));

		this.eyeright_r1 = new ModelRenderer(this);
		this.eyeright_r1.setRotationPoint(-1.5F, 0.5F, 0.5F);
		this.eyeright.addChild(eyeright_r1);
		this.setRotationAngle(eyeright_r1, 0.0F, 0.0F, 0.7854F);
		this.eyeright_r1.cubeList.add(new ModelBox(eyeright_r1, 0, 53, -0.5F, -1.5F, -0.6F, 1, 3, 1, 0.0F, false));

		this.eyeright_r2 = new ModelRenderer(this);
		this.eyeright_r2.setRotationPoint(-1.5F, 0.5F, 0.5F);
		this.eyeright.addChild(eyeright_r2);
		this.setRotationAngle(eyeright_r2, 0.0F, 0.0F, -0.7854F);
		this.eyeright_r2.cubeList.add(new ModelBox(eyeright_r2, 0, 53, -0.5F, -1.5F, -0.7F, 1, 3, 1, 0.0F, false));

		this.eyeleft = new ModelRenderer(this);
		this.eyeleft.setRotationPoint(2.0F, -4.0F, -4.5F);
		this.head.addChild(eyeleft);
		this.setRotationAngle(eyeleft, 0.0F, -0.1745F, 0.0F);
		this.eyeleft.cubeList.add(new ModelBox(eyeleft, 0, 49, 0.0F, -1.0F, 0.0F, 3, 3, 1, 0.0F, false));

		this.eyeleft_r1 = new ModelRenderer(this);
		this.eyeleft_r1.setRotationPoint(1.5F, 0.5F, 0.5F);
		this.eyeleft.addChild(eyeleft_r1);
		this.setRotationAngle(eyeleft_r1, 0.0F, 0.0F, -0.7854F);
		this.eyeleft_r1.cubeList.add(new ModelBox(eyeleft_r1, 0, 53, -0.5F, -1.5F, -0.6F, 1, 3, 1, 0.0F, false));

		this.eyeleft_r2 = new ModelRenderer(this);
		this.eyeleft_r2.setRotationPoint(1.5F, 0.5F, 0.5F);
		this.eyeleft.addChild(eyeleft_r2);
		this.setRotationAngle(eyeleft_r2, 0.0F, 0.0F, 0.7854F);
		this.eyeleft_r2.cubeList.add(new ModelBox(eyeleft_r2, 0, 53, -0.5F, -1.5F, -0.7F, 1, 3, 1, 0.0F, false));

		this.ponytail = new ModelRenderer(this);
		this.ponytail.setRotationPoint(0.0F, -2.0F, 4.0F);
		this.head.addChild(ponytail);
		this.setRotationAngle(ponytail, 0.1745F, 0.0F, 0.0F);
		this.ponytail.cubeList.add(new ModelBox(ponytail, 44, 38, -2.0F, 0.0F, 0.0F, 4, 11, 0, 0.0F, false));

		this.earright = new ModelRenderer(this);
		this.earright.setRotationPoint(-4.0F, -4.0F, 0.0F);
		this.head.addChild(earright);
		this.setRotationAngle(earright, 0.0F, 0.2182F, -0.1745F);
		this.earright.cubeList.add(new ModelBox(earright, 24, 3, -2.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F, false));

		this.earleft = new ModelRenderer(this);
		this.earleft.setRotationPoint(4.0F, -4.0F, 0.0F);
		this.head.addChild(earleft);
		this.setRotationAngle(earleft, 0.0F, -0.2182F, 0.1745F);
		this.earleft.cubeList.add(new ModelBox(earleft, 24, 0, -1.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F, false));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 17, 16, -3.5F, 8.0F, -2.0F, 7, 8, 4, 0.0F, false));
		this.body.cubeList.add(new ModelBox(body, 24, 28, -1.5F, 13.0F, 2.0F, 3, 3, 1, 0.0F, false));

		this.collar = new ModelRenderer(this);
		this.collar.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.collar.cubeList.add(new ModelBox(collar, 17, 32, -4.0F, 8.0F, -2.0F, 8, 8, 4, 0.1F, false));

		this.leftarm = new ModelRenderer(this);
		this.leftarm.setRotationPoint(3.5F, 10.0F, 0.0F);
		this.leftarm.cubeList.add(new ModelBox(leftarm, 40, 16, 0.0F, -2.0F, -2.0F, 3, 9, 4, 0.0F, true));

		this.rightarm = new ModelRenderer(this);
		this.rightarm.setRotationPoint(-3.5F, 10.0F, 0.0F);
		this.rightarm.cubeList.add(new ModelBox(rightarm, 40, 16, -3.0F, -2.0F, -2.0F, 3, 9, 4, 0.0F, false));

		this.leftleg = new ModelRenderer(this);
		this.leftleg.setRotationPoint(2.0F, 16.0F, 0.0F);
		this.leftleg.cubeList.add(new ModelBox(leftleg, 0, 16, -1.5F, 0.0F, -2.0F, 3, 8, 4, 0.0F, true));

		this.rightleg = new ModelRenderer(this);
		this.rightleg.setRotationPoint(-2.0F, 16.0F, 0.0F);
		this.rightleg.cubeList.add(new ModelBox(rightleg, 0, 16, -1.5F, 0.0F, -2.0F, 3, 8, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		collar.render(f5);
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

	@Override
	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
		this.head.rotationPointY = 8.0F;
		this.rightarm.rotationPointY = 10.0F;
		this.leftarm.rotationPointY = 10.0F;

		this.leftleg.rotationPointY = 16.0F;
		this.leftleg.rotationPointZ = 0.0F;
		this.rightleg.rotationPointY = 16.0F;
		this.rightleg.rotationPointZ = 0.0F;

		this.leftleg.rotateAngleY = 0.0F;
		this.rightleg.rotateAngleY = 0.0F;
		this.leftleg.rotateAngleZ = 0.0F;
		this.rightleg.rotateAngleZ = 0.0F;

		this.rightarm.rotateAngleY = 0.0F;
		this.leftarm.rotateAngleY = 0.0F;
		this.rightarm.rotateAngleZ = 0.0F;
		this.leftarm.rotateAngleZ = 0.0F;

		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;

		boolean hasBow = false;
		boolean hasShield = false;
		boolean isBowAiming = false;
		boolean isBlocking = false;

		if (entityIn instanceof EntityToyTeddyQilin) {
			EntityToyTeddyQilin teddy = (EntityToyTeddyQilin) entityIn;
			hasBow = teddy.getHeldItemMainhand().getItem() instanceof ItemBow;
			hasShield = teddy.getHeldItemOffhand().getItem() instanceof ItemShield;
			isBowAiming = teddy.isAiming();
			isBlocking = hasShield && teddy.isHandActive() && teddy.getActiveHand() == EnumHand.OFF_HAND;
		}

		this.leftleg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightleg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;

		if (hasBow && isBowAiming) {
			this.rightarm.rotateAngleX = -1.5707963F + this.head.rotateAngleX;
			this.rightarm.rotateAngleY = -0.34906585F + this.head.rotateAngleY;

			this.leftarm.rotateAngleX = -1.5707963F + this.head.rotateAngleX;
			this.leftarm.rotateAngleY = 0.9599311F + this.head.rotateAngleY;
		}

		if (isBlocking) {
			this.leftarm.rotateAngleX = -0.6981317F;
			this.leftarm.rotateAngleY = 0.5235988F;
			this.leftarm.rotateAngleZ = 0.0F;
		}
	}

	@Override
	public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
		final EntityToyTeddyQilin entity = (EntityToyTeddyQilin) entitylivingbaseIn;
		final int attackTimer = entity.getAttackTimer();
		boolean hasBow = entity.getHeldItemMainhand().getItem() instanceof ItemBow;
		boolean hasShield = entity.getHeldItemOffhand().getItem() instanceof ItemShield;
		boolean isBowAiming = entity.isAiming();
		boolean isBlocking = hasShield && entity.isHandActive() && entity.getActiveHand() == EnumHand.OFF_HAND;
		float idleTime = entity.ticksExisted + partialTickTime;
		float idleFade = MathHelper.clamp(1.0f - limbSwingAmount * 3.0f, 0.0f, 1.0f);
		float idleSway = MathHelper.sin(idleTime * 0.05f) * 0.05f * idleFade;
		float idleBob = (MathHelper.cos(idleTime * 0.045f) * 0.5f + 0.5f) * 0.03f * idleFade;

		if (!isBlocking) {
			this.leftarm.rotateAngleX = (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
					+ idleSway + idleBob;
		}

		if (attackTimer > 0 && !hasBow) {
			this.rightarm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(attackTimer - partialTickTime, 10.0f);
		} else if (!hasBow || !isBowAiming) {
			this.rightarm.rotateAngleX = (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
					- idleSway + idleBob;
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