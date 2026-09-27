package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyPrincessOgre;

public class ModelToyPrincessOgre extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer rightArm;
	private final ModelRenderer leftArm;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;

	public ModelToyPrincessOgre() {
		textureWidth = 64;
		textureHeight = 32;

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 0, 8, -2.0F, -5.0F, -2.0F, 4, 5, 4, 0.0F, false));

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.body.addChild(head);
		this.head.cubeList.add(new ModelBox(head, 0, 0, -1.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		this.rightArm = new ModelRenderer(this);
		this.rightArm.setRotationPoint(-3.0F, -4.0F, 0.0F);
		this.body.addChild(rightArm);
		this.rightArm.cubeList.add(new ModelBox(rightArm, 20, 0, -1.0F, -1.0F, -1.0F, 2, 6, 2, 0.0F, false));
		this.rightArm.cubeList.add(new ModelBox(rightArm, 29, 0, -0.5F, 3.5F, -3.0F, 1, 1, 5, 0.0F, false));
		this.rightArm.cubeList.add(new ModelBox(rightArm, 31, 6, -1.0F, 3.0F, -5.0F, 2, 2, 2, 0.0F, false));

		this.leftArm = new ModelRenderer(this);
		this.leftArm.setRotationPoint(3.0F, -4.0F, 0.0F);
		this.body.addChild(leftArm);
		this.leftArm.cubeList.add(new ModelBox(leftArm, 20, 0, -1.0F, -1.0F, -1.0F, 2, 6, 2, 0.0F, true));

		this.rightLeg = new ModelRenderer(this);
		this.rightLeg.setRotationPoint(-1.0F, 22.0F, 0.0F);
		this.rightLeg.cubeList.add(new ModelBox(rightLeg, 16, 8, -1.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F, false));

		this.leftLeg = new ModelRenderer(this);
		this.leftLeg.setRotationPoint(1.0F, 22.0F, 0.0F);
		this.leftLeg.cubeList.add(new ModelBox(leftLeg, 16, 8, -1.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
		rightLeg.render(f5);
		leftLeg.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
		this.leftLeg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightLeg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.leftLeg.rotateAngleY = 0.0f;
		this.rightLeg.rotateAngleY = 0.0f;
		this.leftLeg.rotateAngleZ = 0.0f;
		this.rightLeg.rotateAngleZ = 0.0f;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		EntityToyPrincessOgre entity = (EntityToyPrincessOgre) entitylivingbaseIn;
		int attackTimer = entity.getAttackTimer();
		float time = entity.ticksExisted + partialTickTime;

		float idleRightX = MathHelper.sin(time * 0.067F) * 0.05F;
		float idleRightZ = MathHelper.cos(time * 0.09F) * 0.05F + 0.05F;
		float idleLeftX = MathHelper.sin(time * 0.067F + 0.5F) * 0.05F;
		float idleLeftZ = MathHelper.cos(time * 0.09F + 0.5F) * 0.05F + 0.05F;

		this.leftArm.rotateAngleX = idleLeftX + (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
		this.leftArm.rotateAngleZ = -idleLeftZ;
		this.leftArm.rotateAngleY = 0.0F;

		if (attackTimer > 0) {
			float progress = attackTimer - partialTickTime;
			float t = progress / 6.0F;
			float swing = MathHelper.sin(t * (float)Math.PI);
			float side  = -MathHelper.sin(t * (float)Math.PI * 2.0F);

			this.rightArm.rotateAngleX = -0.5F - 1.4F * swing;
			this.rightArm.rotateAngleY = side * 0.9F;
			this.rightArm.rotateAngleZ = 0.0F;
		} else {
			this.rightArm.rotateAngleX = idleRightX + (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
			this.rightArm.rotateAngleZ = idleRightZ;
			this.rightArm.rotateAngleY = 0.0F;
		}
	}

	private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
		return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
	}
}