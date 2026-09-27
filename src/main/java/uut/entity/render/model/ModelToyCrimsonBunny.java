package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyCrimsonBunny;

public class ModelToyCrimsonBunny extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer bodyMain;
	private final ModelRenderer leftLeg;
	private final ModelRenderer leftLegMain;
	private final ModelRenderer rightLeg;
	private final ModelRenderer rightLegMain;
	private final ModelRenderer leftLegFront;
	private final ModelRenderer rightLegFront;
	private final ModelRenderer tail;
	private final ModelRenderer head;
	private final ModelRenderer head_r1;
	private final ModelRenderer head_r2;
	private final ModelRenderer head_r3;
	private final ModelRenderer head_r4;
	private final ModelRenderer cube_r1;
	private final ModelRenderer rightEar;
	private final ModelRenderer leftEar;

	private float jumpProgress;

	public ModelToyCrimsonBunny() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 16.0F, -1.5F);


		bodyMain = new ModelRenderer(this);
		bodyMain.setRotationPoint(0.0F, 7.0F, 5.5F);
		body.addChild(bodyMain);
		setRotationAngle(bodyMain, -0.3927F, 0.0F, 0.0F);
		bodyMain.cubeList.add(new ModelBox(bodyMain, 0, 0, -3.0F, -6.0F, -9.0F, 6, 6, 10, 0.0F, false));
		bodyMain.cubeList.add(new ModelBox(bodyMain, 0, 48, -3.0F, -6.0F, -9.0F, 6, 6, 10, 0.2F, false));

		leftLeg = new ModelRenderer(this);
		leftLeg.setRotationPoint(2.0F, 0.0F, 0.0F);
		bodyMain.addChild(leftLeg);
		setRotationAngle(leftLeg, 0.3927F, 0.0F, 0.0F);


		leftLegMain = new ModelRenderer(this);
		leftLegMain.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg.addChild(leftLegMain);
		setRotationAngle(leftLegMain, 0.0F, -0.5236F, 0.0F);
		leftLegMain.cubeList.add(new ModelBox(leftLegMain, 20, 24, -1.0F, 0.0F, -5.0F, 2, 1, 6, 0.0F, true));

		rightLeg = new ModelRenderer(this);
		rightLeg.setRotationPoint(-2.0F, 0.0F, 0.0F);
		bodyMain.addChild(rightLeg);
		setRotationAngle(rightLeg, 0.3927F, 0.0F, 0.0F);


		rightLegMain = new ModelRenderer(this);
		rightLegMain.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg.addChild(rightLegMain);
		setRotationAngle(rightLegMain, 0.0F, 0.5236F, 0.0F);
		rightLegMain.cubeList.add(new ModelBox(rightLegMain, 20, 24, -1.0F, 0.0F, -5.0F, 2, 1, 6, 0.0F, false));

		leftLegFront = new ModelRenderer(this);
		leftLegFront.setRotationPoint(2.0F, 4.0F, -0.5F);
		body.addChild(leftLegFront);
		leftLegFront.cubeList.add(new ModelBox(leftLegFront, 36, 17, -1.1F, -1.0F, -1.0F, 2, 5, 2, 0.0F, true));

		rightLegFront = new ModelRenderer(this);
		rightLegFront.setRotationPoint(-2.0F, 4.0F, -0.5F);
		body.addChild(rightLegFront);
		rightLegFront.cubeList.add(new ModelBox(rightLegFront, 36, 17, -0.9F, -1.0F, -0.9F, 2, 5, 2, 0.0F, false));

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 2.3932F, 7.4217F);
		body.addChild(tail);
		setRotationAngle(tail, -0.3927F, 0.0F, 0.0F);
		tail.cubeList.add(new ModelBox(tail, 20, 16, -2.0F, -3.0084F, -1.0125F, 4, 4, 4, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -1.0F, -1.0F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 16, -2.5F, -3.0F, -4.0F, 5, 5, 5, 0.0F, false));

		head_r1 = new ModelRenderer(this);
		head_r1.setRotationPoint(-1.0F, -1.5F, -3.0F);
		head.addChild(head_r1);
		setRotationAngle(head_r1, -0.2618F, 0.7854F, 0.0873F);
		head_r1.cubeList.add(new ModelBox(head_r1, 58, 7, -2.5F, -0.5F, 0.0F, 3, 5, 0, 0.0F, false));

		head_r2 = new ModelRenderer(this);
		head_r2.setRotationPoint(-2.0F, -0.5F, 0.0F);
		head.addChild(head_r2);
		setRotationAngle(head_r2, -0.3491F, -0.3054F, 0.0873F);
		head_r2.cubeList.add(new ModelBox(head_r2, 58, 0, -2.5F, -0.5F, 0.0F, 3, 5, 0, 0.0F, false));

		head_r3 = new ModelRenderer(this);
		head_r3.setRotationPoint(2.0F, -0.5F, 0.0F);
		head.addChild(head_r3);
		setRotationAngle(head_r3, -0.3491F, 0.3054F, 0.0873F);
		head_r3.cubeList.add(new ModelBox(head_r3, 58, 0, -0.5F, -0.5F, 0.0F, 3, 5, 0, 0.0F, true));

		head_r4 = new ModelRenderer(this);
		head_r4.setRotationPoint(1.0F, -1.5F, -3.0F);
		head.addChild(head_r4);
		setRotationAngle(head_r4, -0.2618F, -0.7854F, -0.0873F);
		head_r4.cubeList.add(new ModelBox(head_r4, 58, 7, -0.5F, -0.5F, 0.0F, 3, 5, 0, 0.0F, true));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(1.5F, -3.0F, -4.0F);
		head.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0436F, 0.0F, -0.3491F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 7, 26, -2.0F, 0.0F, 0.0F, 3, 4, 0, 0.0F, false));

		rightEar = new ModelRenderer(this);
		rightEar.setRotationPoint(-2.0F, -2.0F, 0.0F);
		head.addChild(rightEar);
		setRotationAngle(rightEar, 0.0F, 0.0F, -0.3054F);
		rightEar.cubeList.add(new ModelBox(rightEar, 26, 0, -1.0F, -5.0F, -1.0F, 2, 5, 1, 0.0F, false));

		leftEar = new ModelRenderer(this);
		leftEar.setRotationPoint(2.0F, -2.0F, 0.0F);
		head.addChild(leftEar);
		setRotationAngle(leftEar, 0.0F, 0.0F, 0.3054F);
		leftEar.cubeList.add(new ModelBox(leftEar, 26, 0, -1.0F, -5.0F, -1.0F, 2, 5, 1, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);

		// Obtenemos el tiempo exacto del salto registrado por la entidad
		if (entitylivingbaseIn instanceof EntityToyCrimsonBunny) {
			EntityToyCrimsonBunny bunny = (EntityToyCrimsonBunny) entitylivingbaseIn;
			this.jumpProgress = bunny.getJumpCompletion(partialTickTime);
		} else {
			this.jumpProgress = 0.0F;
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;

		float tailIdleWave = MathHelper.cos(ageInTicks * 0.08F);

		float jumpArc = MathHelper.sin(this.jumpProgress * (float) Math.PI);

		this.body.rotateAngleX = -(jumpArc * -0.45F);
		this.body.rotationPointY = 16.0F - (jumpArc * -3.0F);

		float backLegsAngle = 0.3927F + (jumpArc * 1.05F);
		this.leftLeg.rotateAngleX = backLegsAngle;
		this.rightLeg.rotateAngleX = backLegsAngle;

		float frontLegsAngleX = -jumpArc * 1.2F;
		this.leftLegFront.rotateAngleX = frontLegsAngleX;
		this.rightLegFront.rotateAngleX = frontLegsAngleX;

		float spreadZ = jumpArc * 0.3F;
		this.leftLegFront.rotateAngleZ = -spreadZ;
		this.rightLegFront.rotateAngleZ = spreadZ;

		float earIdle = MathHelper.cos(ageInTicks * 0.08F) * 0.03F;
		float earLag = MathHelper.sin(Math.max(0.0F, this.jumpProgress - 0.12F) * (float) Math.PI) * 0.40F;

		this.rightEar.rotateAngleX = earIdle + earLag;
		this.leftEar.rotateAngleX = earIdle + earLag;

		float tailIdle = tailIdleWave * 0.05F;
		float tailJump = jumpArc * 0.35F;
		this.tail.rotateAngleX = -0.3927F + tailIdle + tailJump;
	}
}