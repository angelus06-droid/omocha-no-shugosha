package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.MathHelper;

public class ModelToyTyrannosaur extends ModelBase {
	public ModelRenderer rightleg;
	public ModelRenderer rightlegmain;
	public ModelRenderer rightleglower;
	public ModelRenderer rightlegfoot;
	public ModelRenderer leftleg;
	public ModelRenderer leftlegmain;
	public ModelRenderer leftleglower;
	public ModelRenderer leftlegfoot;
	public ModelRenderer body;
	public ModelRenderer bodyfront;
	public ModelRenderer tail;
	public ModelRenderer bone;
	public ModelRenderer rightarm;
	public ModelRenderer rightarmlower;
	public ModelRenderer leftarm;
	public ModelRenderer leftarmlower;
	public ModelRenderer neck;
	public ModelRenderer head;
	public ModelRenderer mouth;

	public ModelToyTyrannosaur() {
		textureWidth = 64;
		textureHeight = 64;

		this.rightleg = new ModelRenderer(this);
		this.rightleg.setRotationPoint(-3.0F, 14.0F, 3.0F);


		this.rightlegmain = new ModelRenderer(this);
		this.rightlegmain.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightleg.addChild(rightlegmain);
		this.setRotationAngle(rightlegmain, -0.2618F, 0.0F, 0.0F);
		this.rightlegmain.cubeList.add(new ModelBox(rightlegmain, 0, 12, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		this.rightleglower = new ModelRenderer(this);
		this.rightleglower.setRotationPoint(-1.0F, 2.0F, 1.0F);
		this.rightlegmain.addChild(rightleglower);
		this.setRotationAngle(rightleglower, 0.1745F, 0.0F, 0.0F);
		this.rightleglower.cubeList.add(new ModelBox(rightleglower, 0, 22, -1.5F, 0.0F, -1.0F, 3, 7, 3, 0.0F, false));

		this.rightlegfoot = new ModelRenderer(this);
		this.rightlegfoot.setRotationPoint(0.0F, 6.0F, -1.0F);
		this.rightleglower.addChild(rightlegfoot);
		this.setRotationAngle(rightlegfoot, 0.0873F, 0.0F, 0.0F);
		this.rightlegfoot.cubeList.add(new ModelBox(rightlegfoot, 0, 32, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));

		this.leftleg = new ModelRenderer(this);
		this.leftleg.setRotationPoint(3.0F, 14.0F, 3.0F);


		this.leftlegmain = new ModelRenderer(this);
		this.leftlegmain.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftleg.addChild(leftlegmain);
		this.setRotationAngle(leftlegmain, -0.2618F, 0.0F, 0.0F);
		this.leftlegmain.cubeList.add(new ModelBox(leftlegmain, 0, 12, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));

		this.leftleglower = new ModelRenderer(this);
		this.leftleglower.setRotationPoint(1.0F, 2.0F, 1.0F);
		this.leftlegmain.addChild(leftleglower);
		this.setRotationAngle(leftleglower, 0.1745F, 0.0F, 0.0F);
		this.leftleglower.cubeList.add(new ModelBox(leftleglower, 0, 22, -1.5F, 0.0F, -1.0F, 3, 7, 3, 0.0F, true));

		this.leftlegfoot = new ModelRenderer(this);
		this.leftlegfoot.setRotationPoint(0.0F, 6.0F, -1.0F);
		this.leftleglower.addChild(leftlegfoot);
		this.setRotationAngle(leftlegfoot, 0.0873F, 0.0F, 0.0F);
		this.leftlegfoot.cubeList.add(new ModelBox(leftlegfoot, 0, 32, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, true));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 13.0F, 3.0F);
		this.setRotationAngle(body, -0.0873F, 0.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 16, 14, -4.0F, -4.0F, -4.0F, 8, 7, 8, 0.0F, false));

		this.bodyfront = new ModelRenderer(this);
		this.bodyfront.setRotationPoint(0.0F, -4.0F, -4.0F);
		this.body.addChild(bodyfront);
		this.setRotationAngle(bodyfront, 0.1745F, 0.0F, 0.0F);
		this.bodyfront.cubeList.add(new ModelBox(bodyfront, 16, 0, -3.0F, 0.0F, -8.0F, 6, 6, 8, 0.0F, false));
		this.bodyfront.cubeList.add(new ModelBox(bodyfront, 36, 29, -3.0F, 0.0F, -8.0F, 6, 6, 8, 0.2F, false));

		this.tail = new ModelRenderer(this);
		this.tail.setRotationPoint(0.0F, -2.0F, 4.0F);
		this.body.addChild(tail);
		this.setRotationAngle(tail, 0.0873F, 0.0F, 0.0F);
		this.tail.cubeList.add(new ModelBox(tail, 28, 52, -2.0F, -2.0F, -1.0F, 4, 4, 8, 0.0F, false));

		this.bone = new ModelRenderer(this);
		this.bone.setRotationPoint(0.0F, -2.0F, 7.0F);
		this.tail.addChild(bone);
		this.setRotationAngle(bone, 0.1745F, 0.0436F, 0.0F);
		this.bone.cubeList.add(new ModelBox(bone, 44, 49, -1.0F, 0.0F, -1.0F, 2, 3, 8, 0.0F, false));

		this.rightarm = new ModelRenderer(this);
		this.rightarm.setRotationPoint(-3.0F, 13.0F, -6.0F);
		this.setRotationAngle(rightarm, 0.3491F, 0.0F, 0.0436F);
		this.rightarm.cubeList.add(new ModelBox(rightarm, 0, 0, -1.0F, -1.0F, -1.0F, 2, 4, 2, 0.05F, false));

		this.rightarmlower = new ModelRenderer(this);
		this.rightarmlower.setRotationPoint(0.0F, 3.0F, 1.0F);
		this.rightarm.addChild(rightarmlower);
		this.setRotationAngle(rightarmlower, -0.48F, 0.0F, 0.0F);
		this.rightarmlower.cubeList.add(new ModelBox(rightarmlower, 0, 6, -1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, false));

		this.leftarm = new ModelRenderer(this);
		this.leftarm.setRotationPoint(3.0F, 13.0F, -6.0F);
		this.setRotationAngle(leftarm, 0.3491F, 0.0F, -0.0436F);
		this.leftarm.cubeList.add(new ModelBox(leftarm, 0, 0, -1.0F, -1.0F, -1.0F, 2, 4, 2, 0.05F, true));

		this.leftarmlower = new ModelRenderer(this);
		this.leftarmlower.setRotationPoint(0.0F, 3.0F, 1.0F);
		this.leftarm.addChild(leftarmlower);
		this.setRotationAngle(leftarmlower, -0.48F, 0.0F, 0.0F);
		this.leftarmlower.cubeList.add(new ModelBox(leftarmlower, 0, 6, -1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, true));

		this.neck = new ModelRenderer(this);
		this.neck.setRotationPoint(0.0F, 12.0F, -8.0F);
		this.setRotationAngle(neck, -0.5236F, 0.0F, 0.0F);
		this.neck.cubeList.add(new ModelBox(neck, 18, 38, -2.0F, -2.5F, -4.0F, 4, 4, 6, 0.0F, false));

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, -1.0F, -4.0F);
		this.neck.addChild(head);
		this.setRotationAngle(head, 0.6109F, 0.0F, 0.0F);
		this.head.cubeList.add(new ModelBox(head, 0, 42, -2.5F, -2.0F, -3.0F, 5, 5, 4, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 0, 51, -1.5F, -2.0F, -7.0F, 3, 3, 4, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 10, 48, -1.5F, 1.0F, -7.0F, 0, 1, 4, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 10, 48, 1.5F, 1.0F, -7.0F, 0, 1, 4, 0.0F, false));
		this.head.cubeList.add(new ModelBox(head, 11, 54, -1.5F, 1.0F, -7.0F, 3, 1, 0, 0.0F, false));

		this.mouth = new ModelRenderer(this);
		this.mouth.setRotationPoint(0.0F, 1.0F, -3.0F);
		this.head.addChild(mouth);
		this.mouth.cubeList.add(new ModelBox(mouth, 0, 59, -1.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.rightleg.render(f5);
		this.leftleg.render(f5);
		this.body.render(f5);
		this.rightarm.render(f5);
		this.leftarm.render(f5);
		this.neck.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		this.rightleg.rotateAngleX = MathHelper.cos(limbSwing * 0.7772F) * 1.0F * limbSwingAmount;
		this.leftleg.rotateAngleX = MathHelper.cos(limbSwing * 0.7772F + (float)Math.PI) * 1.0F * limbSwingAmount;

		float headYawRad = netHeadYaw * 0.017453292F;
		float headPitchRad = headPitch * 0.017453292F;
		this.neck.rotateAngleY = headYawRad * 0.6F;
		this.neck.rotateAngleX = -0.5236F + (headPitchRad * 0.5F);
		this.head.rotateAngleY = headYawRad * 0.4F;
		this.head.rotateAngleX = 0.6109F + (headPitchRad * 0.5F);

		float tailSwing = MathHelper.cos(ageInTicks * 0.1F) * 0.15F;
		this.tail.rotateAngleY = tailSwing;
		this.bone.rotateAngleY = tailSwing * 1.2F;

		float baseArmX = 0.3491F;

		float walkArmSwing = MathHelper.cos(limbSwing * 0.7772F) * 0.4F * limbSwingAmount;

		float idleArmSwing = MathHelper.sin(ageInTicks * 0.15F) * 0.03F;

		this.rightarm.rotateAngleX = baseArmX - walkArmSwing + idleArmSwing;
		this.leftarm.rotateAngleX = baseArmX + walkArmSwing - idleArmSwing;

		this.rightarm.rotateAngleZ = 0.0436F + (MathHelper.abs(MathHelper.sin(limbSwing * 0.7772F)) * 0.1F * limbSwingAmount);
		this.leftarm.rotateAngleZ = -0.0436F - (MathHelper.abs(MathHelper.sin(limbSwing * 0.7772F)) * 0.1F * limbSwingAmount);
	}
}