package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.*;

@SideOnly(Side.CLIENT)
public class ModelToySandroneDoll extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer body_r3;
	private final ModelRenderer body_r4;
	private final ModelRenderer rightpower;
	private final ModelRenderer rightpowerback;
	private final ModelRenderer leftpower;
	private final ModelRenderer leftpowerback;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightArmLower;
	private final ModelRenderer rightHand;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftArmLower;
	private final ModelRenderer leftHand;
	private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;

	public ModelToySandroneDoll() {
		this.textureWidth = 128;
		this.textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -1.0F, -1.0F);
		setRotationAngle(head, 0.1309F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -7.0F, -5.0F, 8, 8, 8, 0.0F, false));
		head.cubeList.add(new ModelBox(head, -16, 28, -6.0F, -3.5F, -9.0F, 12, 0, 16, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 16, -4.0F, -8.0F, -5.0F, 8, 4, 8, 0.5F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 11.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 34, 17, -6.0F, -12.0F, -3.0F, 12, 14, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 32, 0, -6.5F, -13.0F, -3.5F, 13, 10, 7, 0.0F, false));

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(0.0F, -10.0F, -4.0F);
		body.addChild(body_r1);
		setRotationAngle(body_r1, 0.0F, 0.0F, -0.7854F);
		body_r1.cubeList.add(new ModelBox(body_r1, 70, 32, -5.0F, -1.0F, 0.45F, 6, 6, 0, 0.0F, false));

		body_r2 = new ModelRenderer(this);
		body_r2.setRotationPoint(-2.0F, 0.0F, -2.0F);
		body.addChild(body_r2);
		setRotationAngle(body_r2, -0.0873F, 0.0F, 0.1309F);
		body_r2.cubeList.add(new ModelBox(body_r2, 72, 0, -4.5F, 0.0F, -1.0F, 5, 10, 4, 0.0F, false));

		body_r3 = new ModelRenderer(this);
		body_r3.setRotationPoint(2.0F, 0.0F, -2.0F);
		body.addChild(body_r3);
		setRotationAngle(body_r3, -0.0873F, 0.0F, -0.1309F);
		body_r3.cubeList.add(new ModelBox(body_r3, 72, 0, -0.5F, 0.0F, -1.0F, 5, 10, 4, 0.0F, true));

		body_r4 = new ModelRenderer(this);
		body_r4.setRotationPoint(0.0F, -1.0F, 3.0F);
		body.addChild(body_r4);
		setRotationAngle(body_r4, 0.2618F, 0.0F, 0.0F);
		body_r4.cubeList.add(new ModelBox(body_r4, 34, 37, -6.5F, 0.0F, -5.0F, 13, 10, 5, 0.0F, false));

		rightpower = new ModelRenderer(this);
		rightpower.setRotationPoint(-2.0F, -11.0F, 2.0F);
		body.addChild(rightpower);
		setRotationAngle(rightpower, -1.0472F, -0.2182F, -0.3054F);
		rightpower.cubeList.add(new ModelBox(rightpower, 70, 24, -3.0F, -4.0F, -1.0F, 4, 4, 4, 0.0F, false));
		rightpower.cubeList.add(new ModelBox(rightpower, 83, 24, -2.5F, -5.0F, -0.5F, 3, 1, 3, 0.0F, false));

		rightpowerback = new ModelRenderer(this);
		rightpowerback.setRotationPoint(-1.0F, -5.0F, 1.0F);
		rightpower.addChild(rightpowerback);
		rightpowerback.cubeList.add(new ModelBox(rightpowerback, 70, 19, -2.0F, -0.5F, -2.0F, 4, 1, 4, 0.0F, false));

		leftpower = new ModelRenderer(this);
		leftpower.setRotationPoint(2.0F, -11.0F, 2.0F);
		body.addChild(leftpower);
		setRotationAngle(leftpower, -1.0472F, 0.2182F, 0.3054F);
		leftpower.cubeList.add(new ModelBox(leftpower, 70, 24, -1.0F, -4.0F, -1.0F, 4, 4, 4, 0.0F, false));
		leftpower.cubeList.add(new ModelBox(leftpower, 83, 24, -0.5F, -5.0F, -0.5F, 3, 1, 3, 0.0F, false));

		leftpowerback = new ModelRenderer(this);
		leftpowerback.setRotationPoint(1.0F, -5.0F, 1.0F);
		leftpower.addChild(leftpowerback);
		leftpowerback.cubeList.add(new ModelBox(leftpowerback, 70, 19, -2.0F, -0.5F, -2.0F, 4, 1, 4, 0.0F, false));

		rightLeg = new ModelRenderer(this);
		rightLeg.setRotationPoint(-3.0F, 13.0F, 0.0F);
		rightLeg.cubeList.add(new ModelBox(rightLeg, 96, 0, -3.0F, 0.0F, -2.5F, 5, 11, 5, 0.0F, false));
		rightLeg.cubeList.add(new ModelBox(rightLeg, 99, 16, -3.0F, 9.0F, -4.5F, 5, 2, 2, 0.0F, false));

		leftLeg = new ModelRenderer(this);
		leftLeg.setRotationPoint(3.0F, 13.0F, 0.0F);
		leftLeg.cubeList.add(new ModelBox(leftLeg, 96, 0, -2.0F, 0.0F, -2.5F, 5, 11, 5, 0.0F, true));
		leftLeg.cubeList.add(new ModelBox(leftLeg, 99, 16, -2.0F, 9.0F, -4.5F, 5, 2, 2, 0.0F, true));

		rightArm = new ModelRenderer(this);
		rightArm.setRotationPoint(-6.0F, 2.0F, 0.0F);
		setRotationAngle(rightArm, 0.0436F, 0.0F, 0.1309F);
		rightArm.cubeList.add(new ModelBox(rightArm, 108, 35, -4.0F, -2.0F, -2.0F, 4, 10, 4, 0.0F, false));
		rightArm.cubeList.add(new ModelBox(rightArm, 105, 22, -5.5F, -3.0F, -2.5F, 6, 8, 5, 0.0F, false));

		rightArmLower = new ModelRenderer(this);
		rightArmLower.setRotationPoint(-2.0F, 7.0F, 0.0F);
		rightArm.addChild(rightArmLower);
		setRotationAngle(rightArmLower, -0.1745F, 0.0F, 0.0F);
		rightArmLower.cubeList.add(new ModelBox(rightArmLower, 104, 49, -2.5F, 0.0F, -3.0F, 5, 7, 6, 0.0F, false));

		rightHand = new ModelRenderer(this);
		rightHand.setRotationPoint(0.0F, 6.0F, -1.0F);
		rightArmLower.addChild(rightHand);
		setRotationAngle(rightHand, 0.0F, 0.0F, -0.0873F);
		rightHand.cubeList.add(new ModelBox(rightHand, 107, 62, -2.0F, -1.0F, -1.0F, 4, 4, 4, 0.0F, false));

		leftArm = new ModelRenderer(this);
		leftArm.setRotationPoint(6.0F, 2.0F, 0.0F);
		setRotationAngle(leftArm, 0.0436F, 0.0F, -0.1309F);
		leftArm.cubeList.add(new ModelBox(leftArm, 108, 35, 0.0F, -2.0F, -2.0F, 4, 10, 4, 0.0F, true));
		leftArm.cubeList.add(new ModelBox(leftArm, 105, 22, -0.5F, -3.0F, -2.5F, 6, 8, 5, 0.0F, true));

		leftArmLower = new ModelRenderer(this);
		leftArmLower.setRotationPoint(2.0F, 7.0F, 0.0F);
		leftArm.addChild(leftArmLower);
		setRotationAngle(leftArmLower, -0.1745F, 0.0F, 0.0F);
		leftArmLower.cubeList.add(new ModelBox(leftArmLower, 104, 49, -2.5F, 0.0F, -3.0F, 5, 7, 6, 0.0F, true));

		leftHand = new ModelRenderer(this);
		leftHand.setRotationPoint(0.0F, 6.0F, -1.0F);
		leftArmLower.addChild(leftHand);
		setRotationAngle(leftHand, 0.0F, 0.0F, 0.0873F);
		leftHand.cubeList.add(new ModelBox(leftHand, 107, 62, -2.0F, -1.0F, -1.0F, 4, 4, 4, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		rightLeg.render(f5);
		leftLeg.render(f5);
		rightArm.render(f5);
		leftArm.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
		EntityToySandroneDoll doll = (EntityToySandroneDoll) entityIn;
		int attackTimer = doll.getAttackTimer();

		this.rightArmLower.rotateAngleX = -0.1745F;
		this.rightArmLower.rotateAngleY = 0.0F;
		this.rightArmLower.rotateAngleZ = 0.0F;

		this.leftArmLower.rotateAngleX = -0.1745F;
		this.leftArmLower.rotateAngleY = 0.0F;
		this.leftArmLower.rotateAngleZ = 0.0F;

		this.leftLeg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightLeg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.leftLeg.rotateAngleY = 0.0f;
		this.rightLeg.rotateAngleY = 0.0f;
		this.leftLeg.rotateAngleZ = 0.0f;
		this.rightLeg.rotateAngleZ = 0.0f;

		this.rightpower.rotateAngleX = -1.0472F;
		this.rightpower.rotateAngleY = -0.2182F;
		this.rightpower.rotateAngleZ = -0.3054F;

		this.leftpower.rotateAngleX = -1.0472F;
		this.leftpower.rotateAngleY = 0.2182F;
		this.leftpower.rotateAngleZ = 0.3054F;

		this.rightpowerback.rotationPointY = -5.0F;
		this.leftpowerback.rotationPointY = -5.0F;

		this.head.rotateAngleY = netHeadYaw * DEG_TO_RAD;
		this.head.rotateAngleX = headPitch * DEG_TO_RAD;

		float powerSpeed = 0.15F;
		float powerIntensity = 0.1F;
		this.rightpower.rotateAngleX += MathHelper.sin(ageInTicks * powerSpeed) * powerIntensity;
		this.leftpower.rotateAngleX += MathHelper.cos(ageInTicks * powerSpeed) * powerIntensity;

		float pistonSpeed = 0.25F;
		float pistonRange = 0.4F;
		this.rightpowerback.rotationPointY += MathHelper.sin(ageInTicks * pistonSpeed) * pistonRange;
		this.leftpowerback.rotationPointY += MathHelper.cos(ageInTicks * pistonSpeed) * pistonRange;

		float armIdleSpeed = 0.045F;
		float armIdleIntensity = 0.045F;

		this.rightArm.rotateAngleZ = 0.1309F + MathHelper.cos(ageInTicks * armIdleSpeed) * armIdleIntensity;
		this.leftArm.rotateAngleZ = -0.1309F - MathHelper.cos(ageInTicks * armIdleSpeed) * armIdleIntensity;

		this.rightArm.rotateAngleY = MathHelper.sin(ageInTicks * armIdleSpeed * 0.7F) * (armIdleIntensity * 0.5F);
		this.leftArm.rotateAngleY = -MathHelper.sin(ageInTicks * armIdleSpeed * 0.7F) * (armIdleIntensity * 0.5F);

		if (attackTimer <= 0) {
			this.rightArm.rotateAngleX = (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
			this.leftArm.rotateAngleX = (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
		}

		if (doll.isSitting()) {
			this.rightArm.rotateAngleX = -45.0F * DEG_TO_RAD;
			this.rightArm.rotateAngleY = 0.0F;
			this.rightArm.rotateAngleZ = 0.0F;

			this.rightArmLower.rotateAngleX = 0.0F;
			this.rightArmLower.rotateAngleY = -20.0F * DEG_TO_RAD;
			this.rightArmLower.rotateAngleZ = -80.0F * DEG_TO_RAD;
		}

		ItemStack offhandItem = doll.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
		boolean isEating = doll.isEating() && !offhandItem.isEmpty() && offhandItem.getItem() instanceof ItemFood;

		if (isEating) {
			float eatProgress = MathHelper.sin(ageInTicks * 0.6F);

			this.head.rotateAngleX += (5.0F + 5.0F * eatProgress) * DEG_TO_RAD;
			this.head.rotateAngleY = -10.0F * DEG_TO_RAD;

			this.leftArm.rotateAngleX = (-50.0F - 10.0F * eatProgress) * DEG_TO_RAD;
			this.leftArm.rotateAngleY = 20.0F * DEG_TO_RAD;
			this.leftArm.rotateAngleZ = 0.0F;

			this.leftArmLower.rotateAngleX = -40.0F * DEG_TO_RAD;
			this.leftArmLower.rotateAngleZ = 30.0F * DEG_TO_RAD;
		}
	}

	@Override
	public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
		final EntityToySandroneDoll entityirongolem = (EntityToySandroneDoll)entitylivingbaseIn;
		final int i = entityirongolem.getAttackTimer();

		if (i > 0) {
			this.rightArm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
			this.leftArm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
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

	public void postRenderArm(float scale, EnumHandSide side) {
		if (side == EnumHandSide.LEFT) {
			this.leftArm.postRender(scale);
			this.leftArmLower.postRender(scale);
			this.leftHand.postRender(scale);
		} else {
			this.rightArm.postRender(scale);
			this.rightArmLower.postRender(scale);
			this.rightHand.postRender(scale);
		}
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