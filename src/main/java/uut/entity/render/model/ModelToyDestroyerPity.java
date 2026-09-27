package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper; // Importante para las funciones seno/coseno

public class ModelToyDestroyerPity extends ModelBase {
	private final ModelRenderer all;
	private final ModelRenderer body;
	private final ModelRenderer tail;
	private final ModelRenderer tailMain;
	private final ModelRenderer tail_r1;
	private final ModelRenderer tail_r2;
	private final ModelRenderer tail_r3;
	private final ModelRenderer neck;
	private final ModelRenderer neckMain;
	private final ModelRenderer neck_r1;
	private final ModelRenderer head;
	private final ModelRenderer headMain;
	private final ModelRenderer mouth;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;

	public ModelToyDestroyerPity() {
		textureWidth = 64;
		textureHeight = 64;

		this.all = new ModelRenderer(this);
		this.all.setRotationPoint(0.0F, 23.0F, 0.0F);

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(-1.0F, -5.0F, 4.0F);
		this.all.addChild(body);
		this.setRotationAngle(body, -0.2182F, 0.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 0, 33, -1.5F, -5.0F, -4.0F, 5, 4, 6, 0.0F, false));
		this.body.cubeList.add(new ModelBox(body, 22, 33, -1.0F, -1.0F, -4.0F, 4, 3, 5, 0.0F, false));

		this.tail = new ModelRenderer(this);
		this.tail.setRotationPoint(1.0F, -5.0F, 2.0F);
		this.body.addChild(tail);
		this.setRotationAngle(tail, -0.2618F, 0.0F, 0.0F);

		this.tailMain = new ModelRenderer(this);
		this.tailMain.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.tail.addChild(tailMain);
		this.tailMain.cubeList.add(new ModelBox(tailMain, 0, 43, -1.5F, -3.0F, 0.0F, 3, 4, 6, 0.0F, false));

		this.tail_r1 = new ModelRenderer(this);
		this.tail_r1.setRotationPoint(0.0F, 0.0F, 6.0F);
		this.tailMain.addChild(tail_r1);
		this.setRotationAngle(tail_r1, 0.0F, -0.7854F, -0.1745F);
		this.tail_r1.cubeList.add(new ModelBox(tail_r1, 26, 46, -3.0F, -1.0F, 0.0F, 3, 0, 6, 0.0F, false));

		this.tail_r2 = new ModelRenderer(this);
		this.tail_r2.setRotationPoint(0.0F, 0.0F, 6.0F);
		this.tailMain.addChild(tail_r2);
		this.setRotationAngle(tail_r2, 0.0F, 0.7854F, 0.1745F);
		this.tail_r2.cubeList.add(new ModelBox(tail_r2, 26, 46, 0.0F, -1.0F, 0.0F, 3, 0, 6, 0.0F, true));

		this.tail_r3 = new ModelRenderer(this);
		this.tail_r3.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.tailMain.addChild(tail_r3);
		this.setRotationAngle(tail_r3, 0.6109F, 0.0F, 0.0F);
		this.tail_r3.cubeList.add(new ModelBox(tail_r3, 18, 41, -1.0F, -2.0F, 0.0F, 2, 3, 5, 0.0F, false));

		this.neck = new ModelRenderer(this);
		this.neck.setRotationPoint(1.0F, -5.0F, -4.0F);
		this.body.addChild(neck);
		this.setRotationAngle(neck, 0.1309F, 0.0F, 0.0F);

		this.neckMain = new ModelRenderer(this);
		this.neckMain.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.neck.addChild(neckMain);
		this.neckMain.cubeList.add(new ModelBox(neckMain, 0, 22, -3.5F, -2.0F, -6.0F, 7, 5, 6, 0.0F, false));

		this.neck_r1 = new ModelRenderer(this);
		this.neck_r1.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.neckMain.addChild(neck_r1);
		this.setRotationAngle(neck_r1, -0.5236F, 0.0F, 0.0F);
		this.neck_r1.cubeList.add(new ModelBox(neck_r1, 20, 22, -2.5F, -1.0F, -4.0F, 5, 2, 4, 0.0F, false));

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, 0.0F, -5.0F);
		this.neckMain.addChild(head);

		this.headMain = new ModelRenderer(this);
		this.headMain.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.addChild(headMain);
		this.setRotationAngle(headMain, 0.1745F, 0.0F, 0.0F);
		this.headMain.cubeList.add(new ModelBox(headMain, 0, 0, -4.0F, -3.0F, -9.0F, 8, 5, 9, 0.0F, false));
		this.headMain.cubeList.add(new ModelBox(headMain, 34, 7, -3.5F, 1.0F, -7.5F, 7, 2, 5, -0.02F, false));

		this.mouth = new ModelRenderer(this);
		this.mouth.setRotationPoint(0.0F, 1.0F, -1.0F);
		this.headMain.addChild(mouth);
		this.setRotationAngle(mouth, 0.6981F, 0.0F, 0.0F);
		this.mouth.cubeList.add(new ModelBox(mouth, 0, 14, -3.0F, 0.0F, -6.0F, 6, 2, 6, 0.0F, false));
		this.mouth.cubeList.add(new ModelBox(mouth, 24, 15, -2.5F, -1.0F, -5.5F, 5, 2, 5, 0.0F, false));

		this.rightLeg = new ModelRenderer(this);
		this.rightLeg.setRotationPoint(-2.0F, -5.0F, 2.5F);
		this.all.addChild(rightLeg);
		this.rightLeg.cubeList.add(new ModelBox(rightLeg, 0, 53, -1.0F, 0.0F, -1.5F, 2, 6, 3, 0.0F, false));

		this.leftLeg = new ModelRenderer(this);
		this.leftLeg.setRotationPoint(2.0F, -5.0F, 2.5F);
		this.all.addChild(leftLeg);
		this.leftLeg.cubeList.add(new ModelBox(leftLeg, 0, 53, -1.0F, 0.0F, -1.5F, 2, 6, 3, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		all.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
		this.leftLeg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightLeg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.leftLeg.rotateAngleY = 0.0f;
		this.rightLeg.rotateAngleY = 0.0f;
		this.leftLeg.rotateAngleZ = 0.0f;
		this.rightLeg.rotateAngleZ = 0.0f;

		float yawRad = netHeadYaw * 0.017453292F;
		float pitchRad = headPitch * 0.017453292F;

		this.neckMain.rotateAngleY = yawRad * 0.3F;
		this.neckMain.rotateAngleX = pitchRad * 0.3F;

		this.head.rotateAngleY = yawRad * 0.7F;
		this.head.rotateAngleX = pitchRad * 0.7F;

		this.neckMain.rotateAngleX += MathHelper.sin(ageInTicks * 0.08F) * 0.03F;

		this.mouth.rotateAngleX = 0.6981F + (MathHelper.cos(ageInTicks * 0.05F) * 0.04F + 0.04F);

		this.tail.rotateAngleX = -0.2618F;

		float tailIdleSpeed = 0.1F;
		float tailWalkSpeed = limbSwing * 0.5F;

		this.tail.rotateAngleY = MathHelper.sin(ageInTicks * tailIdleSpeed + tailWalkSpeed) * (0.15F + limbSwingAmount * 0.25F);
	}

	private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
		return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
	}
}