package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyKitsune; // Corregido el import para tu Kitsune

public class ModelToyKitsune extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer leftear;
	public ModelRenderer rightear;
	public ModelRenderer body;
	public ModelRenderer leg1;
	public ModelRenderer leg2;
	public ModelRenderer leg3;
	public ModelRenderer leg4;
	public ModelRenderer righttail;
	public ModelRenderer righttailend;
	public ModelRenderer middletail;
	public ModelRenderer middletailtailend;
	public ModelRenderer lefttail;
	public ModelRenderer lefttailtailend;

	public ModelToyKitsune() {
		textureWidth = 64;
		textureHeight = 64;

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, 18.0F, -3.0F);
		this.head.cubeList.add(new ModelBox(head, 12, 0, -3.5F, -4.5F, -5.0F, 7, 6, 5, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 35, 0, -1.5F, -1.5F, -7.0F, 3, 3, 2, 0.0F, false));

		this.leftear = new ModelRenderer(this);
		this.leftear.setRotationPoint(1.5F, -4.5F, -3.0F);
		this.head.addChild(leftear);
		this.setRotationAngle(leftear, 0.0F, -0.2182F, 0.3054F);
		this.leftear.cubeList.add(new ModelBox(leftear, 0, 6, 0.0F, -2.0F, 0.0F, 3, 3, 1, 0.0F, false));
		this.leftear.cubeList.add(new ModelBox(leftear, 6, 0, 0.5F, -3.0F, 0.0F, 2, 1, 1, 0.0F, false));

		this.rightear = new ModelRenderer(this);
		this.rightear.setRotationPoint(-1.5F, -4.5F, -3.0F);
		this.head.addChild(rightear);
		this.setRotationAngle(rightear, 0.0F, 0.2182F, -0.3054F);
		this.rightear.cubeList.add(new ModelBox(rightear, 0, 2, -3.0F, -2.0F, 0.0F, 3, 3, 1, 0.0F, false));
		this.rightear.cubeList.add(new ModelBox(rightear, 0, 0, -2.5F, -3.0F, 0.0F, 2, 1, 1, 0.0F, false));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 18.0F, -3.0F);
		this.body.cubeList.add(new ModelBox(body, 0, 11, -2.5F, -2.0F, -1.0F, 5, 4, 9, 0.0F, false));
		this.body.cubeList.add(new ModelBox(body, 28, 11, -2.5F, -2.0F, -1.0F, 5, 4, 9, 0.2F, false));

		this.leg1 = new ModelRenderer(this);
		this.leg1.setRotationPoint(-2.0F, 19.0F, 3.0F);
		this.leg1.cubeList.add(new ModelBox(leg1, 16, 24, -0.51F, -1.0F, -1.0F, 2, 6, 2, 0.0F, false));

		this.leg2 = new ModelRenderer(this);
		this.leg2.setRotationPoint(2.0F, 19.0F, 3.0F);
		this.leg2.cubeList.add(new ModelBox(leg2, 4, 24, -1.49F, -1.0F, -1.0F, 2, 6, 2, 0.0F, false));

		this.leg3 = new ModelRenderer(this);
		this.leg3.setRotationPoint(-2.0F, 19.0F, -2.0F);
		this.leg3.cubeList.add(new ModelBox(leg3, 16, 24, -0.51F, -1.0F, -1.0F, 2, 6, 2, 0.0F, false));

		this.leg4 = new ModelRenderer(this);
		this.leg4.setRotationPoint(2.0F, 19.0F, -2.0F);
		this.leg4.cubeList.add(new ModelBox(leg4, 4, 24, -1.49F, -1.0F, -1.0F, 2, 6, 2, 0.0F, false));

		this.righttail = new ModelRenderer(this);
		this.righttail.setRotationPoint(-1.0F, 17.0F, 4.0F);
		this.setRotationAngle(righttail, 0.4363F, -0.4363F, 0.0F);
		this.righttail.cubeList.add(new ModelBox(righttail, 0, 43, -1.0F, -1.0F, 0.0F, 2, 2, 3, 0.0F, false));

		this.righttailend = new ModelRenderer(this);
		this.righttailend.setRotationPoint(0.0F, 0.0F, 4.0F);
		this.righttail.addChild(righttailend);
		this.righttailend.cubeList.add(new ModelBox(righttailend, 3, 35, -1.5F, -1.5F, -2.0F, 3, 3, 5, 0.0F, false));
		this.righttailend.cubeList.add(new ModelBox(righttailend, 0, 36, -1.0F, -1.0F, 3.0F, 2, 2, 2, 0.0F, false));

		this.middletail = new ModelRenderer(this);
		this.middletail.setRotationPoint(0.0F, 17.0F, 4.0F);
		this.setRotationAngle(middletail, 0.7854F, 0.0F, 0.0F);
		this.middletail.cubeList.add(new ModelBox(middletail, 21, 43, -1.0F, -1.0F, 0.0F, 2, 2, 3, 0.0F, false));

		this.middletailtailend = new ModelRenderer(this);
		this.middletailtailend.setRotationPoint(0.0F, 0.0F, 4.0F);
		this.middletail.addChild(middletailtailend);
		this.middletailtailend.cubeList.add(new ModelBox(middletailtailend, 21, 35, -1.5F, -1.5F, -2.0F, 3, 3, 5, 0.0F, false));
		this.middletailtailend.cubeList.add(new ModelBox(middletailtailend, 18, 36, -1.0F, -1.0F, 3.0F, 2, 2, 2, 0.0F, false));

		this.lefttail = new ModelRenderer(this);
		this.lefttail.setRotationPoint(1.0F, 17.0F, 4.0F);
		this.setRotationAngle(lefttail, 0.4363F, 0.4363F, 0.0F);
		this.lefttail.cubeList.add(new ModelBox(lefttail, 0, 43, -1.0F, -1.0F, 0.0F, 2, 2, 3, 0.0F, true));

		this.lefttailtailend = new ModelRenderer(this);
		this.lefttailtailend.setRotationPoint(0.0F, 0.0F, 4.0F);
		this.lefttail.addChild(lefttailtailend);
		this.lefttailtailend.cubeList.add(new ModelBox(lefttailtailend, 3, 35, -1.5F, -1.5F, -2.0F, 3, 3, 5, 0.0F, true));
		this.lefttailtailend.cubeList.add(new ModelBox(lefttailtailend, 0, 36, -1.0F, -1.0F, 3.0F, 2, 2, 2, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		head.render(f5);
		body.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
		righttail.render(f5);
		middletail.render(f5);
		lefttail.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);

		if (entitylivingbaseIn instanceof EntityToyKitsune) {
			EntityToyKitsune kitsune = (EntityToyKitsune) entitylivingbaseIn;

			if (kitsune.isSitting()) {
				this.body.rotateAngleX = -32.0F * 0.017453292F;

				this.leg1.rotateAngleX = -90.0F * 0.017453292F;
				this.leg1.rotateAngleY = 40.0F * 0.017453292F;

				this.leg2.rotateAngleX = -90.0F * 0.017453292F;
				this.leg2.rotateAngleY = -40.0F * 0.017453292F;

				this.leg3.rotateAngleX = 0.0F;
				this.leg3.rotateAngleY = 0.0F;
				this.leg4.rotateAngleX = 0.0F;
				this.leg4.rotateAngleY = 0.0F;

				this.leg1.rotationPointY = 19.0F - (-4.0F);
				this.leg2.rotationPointY = 19.0F - (-4.0F);

				this.lefttail.rotationPointY = 17.0F - (-4.0F);
				this.righttail.rotationPointY = 17.0F - (-4.0F);
				this.middletail.rotationPointY = 17.0F - (-4.0F);

			} else {
				this.body.rotateAngleX = 0.0F;

				this.leg1.rotationPointY = 19.0F;
				this.leg1.rotateAngleY = 0.0F;

				this.leg2.rotationPointY = 19.0F;
				this.leg2.rotateAngleY = 0.0F;

				this.lefttail.rotationPointY = 17.0F;
				this.righttail.rotationPointY = 17.0F;
				this.middletail.rotationPointY = 17.0F;
			}
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;

		boolean isSitting = false;
		if (entityIn instanceof EntityToyKitsune) {
			isSitting = ((EntityToyKitsune) entityIn).isSitting();
		}

		if (!isSitting) {
			this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F) * 1.2F * limbSwingAmount;
			this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F + (float) Math.PI) * 1.2F * limbSwingAmount;
			this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F + (float) Math.PI) * 1.2F * limbSwingAmount;
			this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F) * 1.2F * limbSwingAmount;
		}

		float tailIdleSpeed = ageInTicks * 0.1F;

		if (isSitting) {
			float sitOffset = 32.0F * 0.017453292F;

			this.middletail.rotateAngleX = 0.7854F + sitOffset + MathHelper.sin(tailIdleSpeed) * 0.15F;
			this.middletailtailend.rotateAngleX = MathHelper.sin(tailIdleSpeed - 0.5F) * 0.1F;

			this.righttail.rotateAngleY = -0.4363F + MathHelper.cos(tailIdleSpeed) * 0.2F;
			this.righttail.rotateAngleX = 0.4363F + sitOffset + MathHelper.sin(tailIdleSpeed) * 0.1F;
			this.righttailend.rotateAngleY = MathHelper.cos(tailIdleSpeed - 0.5F) * 0.1F;

			this.lefttail.rotateAngleY = 0.4363F - MathHelper.cos(tailIdleSpeed) * 0.2F;
			this.lefttail.rotateAngleX = 0.4363F + sitOffset + MathHelper.sin(tailIdleSpeed) * 0.1F;
			this.lefttailtailend.rotateAngleY = -MathHelper.cos(tailIdleSpeed - 0.5F) * 0.1F;
		} else {
			this.middletail.rotateAngleX = 0.7854F + MathHelper.sin(tailIdleSpeed) * 0.15F;
			this.middletailtailend.rotateAngleX = MathHelper.sin(tailIdleSpeed - 0.5F) * 0.1F;

			this.righttail.rotateAngleY = -0.4363F + MathHelper.cos(tailIdleSpeed) * 0.2F;
			this.righttail.rotateAngleX = 0.4363F + MathHelper.sin(tailIdleSpeed) * 0.1F;
			this.righttailend.rotateAngleY = MathHelper.cos(tailIdleSpeed - 0.5F) * 0.1F;

			this.lefttail.rotateAngleY = 0.4363F - MathHelper.cos(tailIdleSpeed) * 0.2F;
			this.lefttail.rotateAngleX = 0.4363F + MathHelper.sin(tailIdleSpeed) * 0.1F;
			this.lefttailtailend.rotateAngleY = -MathHelper.cos(tailIdleSpeed - 0.5F) * 0.1F;
		}
	}
}